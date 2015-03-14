package autor.builder.template;

import java.util.List;

public class SubClass {
    private String subClassName;
    private String comment;
    private List<ClassProperty> properties;
    
    public String getSubClassName() {
        return subClassName;
    }
    public void setSubClassName(String subClassName) {
        this.subClassName = subClassName;
    }
    public List<ClassProperty> getProperties() {
        return properties;
    }
    public void setProperties(List<ClassProperty> properties) {
        this.properties = properties;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SubClass [subClassName=" + subClassName + ", comment="
				+ comment + ", properties=" + properties + "]";
	}
}
