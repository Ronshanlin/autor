package autor.builder.xml;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import autor.builder.exception.AutorException;
import autor.builder.message.ConsoleHelper;
import autor.builder.template.JavaTemplate;
import autor.builder.template.SubClass;
import autor.builder.template.TemplateSerialization;
import autor.builder.utils.CollectionUtils;

public class SaxXmlParser {
	private static final String R_CLASS_NAME = "R";
	private static SAXParserFactory parserFactory;
	
	public static JavaTemplate parse(String packageName, List<IFile> files, String projectName) throws AutorException{
	    ConsoleHelper.printInfo("package name:"+packageName);
		if (parserFactory == null) {
			parserFactory = SAXParserFactory.newInstance();
		}
		
        JavaTemplate template = new JavaTemplate();
        template.setClassName(R_CLASS_NAME);
        template.setPackageName(packageName);
		
        return parseFile(files, template, projectName);
	}
	
	private static JavaTemplate parseFile(List<IFile> files, JavaTemplate template, String projectName) throws AutorException{
	    XmlContentHandler contentHandler = null;
		
	    Map<String, String> tempMap = new HashMap<String, String>();
	    
	    // 获取前面已经解析的xml。
	    JavaTemplate oldJavaTemplate = TemplateSerialization.getSerilFile(projectName);
	    if (oldJavaTemplate != null && CollectionUtils.isNotEmpty(oldJavaTemplate.getSubClasses())) {
			for (SubClass subClass : oldJavaTemplate.getSubClasses()) {
				tempMap.put(subClass.getSubClassName(), subClass.getComment());
			}
		}
	    
		List<SubClass> subClasses = template.getSubClasses();
		SubClass subClass = null;
		
		for (IFile file : files) {
			try {
				contentHandler = new XmlContentHandler(file,tempMap);
				parserFactory.newSAXParser().parse(file.getContents(), contentHandler);
				
				subClass = contentHandler.getSubClass();
				subClasses.add(subClass);
				tempMap.put(subClass.getSubClassName(), file.getName());
			} catch (SAXException e) {
				if (e instanceof SAXParseException) {
				    SAXParseException e1 = (SAXParseException)e;
                    contentHandler.addMarker(file, e1.getMessage(), e1.getLineNumber(), IMarker.SEVERITY_ERROR);
                    template = null;
                }else {
                    throw new AutorException("error",e); 
                }
			} catch (Exception e) {
				throw new AutorException("error",e);
			} 
		}

		
		return template;
	}
}
