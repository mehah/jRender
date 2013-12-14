package greencode.exception;

public class TagException extends RuntimeException {
	private static final long serialVersionUID = -6760478253509665854L;

	public TagException() {}
	
	public TagException(String message) { super(message); }
}
