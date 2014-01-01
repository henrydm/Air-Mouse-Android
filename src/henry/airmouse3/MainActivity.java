package henry.airmouse3;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.hardware.SensorManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	ImageView _buttonLeft, _buttonRight, _buttonWheel, _buttonFocus;
	private Boolean _wheelScsrolling = false;
	private Boolean _focusOn = false;
	private float _lastWheelPixel = -1;
	private byte _wheelStep = 0;

	private OrientationEventListener myOrientationEventListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		_buttonLeft = (ImageView) findViewById(R.id.imageViewButtonLeft);
		_buttonLeft.setOnTouchListener(OnTouchButtonLeft);

		_buttonRight = (ImageView) findViewById(R.id.imageViewButtonRight);
		_buttonRight.setOnTouchListener(OnTouchButtonRight);

		_buttonWheel = (ImageView) findViewById(R.id.imageViewButtonWheel);
		_buttonWheel.setOnTouchListener(OnTouchWheel);

		_buttonFocus = (ImageView) findViewById(R.id.imageViewButtonFocus);
		_buttonFocus.setOnTouchListener(OnTouchButtonFocus);

		Settings.LoadSettings(getApplicationContext());

		if (!Connection.IsConnected()) {
			Scan.SetOnScanInterrupted(OnScanInterrupted);
			Scan.ScanIpsAsync(this.getBaseContext());
		}
		myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {

			@Override
			public void onOrientationChanged(int arg0) {
				if (arg0 != -1) {
					if (arg0 > 75 && arg0 < 105) {
						Intent Intent = new Intent(getApplicationContext(), KeyboardActivity.class);
						startActivity(Intent);
					}
					if (arg0 > 255 && arg0 < 285) {
						Intent Intent = new Intent(getApplicationContext(), KeyboardActivity.class);
						startActivity(Intent);
					}
				}
			}
		};

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	private void Release() {
		if (myOrientationEventListener != null) {
			myOrientationEventListener.disable();
		}
		MotionProvider.ReleaseCalibrationListener();
		MotionProvider.ReleaseMotionListener();
		MotionProvider.UnregisterEvents(getApplicationContext());
	}

	private void AssignMotionListeners() {
		if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.enable();
		}

		MotionProvider.SetOnMotionChanged(OnMotionChanged);
		MotionProvider.SetOnCalibrationFinished(OnCalibrationFinished);
		MotionProvider.RegisterEvents(getApplicationContext());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		getMenuInflater().inflate(R.layout.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_calibrate:
			MotionProvider.Calibrate();
			return true;

		case R.id.menu_settings:
			Intent Intent = new Intent(this, SettingsActivity.class);
			startActivity(Intent);
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		AssignMotionListeners();

	}

	@Override
	protected void onPause() {
		super.onPause();
		Release();
		// wakelock.release();

	};

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
		// wakelock.release();
	}

	@Override
	protected void onStop() {
		MotionProvider.UnregisterEvents(getApplicationContext());
		super.onStop();
	}

	private OnCalibrationFinishedListener OnCalibrationFinished = new OnCalibrationFinishedListener() {

		@Override
		public void OnCalibrationFinished() {
			ShowToast("Calibration OK");
		}
	};
	private OnMotionChangedListener OnMotionChanged = new OnMotionChangedListener() {

		@Override
		public void OnMotionChanged(float x, float y) {

			float motionFactor = Settings.getMOTION_FACTOR();
			if (_focusOn)
				motionFactor /= 8;

			float finalX = x * motionFactor;
			float finalY = y * motionFactor;

			if (Math.abs(finalX) > Settings.getMIN_MOVEMENT() || Math.abs(finalY) > Settings.getMIN_MOVEMENT()) {
				String xStr = Float.toString(finalX);
				String yStr = Float.toString(finalY);
				final String toSend = xStr + " " + yStr;
				Connection.Send(toSend);

			}
		}
	};

	private OnScanInterruptedListener OnScanInterrupted = new OnScanInterruptedListener() {

		@Override
		public void IpFound(String ip) {
			Connection.CreateConnection(ip, Settings.getPORT());
		}

		@Override
		public void ErrorHappened(henry.airmouse3.ErrorCode error) {
			if (Scan.ERROR == ErrorCode.Wifi_Not_Connected) {
				ShowToast("WIFI is not connected to any net");

			} else if (Scan.ERROR == ErrorCode.Time_Out) {
				ShowToast("No Server found");
			} else {
				ShowToast("WIFI unknown error");
			}

		}
	};

	private void ShowToast(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private OnTouchListener OnTouchWheel = new OnTouchListener() {

		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttonclick);
				_buttonWheel.setImageDrawable(clickedDrawable);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// Wheel Move End
				if (_wheelScsrolling) {
					_lastWheelPixel = -1;
					_wheelScsrolling = false;
					Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbutton);
					_buttonWheel.setImageDrawable(clickedDrawable);
				}

				// Click wheel
				else {
					Connection.Send("wheel up");
					Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbutton);
					_buttonWheel.setImageDrawable(clickedDrawable);
				}

			} else if (event.getAction() == android.view.MotionEvent.ACTION_MOVE) {

				PointerCoords coordinates = new PointerCoords();
				event.getPointerCoords(event.getPointerCount() - 1, coordinates);
				if (_lastWheelPixel == -1) {
					_lastWheelPixel = coordinates.y;
				}

				else if (Math.abs(coordinates.y - _lastWheelPixel) > Settings.getMIN_WHEEL_PIXELS()) {
					float delta = coordinates.y - _lastWheelPixel;
					Connection.Send("wheel " + String.valueOf(delta));
					_lastWheelPixel = coordinates.y;
					_wheelScsrolling = true;

					if (_wheelStep == 0) {
						if (delta < 0) {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttonup1);
							_buttonWheel.setImageDrawable(clickedDrawable);
						} else {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttondown1);
							_buttonWheel.setImageDrawable(clickedDrawable);
						}
					}
					if (_wheelStep == 1) {
						if (delta < 0) {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttonup2);
							_buttonWheel.setImageDrawable(clickedDrawable);
						} else {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttondown2);
							_buttonWheel.setImageDrawable(clickedDrawable);
						}
					}
					if (_wheelStep == 2) {
						if (delta < 0) {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttonup3);
							_buttonWheel.setImageDrawable(clickedDrawable);
						} else {
							Drawable clickedDrawable = getResources().getDrawable(R.drawable.midbuttondown3);
							_buttonWheel.setImageDrawable(clickedDrawable);
						}
					}

					if (_wheelStep == 2)
						_wheelStep = 0;
					else
						_wheelStep++;

				}

			}
			return true;
		}
	};

	private OnTouchListener OnTouchButtonRight = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Connection.Send("down right");
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.rightbuttonclick);
				_buttonRight.setImageDrawable(clickedDrawable);

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				Connection.Send("up right");
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.rightbutton);
				_buttonRight.setImageDrawable(clickedDrawable);
			}
			return true;
		}
	};
	private OnTouchListener OnTouchButtonFocus = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

				Drawable clickedDrawable = getResources().getDrawable(R.drawable.focusclick);
				_buttonFocus.setImageDrawable(clickedDrawable);
				_focusOn = true;

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				Connection.Send("down left");
				Connection.Send("up left");
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.focus);
				_buttonFocus.setImageDrawable(clickedDrawable);
				_focusOn = false;
			} 
			return true;
		}
	};

	private OnTouchListener OnTouchButtonLeft = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Connection.Send("down left");
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.leftbuttonclick);
				_buttonLeft.setImageDrawable(clickedDrawable);
			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				Connection.Send("up left");
				Drawable clickedDrawable = getResources().getDrawable(R.drawable.leftbutton);
				_buttonLeft.setImageDrawable(clickedDrawable);
			}
			return true;
		}
	};

}