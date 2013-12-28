package henry.airmouse3;

import java.util.ArrayList;
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

	private static long _lastSensorChange;
	private static int _sensorType = Sensor.TYPE_LINEAR_ACCELERATION;

	private static Boolean _calibrating = false;
	private static float[] _calibratingX;
	private static float[] _calibratingY;
	private static int _calibrationSamples = 50;
	private static int _calibrationStep = -1;

	private static MotionProvider _self = new MotionProvider();
	private static List<OnMotionChangedListener> _motionListeners = new ArrayList<OnMotionChangedListener>();
	private static List<OnCalibrationFinishedListener> _calibrationFinishedListeners = new ArrayList<OnCalibrationFinishedListener>();

	protected static void SetOnCalibrationFinished(OnCalibrationFinishedListener listener) {
		_calibrationFinishedListeners.add(listener);
	}

	protected static void SetOnMotionChanged(OnMotionChangedListener listener) {
		_motionListeners.add(listener);
	}

	protected static void Calibrate() {
		_calibrating = true;
	}

	protected static void RegisterEvents(Context context) {
		SensorManager sm = (SensorManager) context.getSystemService(android.content.Context.SENSOR_SERVICE);
		List<Sensor> sensors = sm.getSensorList(_sensorType);
		if (sensors.size() > 0) {
			sm.registerListener(_self, sensors.get(0), SensorManager.SENSOR_DELAY_GAME);
		}
	}

	protected static void UnregisterEvents(Context context) {
		SensorManager sm = (SensorManager) context.getSystemService(android.content.Context.SENSOR_SERVICE);
		sm.unregisterListener(_self);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float x = event.values[0];
		float y = event.values[1];

		if (_calibrating) {
			ComputeCalibration(x, y);
		}

		else {
			long now = new Date().getTime();

			if (now - _lastSensorChange > Settings.MIN_MILLIS_TOPDATE) {

				float finalX = x - Settings.GetDeltaX();
				float finalY = y - Settings.GetDeltaY();

				for (OnMotionChangedListener listener : _motionListeners)
					listener.OnMotionChanged(finalX, finalY);

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
				Settings.SetDeltaX(deltaX);

				float totalY = 0;
				for (float currentVal : _calibratingY) {
					totalY += currentVal;
				}

				float deltaY = totalY / _calibrationSamples;
				Settings.SetDeltaY(deltaY);

				for (OnCalibrationFinishedListener listener : _calibrationFinishedListeners)
					listener.OnCalibrationFinished();

			}
		}
	}
}
