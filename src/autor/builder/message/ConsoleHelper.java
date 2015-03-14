/**
 * 
 */
package autor.builder.message;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import autor.builder.utils.ConsoLevel;

/**
 * @author shanlin
 */
public class ConsoleHelper {
	private static MessageConsoleStream consoleStream;
	private static MessageConsole mConsole;
	
	public static void init(){
		mConsole = new MessageConsole("AutoR Console", null);
		
		ConsolePlugin.getDefault().getConsoleManager()
				.addConsoles(new IConsole[] { mConsole });
		
		consoleStream = mConsole.newMessageStream();
	}
	
	public static synchronized void print(ConsoLevel tag, Object[] objects) {
//		Display display = getDisplay();
//		Color mRed = new Color(display, 255, 0, 0);
		
		String dateTag = AutorPrintStream.getMessageTag(tag.getLevel());

		for (Object obj : objects) {
			consoleStream.print(dateTag);
			consoleStream.print(" ");
			if (obj instanceof String)
				consoleStream.println((String) obj);
			else if (obj == null)
				consoleStream.println("(null)");
			else
				consoleStream.println(obj.toString());
		}
	}
	
	public static synchronized void printError(String errMsg) {
		print(ConsoLevel.ERROR, new Object[]{ errMsg });
	}
	
    public static synchronized void printError(String errMsg, Throwable throwable) {
        print(ConsoLevel.ERROR, new Object[]{ errMsg, getExceptionStackTrace(throwable) });
    }
	
    public static synchronized void printInfo(String msg) {
        print(ConsoLevel.INFO, new Object[]{ msg });
    }
	
    
    public static String getExceptionStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            ConsoleHelper.printError("Transfer exception stack trace error!! Will return \"\"");
        }
        return sw.toString();
    }
//	private Display getDisplay() {
//		Display display = Display.getCurrent();
//		if (display != null) {
//			return display;
//		}
//		
//		return Display.getDefault();
//	}
}
