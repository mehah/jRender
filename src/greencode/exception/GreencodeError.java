package greencode.exception;

public class GreencodeError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	public GreencodeError(String message) { super(message); }
	public GreencodeError(Throwable arg0) { super(arg0); }
}
