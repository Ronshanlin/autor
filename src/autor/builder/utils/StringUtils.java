/**
 * 
 */
package autor.builder.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
/**
 * @author shanlin
 *
 */
public class StringUtils {
	public static boolean isEmpty(String str){
		if (str == null || str == "") {
			return true;
		}
		
		return false;
	}
	
    // 过滤特殊字符   
    public static String pkgNameFilter(String str) throws PatternSyntaxException {
        Pattern p = Pattern.compile(AutorConstants.SEPARATOR_LINE);
        Matcher m = p.matcher(str);
        return m.replaceAll(AutorConstants.SEPARATOR_DOT).trim();
    }
}
