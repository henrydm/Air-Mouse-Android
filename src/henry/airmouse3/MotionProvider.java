package henry.airmouse3;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

interface OnMotionChangedListener {
	public void OnMotionChanged(float x, float y);
}

interface OnCalibrationFinishedListener {
	public void OnCalibrationFinished();
}

public class MotionProvider implements SensorEventListener {

	private static boolean _loaded = false;
	private static long _lastSensorChange;
	private static int _sensorType = Sensor.TYPE_GYROSCOPE;

	private static Boolean _calibrating = false;
	private static float[] _calibratingX;
	private static float[] _calibratingY;
	private static int _calibrationSamples = 50;
	private static int _calibrationStep = -1;

	private static MotionProvider _self = new MotionProvider();
	private static OnMotionChangedListener _motionListener;
	private static OnCalibrationFinishedListener _calibrationFinishedListener;

	protected static void SetOnCalibrationFinished(OnCalibrationFinishedListener listener) {
		_calibrationFinishedListener = listener;
	}

	protected static void ReleaseCalibrationListener() {
		_calibrationFinishedListener = null;
	}

	protected static void SetOnMotionChanged(OnMotionChangedListener listener) {
		_motionListener = listener;
	}

	protected static void ReleaseMotionListener() {
		_motionListener = null;
	}

	protected static void Calibrate() {
		_calibrating = true;
	}

	protected static void RegisterEvents(Context context) {
		if (_loaded)
			return;
		SensorManager sm = (SensorManager) context.getSystemService(android.content.Context.SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(_sensorType);
		if (sensors.size() > 0) {
			sm.registerListener(_self, sensors.get(0), SensorManager.SENSOR_DELAY_FASTEST);
			_loaded = true;
		}
	}

	protected static void UnregisterEvents(Context context) {
		SensorManager sm = (SensorManager) context.getSystemService(android.content.Context.SENSOR_SERVICE);
		sm.unregisterListener(_self);
		_loaded = false;
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float x = event.values[0];
		float y = event.values[2];

		if (_calibrating) {
			ComputeCalibration(x, y);
		}

		else {
			long now = new Date().getTime();

			if (now - _lastSensorChange > Settings.getMIN_MILLIS_TOPDATE()) {

				float finalX = x - Settings.getCALIBRATION_DELTAX();
				float finalY = y - Settings.getCALIBRATION_DELTAY();

				if (_motionListener != null)
					_motionListener.OnMotionChanged(finalX, finalY);

				_lastSensorChange = now;
			}
		}
	}

	private void ComputeCalibration(float currentX, float currentY) {
		if (_calibrationStep == -1) {
			_calibratingX = new float[_calibrationSamples];
			_calibratingY = new float[_calibrationSamples];
			_calibrationStep++;
		} else {
			if (_calibrationStep < _calibrationSamples) {
				_calibratingX[_calibrationStep] = currentX;
				_calibratingY[_calibrationStep] = currentY;
				_calibrationStep++;
			} else {
				_calibrating = false;
				_calibrationStep = -1;

				float totalX = 0;
				for (float currentVal : _calibratingX) {
					totalX += currentVal;
				}
				float deltaX = totalX / _calibrationSamples;
				Settings.setCALIBRATION_DELTAX(deltaX);

				float totalY = 0;
				for (float currentVal : _calibratingY) {
					totalY += currentVal;
				}

				float deltaY = totalY / _calibrationSamples;
				Settings.setCALIBRATION_DELTAY(deltaY);

				if (_calibrationFinishedListener != null)
					_calibrationFinishedListener.OnCalibrationFinished();

			}
		}
	}
}
