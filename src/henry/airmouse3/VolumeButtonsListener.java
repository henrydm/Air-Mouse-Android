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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/*This must be used with a safe managed wake lock, or streaming a null audio, otherwise the broadcast won't receive anything, this feature will be implemented in future releases*/
public final class VolumeButtonsListener extends BroadcastReceiver {
	int _previousVolume;

	public VolumeButtonsListener(Context context) {
		AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		_previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
	}
	public VolumeButtonsListener() {
		
		_previousVolume = 0;
	}
	
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {

		AudioManager audio = (AudioManager) arg0.getSystemService(Context.AUDIO_SERVICE);
		int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

		int delta = _previousVolume - currentVolume;

		if (delta > 0) {

			_previousVolume = currentVolume;
		} else if (delta < 0) {

			_previousVolume = currentVolume;
		}


	}

}
