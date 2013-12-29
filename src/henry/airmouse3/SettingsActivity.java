package henry.airmouse3;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;




public class SettingsActivity extends Activity {

	EditText _editTextMinMovement;
	EditText _editTextMinWheelMovement;
	EditText _editTextMilliSecondsToUpdate;
	EditText _editTextAcceleration;
	EditText _editTextPort;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		_editTextMinMovement = (EditText) findViewById(R.id.EditTextMinMovement);
		_editTextMilliSecondsToUpdate = (EditText) findViewById(R.id.EditTextMillisecondstoUpdate);
		_editTextAcceleration = (EditText) findViewById(R.id.EditTextAcceleration);
		_editTextMinWheelMovement = (EditText) findViewById(R.id.EditTextMinWheelMovement);
		_editTextPort= (EditText) findViewById(R.id.editTextPort);
	}
	
	private void LoadSettingsToUI()
	{
		_editTextMinMovement.setText(String.valueOf(Settings.getMIN_MOVEMENT()));
		_editTextMinWheelMovement.setText(String.valueOf(Settings.getMIN_WHEEL_PIXELS()));
		_editTextMilliSecondsToUpdate.setText(String.valueOf(Settings.getMIN_MILLIS_TOPDATE()));
		_editTextAcceleration.setText(String.valueOf(Settings.getMOTION_FACTOR()));
		_editTextPort.setText(String.valueOf(Settings.getPORT()));
	}
	
	private void SaveSettingsFromUI()
	{
		Settings.setMIN_MOVEMENT(Integer.valueOf(_editTextMinMovement.getText().toString()));
		Settings.setMIN_WHEEL_PIXELS(Integer.valueOf(_editTextMinWheelMovement.getText().toString()));
		Settings.setMIN_MILLIS_TOPDATE(Integer.valueOf(_editTextMilliSecondsToUpdate.getText().toString()));
		Settings.setMOTION_FACTOR(Integer.valueOf(_editTextAcceleration.getText().toString()));
		Settings.setPORT(Integer.valueOf(_editTextPort.getText().toString()));
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		LoadSettingsToUI();

	}

	@Override
	protected void onPause() {
		super.onPause();
		SaveSettingsFromUI();
	};
}
