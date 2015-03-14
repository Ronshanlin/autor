/**
 * 
 */
package autor.builder.utils;

/**
 * @author shanlin
 *
 */
public enum ConsoLevel {
	INFO(0,"INFO"),
	WARN(1,"WARN"),
	ERROR(2,"ERROR");
	
	private String level;
	private int severity;
	
	private ConsoLevel(int severity,String level){
		this.level = level;
		this.severity = severity;
	}
	
	public String getLevel(){
		return this.level;
	}

	public int getSeverity() {
		return severity;
	}
	
	public static ConsoLevel getBySeverity(int severity){
		for (ConsoLevel level : ConsoLevel.values()) {
			if (severity == level.severity) {
				return level;
			}
		}
		return null;
	}
}
