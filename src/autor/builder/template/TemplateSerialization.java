package autor.builder.template;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import autor.builder.message.ConsoleHelper;
import autor.builder.utils.AutorConstants;
import autor.builder.utils.JsonUtil;
import autor.builder.utils.StringUtils;
import autor.builder.utils.TemplateCompare;

/**
 * 
 * 序列化数据到本地，当xml增量变化时，与原有数据合并<br> 
 *
 * @author shazl
 */
public class TemplateSerialization {
    private TemplateSerialization(){}
    
    public static JavaTemplate serialize(String serilFileName, JavaTemplate newTemplate){
        ConsoleHelper.printInfo("template Serialization");
        TemplateSerialization serialization = new TemplateSerialization();
        String newJson = JsonUtil.toJson(newTemplate);
        try {
            // 获取就序列化文件 
            String oldJson =  serialization.readFile(serilFileName);
            if (StringUtils.isEmpty(oldJson)) {
                // 序列化新数据
                serialization.writeFile(newJson, serilFileName);
                return newTemplate;
            }
            
            // 新旧相同，不再序列化
            if (newJson.equals(oldJson)) {
                return newTemplate;
            }
            
            // 比较模板
            JavaTemplate template = serialization.compare(newJson, oldJson);
            serialization.writeFile(JsonUtil.toJson(template), serilFileName);
            
            return template;
        } catch (Exception e) {
            ConsoleHelper.printError("serialization error :"+e.getMessage());
        }
        
        return newTemplate;
    }
    
    public static void removeAll(String fileName){
        File file = new File(getPath(fileName));
        
        if (file.exists()) {
            file.delete();
        }
    }
    
    public static void remove(String fileName, String key){
    	try {
    		TemplateSerialization serialization = new TemplateSerialization();
			String oldJson =  serialization.readFile(fileName);
			if (StringUtils.isEmpty(oldJson)) {
				return;
			}
			
			JavaTemplate template = JsonUtil.fromJson(oldJson, JavaTemplate.class);
			SubClass subClass = null;
			int flag = -1;
			for (int i = 0; i < template.getSubClasses().size(); i++) {
				subClass = template.getSubClasses().get(i);
				if (subClass.getComment().equals(key)) {
					flag = i;
				}
			}
			
			if (flag != -1) {
				template.getSubClasses().remove(flag);
				// 重写file
				serialization.writeFile(JsonUtil.toJson(template), fileName);
			}
		} catch (Exception e) {
			ConsoleHelper.printError("remove from serilization file error ", e);
		}
    }
    
    public static JavaTemplate getSerilFile(String serilFileName){
    	try {
    		TemplateSerialization serialization = new TemplateSerialization();
			String oldJson =  serialization.readFile(serilFileName);
			if (StringUtils.isEmpty(oldJson)) {
				return null;
			}else {
				return JsonUtil.fromJson(oldJson, JavaTemplate.class);
			}
			
		} catch (Exception e) {
			ConsoleHelper.printError("get serilization file error ", e);
		}
    	
    	return null;
    } 
    /**
     * 功能描述: 读取文件，并转为对象<br>
     *
     * @param fileName
     * @return 
     * @throws IOException
     * @throws FileNotFoundException
     */
    private String readFile(String fileName) throws IOException, FileNotFoundException{
        File file = new File(getPath(fileName));
        if (!file.exists()) {
            return null;
        }
        // 新建流
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),
                AutorConstants.FILE_ENCODE));
        // 存储文件时，只存一行，故此只读取第一行
        String json = reader.readLine();
        // 关闭流
        reader.close();
        
        return json;
    }
    
    private void writeFile(String json, String fileName) throws IOException{
        File file = new File(getPath(fileName));
        ConsoleHelper.printInfo("json file path: "+file.getAbsolutePath());
        
        Writer writer = new FileWriter(file);
        writer.write(json);
        writer.flush();
        
        writer.close();
    }
    
    private JavaTemplate compare(String newTemplateJson, String oldTemplateJson){
        if (StringUtils.isEmpty(oldTemplateJson) || StringUtils.isEmpty(newTemplateJson)
                || oldTemplateJson.equals(newTemplateJson)) {
            return null;
        }
        
        JavaTemplate newTemplate = JsonUtil.fromJson(newTemplateJson, JavaTemplate.class);
        JavaTemplate oldTemplate = JsonUtil.fromJson(oldTemplateJson, JavaTemplate.class);
        
        Map<String, SubClass> oldMap = new HashMap<String, SubClass>();
        Map<String, SubClass> oldFileMap = new HashMap<String, SubClass>();
        
        for (SubClass oldSubClass : oldTemplate.getSubClasses()) {
            oldMap.put(oldSubClass.getSubClassName(), oldSubClass);
            oldFileMap.put(oldSubClass.getComment(), oldSubClass);
        }
        
        List<SubClass> subClasses = new ArrayList<SubClass>();
        SubClass oldSubClass = null;
        SubClass oldSubClassByFileName = null;
        
        for (SubClass newSubClass : newTemplate.getSubClasses()) {
            oldSubClass = oldMap.get(newSubClass.getSubClassName());
            oldSubClassByFileName = oldFileMap.get(newSubClass.getComment());
            
            // 获取发生改变的subclass
            // 1. 没有在map中取到(新增)， 2. 取到，但内容不一致（修改）
            if (oldSubClass == null && oldSubClassByFileName == null) {
                subClasses.add(newSubClass);
                continue;
            }
            
            // 内容改变，但为namespace变化
            if (oldSubClass == null && oldSubClassByFileName != null) {
                subClasses.add(newSubClass);
                oldMap.remove(oldSubClassByFileName.getSubClassName());
                continue;
            }
            
            // 内容改变，namespace没有变化
            if (!TemplateCompare.compareSubclass(oldSubClass, newSubClass)) {
                subClasses.add(newSubClass);
                oldMap.remove(newSubClass.getSubClassName());
            }
        }
        
        // 取出剩余的subClass
        if (!oldMap.isEmpty()) {
            subClasses.addAll(new ArrayList<SubClass>(oldMap.values()));
        }
        
        newTemplate.setSubClasses(subClasses);
        
        return newTemplate;
    }
    
    private static String getPath(String fileName){
        return AutorConstants.FILE_PATH.concat(AutorConstants.SEPARATOR_SLASH).concat(fileName)
                .concat(AutorConstants.FILE_SUFFIX_JSON);
    }
    
}
