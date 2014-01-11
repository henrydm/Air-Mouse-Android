package henry.airmouse3;

import java.net.DatagramSocket;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadApp extends Activity {
	byte _wifiStep = 0;
	ImageView _imageView;
	TextView _textViewInfo;
	TextView _textViewError;
	Timer _timer = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_app);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		_imageView = (ImageView) findViewById(R.id.imageViewWifi);
		_textViewInfo = (TextView) findViewById(R.id.textViewLoadInfo);
		_textViewError = (TextView) findViewById(R.id.textViewLoadError);

		Settings.LoadSettings(getApplicationContext());

		PackageManager packageManager = getPackageManager();
		boolean gyroExists = packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);

		if (!gyroExists) {
			AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
			myAlertDialog.setTitle(R.string.app_name);
			myAlertDialog.setMessage("Unfortunately your phone is not equipped with gyroscope, try buying a new phone");
		}

		else {
			StartLoopAnimation();

			Scan.SetOnScanInterrupted(OnScanInterrupted);
			Scan.ScanIpsAsync(this.getBaseContext());
		}
	}

	private void StartLoopAnimation() {
		_timer = new Timer();

		_timer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				Drawable clickedDrawable = null;
				if (_wifiStep == 0)
					clickedDrawable = getResources().getDrawable(R.drawable.wifi0);

				else if (_wifiStep == 1)
					clickedDrawable = getResources().getDrawable(R.drawable.wifi1);

				else if (_wifiStep == 2)
					clickedDrawable = getResources().getDrawable(R.drawable.wifi2);

				else if (_wifiStep == 3)
					clickedDrawable = getResources().getDrawable(R.drawable.wifi3);

				SetImage(clickedDrawable);

				if (_wifiStep == 3)
					_wifiStep = 0;
				else
					_wifiStep++;
			}
		}, 500, 500);

	}

	private void SetImage(final Drawable draw) {
		runOnUiThread(new Runnable() {
			public void run() {
				_imageView.setImageDrawable(draw);
			}
		});
	}

	private void SetTextInfo(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				_textViewInfo.setText(str);
			}
		});
	}

	private void SetTextError(final String str) {
		runOnUiThread(new Runnable() {
			public void run() {
				_textViewError.setText(str);
			}
		});
	}

	private void StopLoopAnimation() {
		if (_timer != null) {
			_timer.cancel();
			_timer.purge();
		}

		SetImage(getResources().getDrawable(R.drawable.wifi0));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.load_app, menu);
		return true;
	}

	private OnScanInterruptedListener OnScanInterrupted = new OnScanInterruptedListener() {

		@Override
		public void IpFound(DatagramSocket socket) {
			StopLoopAnimation();
			SetTextInfo("Connected");
			Connection.CreateConnection(socket);
			Intent Intent = new Intent(getApplicationContext(), MainActivity.class);
			finish();
			startActivity(Intent);

		}

		@Override
		public void ErrorHappened(henry.airmouse3.ErrorCode error) {

			StopLoopAnimation();
			SetTextInfo("Not Connected");
			if (Scan.ERROR == ErrorCode.Wifi_Not_Connected) {
				SetTextError("WIFI is not connected, please enable Wifi.");

			} else if (Scan.ERROR == ErrorCode.Time_Out) {
				String ssid = Scan.GetCurrentSsid(getApplicationContext());
				SetTextError("No Server found in " + ssid + ". Please ensure AirMouse Server is running on a pc connected to " + ssid
						+ "\n You can download AirMouse Server for Windows from http://www.airmouse.com");
			} else {
				SetTextError("WIFI unknown error");
			}

		}
	};

}
