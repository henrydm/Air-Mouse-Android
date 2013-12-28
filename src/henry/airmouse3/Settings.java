package henry.airmouse3;

public class Settings {

	public static long MIN_MILLIS_TOPDATE = 1000;
	public static int PORT = 6000;
	public static float SENSIBILITY = 100;
	private static float _deltaX = 0;
	private static float _deltaY = 0;
	
	protected static float GetDeltaX() {
		return _deltaX;
	}
	
	protected static float GetDeltaY() {
		return _deltaY;
	}

	protected static void SetDeltaX(float deltaX) {
		_deltaX = deltaX;
	}

	protected static void SetDeltaY(float deltaY) {
		_deltaY = deltaY;
	}
}
