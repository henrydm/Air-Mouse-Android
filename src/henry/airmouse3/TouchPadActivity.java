package henry.airmouse3;

import java.util.Date;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import java.lang.Math;

public class TouchPadActivity extends Activity {
	ImageView _touchPad;
	TextView _tvInfo;
	TextView _tvindex;
	TextView _tvCoord1;
	TextView _tvCoord2;

	private static final int ClickTime = 300;
	private static final int ClickDragTime = 900;
	int _downx0;
	int _downy0;

	int _downx1;
	int _downy1;

	int _lastx0;
	int _lasty0;

	int _lastx1;
	int _lasty1;

	int _x0;
	int _y0;

	int _x1;
	int _y1;

	boolean _motion0;
	boolean _motion1;
	boolean _isClickAndDragging;

	Date _dateLastPrimaryClick;
	Date _dateDown0;
	Date _dateDown1;
	int _pixelTolerance;
	int _pixelToleranceSqr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tochpad);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		int width = size.x;
		int height = size.y;

		_pixelTolerance = (int) ((float) (Math.min(width, height)) * Settings.getPIXEL_TOLERANCE_PERCENTAGE());
		_pixelToleranceSqr = (int) Math.pow(_pixelTolerance, 2);

		_dateLastPrimaryClick = new Date(0);
		_touchPad = (ImageView) findViewById(R.id.imageViewTouch);
		_touchPad.setOnTouchListener(OnTouchButtonLeft);
		_tvInfo = (TextView) findViewById(R.id.textViewtouchInfo);
		_tvindex = (TextView) findViewById(R.id.textViewTouchIndex);
		_tvCoord1 = (TextView) findViewById(R.id.textViewTouchCoord1);
		_tvCoord2 = (TextView) findViewById(R.id.textViewTouchCoord2);

	}

	@Override
	protected void onDestroy() {
		Connection.Disconnect();
	};

	private int DistanceSqrt(int x1, int y1, int x2, int y2) {
		return Math.abs((int) Math.pow(x2 - x1, 2) + (int) Math.pow(y2 - y1, 2));
	}

	private int Distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(DistanceSqrt(x1, y1, x2, y2));
	}

	private OnTouchListener OnTouchButtonLeft = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			int action = MotionEventCompat.getActionMasked(event);

			_tvCoord1.setText(String.valueOf(_x0 + " " + String.valueOf(_y0)));
			_tvCoord2.setText(String.valueOf(_x1 + " " + String.valueOf(_y1)));

			switch (action) {
			case android.view.MotionEvent.ACTION_MOVE:
				// Set values
				_x0 = (int) MotionEventCompat.getX(event, 0);
				_y0 = (int) MotionEventCompat.getY(event, 0);
				if (event.getPointerCount() > 1) {
					_x1 = (int) MotionEventCompat.getX(event, 1);
					_y1 = (int) MotionEventCompat.getY(event, 1);
				}

				// Process Click & Drag
				Date currentDate = new Date();
				if (currentDate.getTime() - _dateLastPrimaryClick.getTime() < ClickDragTime) {
					Connection.Send(Commands.DownLeft);
					_isClickAndDragging = true;
				}
				// Process for left click
				if (!_motion0) {
					int dist = DistanceSqrt(_x0, _y0, _downx0, _downy0);

					if (dist > _pixelToleranceSqr) {
						_motion0 = true;
					}

				}
				// Process one finger motion
				else {
					boolean swap = Settings.getTOUCHPAD_SWAP_AXIS();
					float diffx = swap ? _y0 - _lasty0 : _x0 - _lastx0;
					float diffy = swap ? _x0 - _lastx0 : _y0 - _lasty0;
					Log.i("air", "xDelta =" + diffx);
					Log.i("air", "yDelta =" + diffy);
					diffx *= Settings.getTOUCHPAD_MOTION_FACTOR();
					diffy *= Settings.getTOUCHPAD_MOTION_FACTOR();
					Connection.Send(Commands.GetDelta(diffx, diffy));
				}

				// Process for right click
				if (!_motion1) {
					int dist = DistanceSqrt(_x0, _y0, _downx0, _downy0);

					if (dist > _pixelToleranceSqr) {
						_motion0 = true;
					}

				}
				// Mouse wheel
				else {

				}

				// After process
				_lastx0 = _x0;
				_lasty0 = _y0;
				if (event.getPointerCount() > 1) {
					_lastx1 = _x1;
					_lasty1 = _y1;
				}
				break;
			case android.view.MotionEvent.ACTION_DOWN:
				_x0 = (int) MotionEventCompat.getX(event, 0);
				_y0 = (int) MotionEventCompat.getY(event, 0);
				_downx0 = _x0;
				_downy0 = _y0;
				_dateDown0 = new Date();
				break;
			case android.view.MotionEvent.ACTION_POINTER_DOWN:
				_x1 = (int) MotionEventCompat.getX(event, 1);
				_y1 = (int) MotionEventCompat.getY(event, 1);
				_downx1 = _x1;
				_downy1 = _y1;
				_dateDown1 = new Date();
				break;

			// Primary Click
			case android.view.MotionEvent.ACTION_UP:
				if (!_motion0) {
					Date currentDatePrimaryUp = new Date();
					long diff = currentDatePrimaryUp.getTime() - _dateDown0.getTime();

					if (diff < ClickTime) {
						Connection.Send(Commands.DownLeft);
						Connection.Send(Commands.UpLeft);
						_dateLastPrimaryClick = new Date();
						_tvInfo.setText("left click");// Send left click
					}

				}
				_motion0 = false;
				_x0 = -1;
				_y0 = -1;
				if (_isClickAndDragging) {
					_isClickAndDragging = false;
					Connection.Send(Commands.UpLeft);
				}
				break;

			// Secondary Click
			case android.view.MotionEvent.ACTION_POINTER_UP:
				if (!_motion1) {
					Date currentDateSecondaryUp = new Date();
					long diff = currentDateSecondaryUp.getTime() - _dateDown1.getTime();

					if (diff < ClickTime) {
						Connection.Send(Commands.DownRight);
						Connection.Send(Commands.UpRight);
						_tvInfo.setText("right click");// Send right click
					}
				}

				_motion1 = false;
				_x1 = -1;
				_y1 = -1;

				break;
			}

			return true;
		}
	};

	public static String actionToString(int action) {
		switch (action) {

		case MotionEvent.ACTION_DOWN:
			return "Down";
		case MotionEvent.ACTION_MOVE:
			return "Move";
		case MotionEvent.ACTION_POINTER_DOWN:
			return "Pointer Down";
		case MotionEvent.ACTION_UP:
			return "Up";
		case MotionEvent.ACTION_POINTER_UP:
			return "Pointer Up";
		case MotionEvent.ACTION_OUTSIDE:
			return "Outside";
		case MotionEvent.ACTION_CANCEL:
			return "Cancel";
		}
		return "";
	}

}
