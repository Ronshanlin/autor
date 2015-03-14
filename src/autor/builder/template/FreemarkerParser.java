package autor.builder.template;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;

import autor.Activator;
import autor.builder.message.ConsoleHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;

public class FreemarkerParser {
	
    private static Configuration configuration = null;
    private static Template template = null;
    
    public static void init(){
        configuration = new Configuration(new Version("1.0"));
        configuration.setAutoFlush(true);
        configuration.setClassicCompatible(true);
        
        Reader reader = null;
        InputStream inputStream = null;
        try {
        	URL url = Activator.getDefault().getBundle().getResource("/src/javaTemplate.template");
        	if (url != null) {
        	    ConsoleHelper.printInfo("template url: "+url.getPath());
            }
            inputStream = new FileInputStream(FileLocator.toFileURL(url).getPath());
            reader = new InputStreamReader(inputStream, "utf-8");
            template = new Template("javaTemplate", reader, configuration);
        } catch (Exception e) {
        	ConsoleHelper.printError("FreemarkerParser init",e);
        } finally{
            try {
                if (reader != null) {
                   reader.close();
                }
                if(inputStream != null){
                    inputStream.close();
                }
            } catch (IOException e1) {
            	ConsoleHelper.printError(e1.getMessage());
            }
        }
    }
    
    /**
     * 功能描述: <br>
     *
     * @param javaTemplate
     * @return the string that was parsed 
     */
    public static String parse(JavaTemplate javaTemplate){
        Writer out = null;
        try {
            out = new StringWriter();
            
            Map<String, Object> rootMap = new HashMap<String, Object>();
            rootMap.put("packageName", javaTemplate.getPackageName());
            rootMap.put("className", javaTemplate.getClassName());
            rootMap.put("subClasses", javaTemplate.getSubClasses());
            rootMap.put("createTime", new Timestamp(System.currentTimeMillis()));
            template.process(rootMap, out);
            
            return out.toString();
        } catch (Exception e) {
        	ConsoleHelper.printError("freemarker parese error", e);
        }finally{
            try {
                if (out !=null) {
                    out.close();
                }
            } catch (IOException e) {
            	ConsoleHelper.printError("io exception",e);
            }
        }
        
        return null;
    }
}
