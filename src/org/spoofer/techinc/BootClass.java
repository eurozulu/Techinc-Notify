package org.spoofer.techinc;

import org.spoofer.techinc.state.StateEngine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Boot Class is the Broadcast receiver that processes the boot up of the device.
 * It checks if the Service is configured to start on boot up of the device and start the service
 * as required.
 * 
 * @author rob gilham
 *
 */
public class BootClass extends BroadcastReceiver {

	private static final String LOG_TAG = BootClass.class.getSimpleName();
	

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "Device starting, checking if service should be started...");
		
		Preferences prefs = new Preferences(context);
		if (prefs.getStartOnBoot() ) {
			// Fire up the State engine to poll the state of the website.
			Intent startEngineIntent = new Intent(context, StateEngine.class);
			context.startService(startEngineIntent);
		}
	}

}