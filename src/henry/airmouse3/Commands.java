package henry.airmouse3;

public final class Commands {

	public static final String VolumeUp = "volume|up";
	public static final String VolumeDown = "volume|down";
	public static final String UpRight = "up|right";
	public static final String UpLeft = "up|left";
	public static final String DownRight = "down|right";
	public static final String DownLeft = "down|left";
	public static final String Bye = "adeu";
	
	public static final String GetDelta(float x, float y)
	{
		String xStr = Float.toString(x);
		String yStr = Float.toString(y);
		return xStr + "|" + yStr;
	}
}
