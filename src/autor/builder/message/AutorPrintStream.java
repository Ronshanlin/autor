/**
 * 
 */
package autor.builder.message;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Calendar;

/**
 * @author shanlin
 * 
 * 模仿adt中处理
 */
public class AutorPrintStream extends PrintStream {

	public AutorPrintStream(OutputStream stream) {
		super(stream);
	}

	public static String getMessageTag(String tag) {
		Calendar c = Calendar.getInstance();

		if (tag == null) {
			return String.format(Messages.Console_Date_Tag, new Object[] { c });
		}

		return String.format(Messages.Console_Data_Project_Tag, new Object[] {
				c, tag });
	}
}
