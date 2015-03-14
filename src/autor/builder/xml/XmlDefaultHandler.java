/**
 * 
 */
package autor.builder.xml;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.xml.sax.helpers.DefaultHandler;

import autor.builder.message.ConsoleHelper;
import autor.builder.utils.ConsoLevel;

/**
 * @author shanlin
 *
 */
public class XmlDefaultHandler extends DefaultHandler{
	private static final String MARKER_TYPE = "autor.xmlProblem";

	protected void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
			
			String msg = "-"+file.getName()+" line number:"+lineNumber+" "+message;
			ConsoleHelper.print(ConsoLevel.getBySeverity(severity), new Object[]{msg});
		} catch (CoreException e) {
			ConsoleHelper.printError("--AuotR plugin error: add Marker error"+e.getMessage());
		}
	}
}
