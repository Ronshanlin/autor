package autor.builder.template;

import java.util.ArrayList;
import java.util.List;

public class JavaTemplate {
    private String packageName;
    private String className;
    private List<SubClass> subClasses = new ArrayList<SubClass>();
    
    public String getPackageName() {
        return packageName;
    }
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    public String getClassName() {
        return className;
    }
    public void setClassName(String className) {
        this.className = className;
    }
    public List<SubClass> getSubClasses() {
        return subClasses;
    }
    public void setSubClasses(List<SubClass> subClasses) {
        this.subClasses = subClasses;
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JavaTemplate [packageName=" + packageName + ", className="
				+ className + ", subClasses=" + subClasses + "]";
	}
}
