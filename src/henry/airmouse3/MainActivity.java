package henry.airmouse3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.os.Build;
import android.os.Bundle;
import android.annotation.TargetApi;
import android.app.Activity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class MainActivity extends Activity {

	private Boolean _wheelScsrolling = false;
	private float _lastWheelPixel = -1;
	private float MIN_WHEEL_PIXELS = 10;

	// private DatagramSocket socket;
	// private static final int SERVERPORT = 6000;
	// private static String SERVER_IP = "hfgjhgfgh";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button) findViewById(R.id.buttonCalibrate)).setOnClickListener(OnClickCalibrate);
		((Button) findViewById(R.id.buttonLeft)).setOnTouchListener(OnTouchButtonRight);
		((Button) findViewById(R.id.buttonRight)).setOnTouchListener(OnTouchButtonLeft);
		((Button) findViewById(R.id.buttonRight)).setOnTouchListener(OnTouchButtonLeft);
		((Button) findViewById(R.id.buttonWheel)).setOnTouchListener(OnTouchWheel);

		Scan.SetOnScanInterrupted(OnScanInterrupted);
		Scan.ScanIpsAsync(this.getBaseContext());

		MotionProvider.SetOnMotionChanged(OnMotionChanged);
		MotionProvider.SetOnCalibrationFinished(OnCalibrationFinished);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		 MotionProvider.RegisterEvents(getApplicationContext());
		
	}

	@Override
	protected void onPause() {
		MotionProvider.UnregisterEvents(getApplicationContext());
		
		super.onPause();
	};

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
			String xStr = Float.toString(x * Settings.SENSIBILITY);
			String yStr = Float.toString(y * Settings.SENSIBILITY);
			final String toSend = xStr + " " + yStr;
			Connection.Send(toSend);
		}
	};

	private OnScanInterruptedListener OnScanInterrupted = new OnScanInterruptedListener() {

		@Override
		public void IpFound(String ip) {
			Connection.CreateConnection(ip, Settings.PORT);
		}

		@Override
		public void ErrorHappened(henry.airmouse3.ErrorCode error) {
			if (Scan.ERROR == ErrorCode.Wifi_Not_Connected) {
				ShowToast("WIFI is not connected to any net");

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

	private OnClickListener OnClickCalibrate = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			MotionProvider.Calibrate();
		}
	};

	private OnTouchListener OnTouchWheel = new OnTouchListener() {

		@TargetApi(Build.VERSION_CODES.GINGERBREAD)
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {

			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {

				// Wheel Move End
				if (_wheelScsrolling) {
					_lastWheelPixel = -1;
					_wheelScsrolling = false;

				}

				// Click wheel
				else {
					Connection.Send("wheel up");
				}

			} else if (event.getAction() == android.view.MotionEvent.ACTION_MOVE) {

				PointerCoords coordinates = new PointerCoords();
				event.getPointerCoords(event.getPointerCount() - 1, coordinates);
				if (_lastWheelPixel == -1) {
					_lastWheelPixel = coordinates.y;
				}

				else if (Math.abs(coordinates.y - _lastWheelPixel) > MIN_WHEEL_PIXELS) {
					Connection.Send("wheel " + String.valueOf(coordinates.y - _lastWheelPixel));
					_lastWheelPixel = coordinates.y;
					_wheelScsrolling = true;
				}

			}
			return false;
		}
	};

	private OnTouchListener OnTouchButtonRight = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Connection.Send("down right");
			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				Connection.Send("up right");
			}
			return false;
		}
	};

	private OnTouchListener OnTouchButtonLeft = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
				Connection.Send("down left");
			} else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
				Connection.Send("up left");
			}
			return false;
		}
	};

}