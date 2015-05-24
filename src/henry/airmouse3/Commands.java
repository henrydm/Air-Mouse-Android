package henry.airmouse3;

public final class Commands {

	public static final String Zoom = "zoom";
	public static final String Wheel = "wheel";
	public static final String Volume = "volume";
	public static final String In = "in";
	public static final String Out = "out";
	public static final String Up = "up";
	public static final String Down = "down";
	public static final String Primary = "primary";
	public static final String Secondary = "secondary";
	public static final String Right = "right";
	public static final String Left = "left";
	public static final String Separator = "|";
	public static final String Separator2 = "#";	
	public static final String Hello = "hola";
	public static final String HelloResponse = "que ase";
	public static final String Bye = "adeu";

	public static final String WheelUp = Wheel + Separator + Up;
	public static final String WheelDown = Wheel + Separator + Down;
	public static final String VolumeUp = Volume + Separator + Up;
	public static final String VolumeDown = Volume + Separator + Down;
	public static final String UpRight = Up + Separator + Right;
	public static final String UpLeft = Up + Separator + Left;
	public static final String DownRight = Down + Separator + Right;
	public static final String DownLeft = Down + Separator + Left;
	public static final String DownPrimary = Down + Separator + Primary;
	public static final String UpPrimary = Up + Separator + Primary;
	public static final String DownSecondary = Down + Separator + Secondary;
	public static final String UpSecondary = Up + Separator + Secondary;

	public static final String GetConnectionString() {
		return Hello + Separator + android.os.Build.MANUFACTURER + Separator2 + android.os.Build.MODEL;
	}

	public static final String GetMouseDeltaString(float x, float y) {
		String xStr = Float.toString(x);
		String yStr = Float.toString(y);
		return xStr + Separator + yStr;
	}

	public static final String GetWheelDeltaString(float delta) {
		return Wheel + Separator + String.valueOf(delta);
	}

	public static final String GetZoomDeltaString(float delta) {
		return Zoom + Separator + String.valueOf(delta);
	}
}
