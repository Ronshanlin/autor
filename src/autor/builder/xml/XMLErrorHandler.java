/**
 * 
 */
package autor.builder.xml;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author shanlin
 *
 */
public class XMLErrorHandler extends XmlDefaultHandler{
	private IFile file;

	public XMLErrorHandler(IFile file) {
		this.file = file;
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		addMarker(e, IMarker.SEVERITY_WARNING);
	}

	@Override
	public void error(SAXParseException exception) throws SAXException {
		addMarker(exception, IMarker.SEVERITY_ERROR);
	}

	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		addMarker(exception, IMarker.SEVERITY_ERROR);
	}
	
	private void addMarker(SAXParseException e, int severity) {
		super.addMarker(file, e.getMessage(), e.getLineNumber(), severity);
	}
}

