/********************************************************************************************
*	Copyright(C) 2014  Enric del Molino 													*
*	http://www.androidairmouse.com															*
*	enricdelmolino@gmail.com																*
*																							*
*	This file is part of Air Mouse Client for Android.										*
*																							*
*   Air Mouse Client for Android is free software: you can redistribute it and/or modify	*
*   it under the terms of the GNU General Public License as published by					*
*   the Free Software Foundation, either version 3 of the License, or						*
*   (at your option) any later version.														*
*																							*
*   Air Mouse Client for Android is distributed in the hope that it will be useful,			*
*   but WITHOUT ANY WARRANTY; without even the implied warranty of							*
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the							*
*   GNU General Public License for more details.											*
*																							*
*   You should have received a copy of the GNU General Public License						*
*   along with Air Mouse Server for Android.  If not, see <http://www.gnu.org/licenses/>.	*
*********************************************************************************************/

package henry.airmouse3;

import java.util.Timer;
import java.util.TimerTask;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.View.OnKeyListener;

import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class KeyboardActivity extends Activity {
	OrientationEventListener myOrientationEventListener;
	EditText _editText;
	String _lastTest = "";
	Timer _sendTimer = new Timer();

	private void Send(final String str) {
		if (_sendTimer != null) {
			_sendTimer.cancel();
			_sendTimer.purge();
		}
		_sendTimer = new Timer();
		_sendTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				SendNow(str);

			}
		}, 3000);
	}

	private void SendNow(final String str) {
		Connection.Send("keyboard|" + str);
		runOnUiThread(new Runnable() {
			public void run() {
				_editText.setText("");
			}
		});

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);

		_editText = (EditText) findViewById(R.id.customEdit1);

		myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_UI) {

			@Override
			public void onOrientationChanged(int arg0) {
				if (arg0 != -1) {
					if (arg0 > 330 || arg0 < 30) {
						Release();
						finish();
					}
				}
			}
		};

		if (myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.enable();

		}

		// _editText.requestFocus();
		_editText.addTextChangedListener(watcher);
		_editText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				// You can identify which key pressed buy checking keyCode value
				// with KeyEvent.KEYCODE_
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					String text = String.valueOf(((EditText) v).getText());
					if (text.length() == 0 && event.getAction() == KeyEvent.ACTION_UP)
						Connection.Send("delete");

				}
				return false;
			}
		});

		_editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {

					if (_sendTimer != null) {
						_sendTimer.cancel();
						_sendTimer.purge();
					}

					String text = String.valueOf(v.getText());
					if (text.length() == 0)
						Connection.Send("enter");

					else
						SendNow(text);
					// sendMessage();
					handled = true;
				}
				return handled;
			}

		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onPostResume() {

		super.onPostResume();
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.showSoftInput(_editText, InputMethodManager.SHOW_FORCED);

			}
		}, 500);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.showSoftInput(_editText, InputMethodManager.SHOW_FORCED);

			}
		}, 1000);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.showSoftInput(_editText, InputMethodManager.SHOW_FORCED);

			}
		}, 1500);
		new Timer().schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				imm.showSoftInput(_editText, InputMethodManager.SHOW_FORCED);

			}
		}, 2000);

	}

	@Override
	protected void onResume() {
		if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.enable();
		}

		super.onResume();

	};

	@Override
	protected void onPause() {
		if (myOrientationEventListener != null && myOrientationEventListener.canDetectOrientation()) {
			myOrientationEventListener.disable();
		}
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(_editText, InputMethodManager.HIDE_IMPLICIT_ONLY);
		super.onPause();
	};

	TextWatcher watcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			if (arg0.length() == 0)
				return;
			Send(String.valueOf(arg0));
			// int currentLength = arg0.length();
			// int lastLength = _lastTest.length();
			// int diff = currentLength - lastLength;
			// if (diff < 0) {
			// int deletecount = Math.abs(diff);
			// for (int i = 0; i > deletecount; deletecount++)
			// Connection.Send("delete");
			// } else {
			//
			// int from = currentLength - diff;
			// int to = currentLength;
			// String str = arg0.subSequence(from, to).toString();
			// _lastTest = arg0.toString();
			// Connection.Send("keyboard " + str);
			// }
			// Connection.Send("keyboard "+arg0);

		}
	};

	private void Release() {
		if (myOrientationEventListener != null) {
			myOrientationEventListener.disable();
		}
	}

}
