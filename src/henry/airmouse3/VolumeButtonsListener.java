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
