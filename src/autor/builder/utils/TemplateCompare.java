package autor.builder.utils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import autor.builder.template.ClassProperty;
import autor.builder.template.JavaTemplate;
import autor.builder.template.SubClass;

public class TemplateCompare {
    private static TemplateCompare templateCompare = new TemplateCompare();
    private TemplateCompare(){}
    
    public static boolean compareTemplate(JavaTemplate oldTemplate, JavaTemplate newTemplate){
        oldTemplate = templateCompare.sortSub(oldTemplate);
        newTemplate = templateCompare.sortSub(newTemplate);
        
        return JsonUtil.toJson(oldTemplate).equals(JsonUtil.toJson(newTemplate));
    }
    
    public static boolean compareSubclass(SubClass subClass1, SubClass subClass2){
        subClass1 = templateCompare.sortPropertis(subClass1);
        subClass2 = templateCompare.sortPropertis(subClass2);
        
        return JsonUtil.toJson(subClass1).equals(JsonUtil.toJson(subClass2));
    }
    
    private JavaTemplate sortSub(JavaTemplate template){
        SubClassSort subClassSort = new SubClassSort();
        subClassSort.setSubClasses(template.getSubClasses());
        List<SubClass> subClasses = subClassSort.sort();
        
        template.setSubClasses(subClasses);
        
        for (SubClass subClass : subClasses) {
            subClass = this.sortPropertis(subClass);
        }
        
        return template;
    }
    
    private SubClass sortPropertis(SubClass subClass){
        ClassPropertiesSort sort = new ClassPropertiesSort();
        sort.setClassProperties(subClass.getProperties());
        
        subClass.setProperties(sort.sort());
        
        return subClass;
    }
    
    private class SubClassSort implements Comparator<SubClass>{
        private List<SubClass> subClasses;
        
        public void setSubClasses(List<SubClass> subClasses){
            this.subClasses = subClasses;
        }
        
        @Override
        public int compare(SubClass o1, SubClass o2) {
            return o1.getSubClassName().compareTo(o2.getSubClassName());
        }
        
        public List<SubClass> sort(){
            Collections.sort(this.subClasses, this);
            return this.subClasses;
        }
    }
    
    private class ClassPropertiesSort implements Comparator<ClassProperty>{
        private List<ClassProperty> classProperties;
        
        @Override
        public int compare(ClassProperty o1, ClassProperty o2) {
            return o1.getPropName().compareTo(o2.getPropName());
        }

        public void setClassProperties(List<ClassProperty> classProperties) {
            this.classProperties = classProperties;
        }
        
        public List<ClassProperty> sort(){
            Collections.sort(this.classProperties, this);
            
            return this.classProperties;
        }
    }
}
