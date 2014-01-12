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

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ToggleButton;




public class SettingsActivity extends Activity {

	EditText _editTextMinMovement;
	EditText _editTextMinWheelMovement;
	EditText _editTextMilliSecondsToUpdate;
	EditText _editTextAcceleration;
	EditText _editTextPort;
	ToggleButton _toggleSwitchButtons;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		_editTextMinMovement = (EditText) findViewById(R.id.EditTextMinMovement);
		_editTextMilliSecondsToUpdate = (EditText) findViewById(R.id.EditTextMillisecondstoUpdate);
		_editTextAcceleration = (EditText) findViewById(R.id.EditTextAcceleration);
		_editTextMinWheelMovement = (EditText) findViewById(R.id.EditTextMinWheelMovement);
		_editTextPort= (EditText) findViewById(R.id.editTextPort);
		_toggleSwitchButtons=(ToggleButton) findViewById(R.id.toggleButtonSwitchButtons);
	}
	
	private void LoadSettingsToUI()
	{
		_editTextMinMovement.setText(String.valueOf(Settings.getMIN_MOVEMENT()));
		_editTextMinWheelMovement.setText(String.valueOf(Settings.getMIN_WHEEL_PIXELS()));
		_editTextMilliSecondsToUpdate.setText(String.valueOf(Settings.getMIN_MILLIS_TOPDATE()));
		_editTextAcceleration.setText(String.valueOf(Settings.getMOTION_FACTOR()));
		_editTextPort.setText(String.valueOf(Settings.getPORT()));
		_toggleSwitchButtons.setChecked(Settings.getSWITCH_BUTTONS());
	}
	
	private void SaveSettingsFromUI()
	{
		Settings.setMIN_MOVEMENT(Integer.valueOf(_editTextMinMovement.getText().toString()));
		Settings.setMIN_WHEEL_PIXELS(Integer.valueOf(_editTextMinWheelMovement.getText().toString()));
		Settings.setMIN_MILLIS_TOPDATE(Integer.valueOf(_editTextMilliSecondsToUpdate.getText().toString()));
		Settings.setMOTION_FACTOR(Integer.valueOf(_editTextAcceleration.getText().toString()));
		Settings.setPORT(Integer.valueOf(_editTextPort.getText().toString()));
		Settings.setSWITCH_BUTTONS(_toggleSwitchButtons.isChecked());
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
