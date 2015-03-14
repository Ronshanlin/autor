package autor.builder.template;

public class ClassProperty {
    private String propName;
    private String propValue;
    private String maker;
    
    public String getPropName() {
        return propName;
    }
    public void setPropName(String propName) {
        this.propName = propName;
    }
    public String getPropValue() {
        return propValue;
    }
    public void setPropValue(String propValue) {
        this.propValue = propValue;
    }
    public String getMaker() {
        return maker;
    }
    public void setMaker(String maker) {
        this.maker = maker;
    }
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClassProperty [propName=" + propName + ", propValue="
				+ propValue + ", maker=" + maker + "]";
	}
}
