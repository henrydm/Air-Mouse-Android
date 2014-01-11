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
	private static final String KEY_SWITCH_BUTTONS = "SWITCH_BUTTONS";
	private static final String KEY_FIRST_USE = "FIRST_USE";

	private final static int DEFAULT_MIN_WHEEL_PIXELS = 10;
	private final static int DEFAULT_MIN_MILLIS_TOPDATE=10;
	private final static int DEFAULT_PORT = 6000;
	private final static int DEFAULT_MOTION_FACTOR = 50;
	private final static int DEFAULT_MIN_MOVEMENT=1;
	private final static boolean SWITCH_BUTTONS= false;
	private final static boolean FIRST_USE= true;
	
	public static void LoadSettings(Context context) {
		mContext = context;
//		SharedPreferences.Editor editor = getSettings().edit();
//		editor.remove(KEY_FIRST_USE);
//		editor.commit();
	}

	private static SharedPreferences getSettings() {
		return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
	}

	private static void PushFloat(String key, float f) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putFloat(key, f);
		editor.commit();
	}
	private static void PushBool(String key, boolean b) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putBoolean(key, b);
		editor.commit();
	}
	private static void PushInt(String key, int i) {
		SharedPreferences.Editor editor = getSettings().edit();
		editor.putInt(key, i);
		editor.commit();
	}

	
	protected static int getMIN_WHEEL_PIXELS() {
		return getSettings().getInt(KEY_MIN_WHEEL_PIXELS, DEFAULT_MIN_WHEEL_PIXELS);
	}

	protected static void setMIN_WHEEL_PIXELS(int value) {
		PushInt(KEY_MIN_WHEEL_PIXELS, value);
	}

	protected static int getMIN_MILLIS_TOPDATE() {
		return getSettings().getInt(KEY_MIN_MILLIS_TOPDATE, DEFAULT_MIN_MILLIS_TOPDATE);
	}

	protected static void setMIN_MILLIS_TOPDATE(int value) {
		PushInt(KEY_MIN_MILLIS_TOPDATE, value);
	}

	protected static int getPORT() {
		return getSettings().getInt(KEY_PORT, DEFAULT_PORT);
	}

	protected static void setPORT(int value) {
		PushInt(KEY_PORT, value);
	}

	protected static int getMOTION_FACTOR() {
		return getSettings().getInt(KEY_MOTION_FACTOR, DEFAULT_MOTION_FACTOR);
	}

	protected static void setMOTION_FACTOR(int value) {
		PushInt(KEY_MOTION_FACTOR, value);
	}

	protected static int getMIN_MOVEMENT() {
		return getSettings().getInt(KEY_MIN_MOVEMENT, DEFAULT_MIN_MOVEMENT);
	}

	protected static void setMIN_MOVEMENT(int value) {
		PushInt(KEY_MIN_MOVEMENT, value);
	}

	protected static float getCALIBRATION_DELTAX() {
		return getSettings().getFloat(KEY_CALIBRATION_DELTAX, 0);
	}

	protected static void setCALIBRATION_DELTAX(float value) {
		PushFloat(KEY_CALIBRATION_DELTAX, value);
	}

	protected static float getCALIBRATION_DELTAY() {
		return getSettings().getFloat(KEY_CALIBRATION_DELTAY, 0);
	}

	protected static void setCALIBRATION_DELTAY(float value) {
		PushFloat(KEY_CALIBRATION_DELTAY, value);
	}
	
	protected static boolean getSWITCH_BUTTONS() {
		return getSettings().getBoolean(KEY_SWITCH_BUTTONS, SWITCH_BUTTONS);
	}

	protected static void setSWITCH_BUTTONS(boolean value) {
		PushBool(KEY_SWITCH_BUTTONS, value);
	}
	
	protected static boolean getFISRT_USE() {
		return getSettings().getBoolean(KEY_FIRST_USE, FIRST_USE);		
	}
	
	protected static void setFISRT_USE(boolean value) {
		PushBool(KEY_FIRST_USE, value);
	}
}
