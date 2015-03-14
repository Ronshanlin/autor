package autor.builder.template;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author shazl
 */
public class FileGenerator {
    public static void main(String[] args) {
        
        ClassProperty classProperty = new ClassProperty();
        classProperty.setPropName("GETUNAME");
        classProperty.setPropValue("getUname");
        
        ClassProperty classProperty1 = new ClassProperty();
        classProperty1.setPropName("GETUNAME111");
        classProperty1.setPropValue("getUname111");
        
        List<ClassProperty> properties = new ArrayList<ClassProperty>();
        properties.add(classProperty1);
        properties.add(classProperty);
        
        SubClass subClass = new SubClass();
        subClass.setSubClassName("user");
        subClass.setProperties(properties);
        subClass.setComment("qqq");
        
        SubClass subClass1 = new SubClass();
        subClass1.setSubClassName("user1");
        subClass1.setProperties(properties);
        subClass1.setComment("sffsdfs");
        
        List<SubClass> classes = new ArrayList<SubClass>();
        classes.add(subClass);
        classes.add(subClass1);
        
        JavaTemplate template = new JavaTemplate();
        template.setPackageName("com.suning.demo.util");
        template.setClassName("R");
        template.setSubClasses(classes);
        
        System.out.println(FreemarkerParser.parse(template));
    }
}
