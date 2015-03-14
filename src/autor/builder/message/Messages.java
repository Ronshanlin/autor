/**
 * 
 */
package autor.builder.message;

import org.eclipse.osgi.util.NLS;

/**
 * @author shanlin
 *
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = "autor.builder.message.messages";
	// properties key 必须与properties中一致
	public static String Console_Date_Tag;
	public static String Console_Data_Project_Tag;
	public static String Package_Name_Prefix;
	
	static{
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}
}
