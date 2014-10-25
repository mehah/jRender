package greencode.exception;

public class StopProcess extends RuntimeException {
	private static final long serialVersionUID = -6760478253509665854L;
	public StopProcess() {}
	public StopProcess(String message) { super(message); }
}
