package greencode.util;

public final class MathUtils {
	public final static int rand(int arg0, int arg1)
	{
		return (int) (Math.random() * arg1) + arg0;
	}
}