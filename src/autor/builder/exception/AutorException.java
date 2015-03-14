/**
 * 
 */
package autor.builder.exception;

/**
 * @author shanlin
 *
 */
public class AutorException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7959052241233393723L;
	
	private String message;
	
	public AutorException(String msg){
		super(msg);
		this.setMessage(msg);
	}
	
	public AutorException(String msg, Throwable throwable){
		super(msg, throwable);
		this.setMessage(msg);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
