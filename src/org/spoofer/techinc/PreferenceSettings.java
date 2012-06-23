package org.spoofer.techinc;

import org.spoofer.techinc.state.StateEngine;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class PreferenceSettings extends Activity {
	private static final String LOG_TAG = PreferenceSettings.class.getSimpleName();

	private Preferences preferences;
	

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(LOG_TAG, "creating preferences activity");
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (null == preferences)
			preferences = new Preferences(getApplicationContext());

		loadSettings(preferences);

		// Set up each Value View to trigger on focus change.
		// This sets the value of the Preferences, if it has changed.

		TextView openURL = (TextView)findViewById(R.id.open_url);
		openURL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String newValue = ((TextView)v).getText().toString();
				if (!preferences.getOpenURL().equals(newValue)) {
					preferences.setOpenURL(newValue);
					updateButtons(preferences);
				}
			}
		});

		CheckBox bootStart = (CheckBox)findViewById(R.id.boot_start);
		bootStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				boolean newValue = ((CheckBox)v).isChecked();
				if (!preferences.getStartOnBoot() == newValue) {
					preferences.setStartOnBoot(newValue);
					updateButtons(preferences);
				}
			}
		});

		CheckBox vibrate = (CheckBox)findViewById(R.id.vibrate);
		vibrate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				boolean newValue = ((CheckBox)v).isChecked();
				if (preferences.getVibrateNotify() != newValue) {
					preferences.setVibrateNotify(newValue);
					updateButtons(preferences);
				}
			}
		});

		TextView sound = (TextView)findViewById(R.id.notify_sound);
		sound.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String newValue = ((TextView)v).getText().toString();
				if (!preferences.getNotifySound().equals(newValue)) {
					preferences.setNotifySound(newValue);
					updateButtons(preferences);
				}
			}
		});

		TextView pollDelay = (TextView)findViewById(R.id.update_freq);
		pollDelay.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				long newValue = Long.parseLong(((TextView)v).getText().toString());
				if (preferences.getPollDelay() != newValue) {
					preferences.setPollDelay(newValue);
					updateButtons(preferences);
				}
			}
		});

		TextView pollURL = (TextView)findViewById(R.id.update_url);
		pollURL.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				String newValue = ((TextView)v).getText().toString();
				if (!preferences.getPollURL().equals(newValue)) {
					preferences.setPollURL(newValue);
					updateButtons(preferences);
				}
			}
		});


		// Set up the Button click listeners


		Button buttonStart = (Button)findViewById(R.id.start);
		buttonStart.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				startService(new Intent(getApplicationContext(), StateEngine.class));
			}
		});

		Button buttonStop = (Button)findViewById(R.id.stop);
		buttonStop.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent stopIntent = new Intent(getApplicationContext(), StateEngine.class);
				stopIntent.setAction(Intent.ACTION_SHUTDOWN);

				stopService(stopIntent);

				//startService(new Intent(getApplicationContext(), StateEngine.class));
			}
		});

		Button buttonApply = (Button)findViewById(R.id.apply);
		buttonApply.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				preferences.commit();
				loadSettings(preferences);
				updateButtons(preferences);
			}
		});

		Button buttonReset = (Button)findViewById(R.id.reset);
		buttonReset.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {

				preferences.reset();
				loadSettings(preferences);
			}
		});
		
		Button buttonBrowse = (Button)findViewById(R.id.butn_browse_sound);
		buttonBrowse.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectRingTone();
			}
			
		});

		updateButtons(preferences);
	}

	private void updateButtons(Preferences preferences) {
		
		//Button buttonStart = (Button)findViewById(R.id.start);
		//Button buttonStop = (Button)findViewById(R.id.stop);

		Button buttonApply = (Button)findViewById(R.id.apply);
		buttonApply.setEnabled(preferences.isDirty());

	}


	/**
	 * Loads the current values from the given preferences store, into the GUI places.
	 * @param preferences
	 */
	private void loadSettings(Preferences preferences) {
		TextView openURL = (TextView)findViewById(R.id.open_url);
		openURL.setText(preferences.getOpenURL());

		CheckBox bootStart = (CheckBox)findViewById(R.id.boot_start);
		bootStart.setChecked(preferences.getStartOnBoot());

		CheckBox vibrate = (CheckBox)findViewById(R.id.vibrate);
		vibrate.setChecked(preferences.getVibrateNotify());

		TextView sound = (TextView)findViewById(R.id.notify_sound);
		sound.setText(preferences.getNotifySound());

		TextView pollDelay = (TextView)findViewById(R.id.update_freq);
		pollDelay.setText(Long.toString(preferences.getPollDelay()));

		TextView pollURL = (TextView)findViewById(R.id.update_url);
		pollURL.setText(preferences.getPollURL());

	}

	private void selectRingTone() {

		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select ringtone");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,RingtoneManager.TYPE_NOTIFICATION);

		this.startActivityForResult(intent, 999);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (RESULT_OK == resultCode && 999 == requestCode) {
	    	
	    	Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	    	if (null != uri) {
	    		preferences.setNotifySound(uri.toString());
	    		loadSettings(preferences);
	    		updateButtons(preferences);
	    	}
	    }
	}



/*
	protected Dialog onCreateConfirmDialog() {

		/ **
 * Create a confirmation dialog to cler settings
 * /
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		confirmResult = Boolean.FALSE;

		builder.setMessage(getString(R.string.confirm_reset))

		.setPositiveButton(getString(R.string.confirm_yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmResult = Boolean.TRUE;
				dismissDialog(0);
			}
		})
		.setNegativeButton(getString(R.string.confirm_no), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				confirmResult = Boolean.FALSE;
			}
		});

		return builder.create();
	}*/



}