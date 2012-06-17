package org.spoofer.techinc.state;

import java.io.IOException;

import org.spoofer.techinc.Preferences;
import org.spoofer.techinc.R;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * This Service retrieves the state of the hacker space from its POLL URL
 * and sets the Notification state
 * @author robgilham
 *
 */
public class StateEngine extends Service {




	private static final String LOG_TAG = StateEngine.class.getSimpleName();

	private static final int NOTIFY_ID = 1;

	private Preferences preferences;


	private static final String EXTRA_LAST_STATE = null;
	private Boolean lastState = null; // null = unknown state.


	private static final String WORKER_THREADNAME = "state_worker_thread";
	private Thread workerThread = null;
	private Handler guiHandler = new Handler();


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		Log.d(LOG_TAG, "State Engine is starting");

		if (null == preferences)
			preferences = new Preferences(getApplicationContext());

		if (intent.hasExtra(EXTRA_LAST_STATE))
			lastState = Boolean.valueOf(intent.getBooleanExtra(EXTRA_LAST_STATE, false));

		if (null == workerThread) {
			workerThread = new Thread(checkState, WORKER_THREADNAME);
			workerThread.start();
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		workerThread = null;
		lastState = null;
		Log.d(LOG_TAG, "State Engine is stopping");
		removeNotification();
	}



	/**
	 * The Run task to Poll the state.
	 *  
	 */
	private Runnable checkState = new Runnable() {

		@Override
		public void run() {

			Log.v(LOG_TAG, "starting worker thread to check state");
			
			if (null == workerThread) {
				Log.v(LOG_TAG, "worker thread aborting as engine has been stopped");
				return;
			}
			String pollURL = preferences.getPollURL();
			StateReader stateReader;

			Log.v(LOG_TAG, "Checking state with " + pollURL);

			try {
				stateReader = new StateReader(pollURL);

				boolean state = stateReader.getState();
				
				String prevState = null == lastState ? "unknown" : (lastState ? "open" : "closed");
				String currentState = state ? "open" : "closed";
				Log.d(LOG_TAG, "previous state is " + prevState + ", current state is " + currentState);
				
				if (null == lastState || state != lastState.booleanValue())
					showNotification(state);
				
				if (null != workerThread)
					scheduleNextStartUp(state);

			} catch (IOException e) {
				e.printStackTrace();
				Log.e(LOG_TAG, "Failed to read current state " + e.getMessage(), e);
				postMessage(e.getMessage());
			}

			workerThread = null;
		}


		private void postMessage(final String message) {
			guiHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
				}
			});
		}
	};



	private void showNotification(boolean state) {

		Log.d(LOG_TAG, "showing notficiation for " + (state ? "open" : "closed"));

		Notification notification = buildNotification(state);
		NotificationManager notifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.notify(NOTIFY_ID, notification);
	}

	private void removeNotification() {
		NotificationManager notifyManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		notifyManager.cancel(NOTIFY_ID);
	}


	private Notification buildNotification(boolean state) {

		long when = System.currentTimeMillis();

		String openURL = preferences.getOpenURL();
		
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(openURL));
		PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		Notification notify;
		String titleText = getString(R.string.app_name);
		String tickerText;
		int icon;

		if (state) {
			tickerText = getString(R.string.notify_open);
			icon = R.drawable.techinclogo;

		}else{
			tickerText = getString(R.string.notify_closed);
			icon = R.drawable.techinclogo_mono;
		}

		notify = new Notification(icon, tickerText, when);
		notify.setLatestEventInfo(getApplicationContext(), titleText, tickerText, pi);

		//TODO: Read other preferences for notification, such as Sound file name, vibrate, etc
		if (preferences.getVibrateNotify())
			notify.vibrate = new long[]{0, 100, 100, 100, 100, 100};

		String soundURI = preferences.getNotifySound();
		if (null != soundURI &&  soundURI.length() > 0)
			notify.sound = Uri.parse(soundURI);
		return notify;
	}

	private void scheduleNextStartUp(boolean currentState) {

		Context context = getApplicationContext();
		long interval = preferences.getPollDelay() * 1000;
		long triggerAtTime = System.currentTimeMillis() + interval;

		Intent pollingRestart = new Intent(context, StateEngine.class);
		pollingRestart.putExtra(EXTRA_LAST_STATE, currentState);

		PendingIntent operation = PendingIntent.getService(context, 0, pollingRestart, PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, triggerAtTime, operation);

	}



}
