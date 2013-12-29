package henry.airmouse3;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {

	private static Context mContext;
	private static final String SHARED_PREFS_FILE = "HMPrefs";

	private static final String KEY_MIN_WHEEL_PIXELS = "MIN_WHEEL_PIXELS";
	private static final String KEY_MIN_MILLIS_TOPDATE = "MIN_MILLIS_TOPDATE";
	private static final String KEY_PORT = "PORT";
	private static final String KEY_MOTION_FACTOR = "MOTION_FACTOR";
	private static final String KEY_MIN_MOVEMENT = "MIN_MOVEMENT";
	private static final String KEY_CALIBRATION_DELTAX = "CALIBRATION_DELTAX";
	private static final String KEY_CALIBRATION_DELTAY = "CALIBRATION_DELTAY";



	public static void LoadSettings(Context context) {
		mContext = context;
		SharedPreferences.Editor editor = getSettings().edit();
		editor.clear();
		editor.commit();
	}

	private static SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}


	private static void PushFloat(String key, float f)
	{
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putFloat(key, f);
		editor.commit();
	}
	private static void PushInt(String key, int i)
	{
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(key, i);
		editor.commit();
	}
	
	protected static int getMIN_WHEEL_PIXELS() {
		return getSettings().getInt(KEY_MIN_WHEEL_PIXELS, 10);
	}

	protected static void setMIN_WHEEL_PIXELS(int mIN_WHEEL_PIXELS) {
		PushInt(KEY_MIN_WHEEL_PIXELS,mIN_WHEEL_PIXELS);
	}

	protected static int getMIN_MILLIS_TOPDATE() {	 
		return getSettings().getInt(KEY_MIN_MILLIS_TOPDATE, 10);
	}

	protected static void setMIN_MILLIS_TOPDATE(int mIN_MILLIS_TOPDATE) {
		PushInt(KEY_MIN_MILLIS_TOPDATE,mIN_MILLIS_TOPDATE);
	}

	protected static int getPORT() {
		return getSettings().getInt(KEY_PORT, 6000);
	}

	protected static void setPORT(int pORT) {
		PushInt(KEY_PORT,pORT);
	}

	protected static int getMOTION_FACTOR() {
		return getSettings().getInt(KEY_MOTION_FACTOR, 100);
	}

	protected static void setMOTION_FACTOR(int mOTION_FACTOR) {
		PushInt(KEY_MOTION_FACTOR,mOTION_FACTOR);
	}

	protected static int getMIN_MOVEMENT() {
		return getSettings().getInt(KEY_MIN_MOVEMENT, 1);
	}

	protected static void setMIN_MOVEMENT(int mIN_MOVEMENT) {
		PushInt(KEY_MIN_MOVEMENT,mIN_MOVEMENT);
	}

	protected static float getCALIBRATION_DELTAX() {
		return getSettings().getFloat(KEY_CALIBRATION_DELTAX, 0);
	}

	protected static void setCALIBRATION_DELTAX(float cALIBRATION_DELTAX) {
		PushFloat(KEY_CALIBRATION_DELTAX,cALIBRATION_DELTAX);
	}

	protected static float getCALIBRATION_DELTAY() {
		return getSettings().getFloat(KEY_CALIBRATION_DELTAY, 0);
	}

	protected static void setCALIBRATION_DELTAY(float cALIBRATION_DELTAY) {
		PushFloat(KEY_CALIBRATION_DELTAY,cALIBRATION_DELTAY);
	}
}
