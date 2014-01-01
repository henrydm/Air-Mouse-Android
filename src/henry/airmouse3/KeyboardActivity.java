package henry.airmouse3;

import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputFilter.LengthFilter;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.OrientationEventListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class KeyboardActivity extends Activity {
	OrientationEventListener myOrientationEventListener;
	EditText _editText;
	String _lastTest = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_keyboard);

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

		_editText = (EditText) findViewById(R.id.customEdit1);
		_editText.addTextChangedListener(watcher);
		_editText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				boolean handled = false;
				if (actionId == EditorInfo.IME_ACTION_SEND) {
					// sendMessage();
					handled = true;
				}
				return handled;
			}

		});

	}

	TextWatcher watcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {

			// you can call or do what you want with your EditText here

		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			String f = "f";

		}

		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

			int currentLength = arg0.length();
			int lastLength = _lastTest.length();
			int diff = currentLength - lastLength;
			if (diff < 0) {
				int deletecount = Math.abs(diff);
				for (int i = 0; i > deletecount; deletecount++)
					Connection.Send("delete");
			} else {

				int from = currentLength - diff;
				int to = currentLength;
				String str = arg0.subSequence(from, to).toString();
				_lastTest = arg0.toString();
				Connection.Send("keyboard " + str);
			}
			// Connection.Send("keyboard "+arg0);

		}
	};

	private void Release() {
		if (myOrientationEventListener != null) {
			myOrientationEventListener.disable();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
