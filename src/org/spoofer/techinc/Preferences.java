package org.spoofer.techinc;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * Preferences manages the user adjustable settings of the application.
 * It is a simple abstraction of the Android SharedPreferences service, with getters and setters for each of the induvidual settings
 * used by the application.
 * 
 * @author rob gilham
 *
 */
public class Preferences {

	private static final String LOG_TAG = Preferences.class.getSimpleName();
	
	private static final String PREF_POLL_TIME = "poll_time";	// Preference poll time
	private static final String PREF_POLL_URL = "poll_url";	// Preference poll URL
	private static final String PREF_OPEN_URL = "open_url";		// Preference open URL
	private static final String PREF_BOOT_START = "boot_start"; // Flag to turn on auto startup
	private static final String PREF_NOTIFY_VIBRATE = "notify_vibrate";
	private static final String PREF_NOTIFY_SOUND = "notify_sound";


	private static final String PREFERENCE_NAME = "techinc";	// Name of the private preferences


	public static final long DEFAULT_POLL_TIME = 3 * 60;  // Three minutes
	public static final String DEFAULT_POLL_URL = "http://techinc.nl/space/spacestate";  // URL to retrieve current state
	public static final String DEFAULT_OPEN_URL = "http://techinc.nl/";  // Page to visit when invoking Notification
	public static final boolean DEFAULT_BOOT_START = true;
	public static final boolean DEFAULT_NOTIFY_VIBRATE = false;
	public static final String DEFAULT_NOTIFY_SOUND = ""; // No notify sound by default
	

	private final SharedPreferences sharedPrefs;


	private Map<String, Object> changedProperties;
	
	
	/**
	 * Create a new Preferences object, using the given Context.
	 * 
	 * @param context A Complex to use to access the SharedPreferences.
	 */
	public Preferences(Context context) {
		sharedPrefs = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
	}

	/**
	 * Gets the named value from preferences.
	 * IF the given name does not exist, the given default value is returned.
	 * If the named preference has been updated since the last save, then the new value
	 * is changed.
	 * 
	 * @param name The named of the value
	 * @param defaultValue the value to return if the named value does not exist / has never been set since the last clear.
	 * @return the last updated value of the named preference or the default value if no named preference is returned.
	 */
	public Object getValue(String name, Object defaultValue) {
		boolean isChanged = isDirty() && changedProperties.containsKey(name);
		
		return isChanged ? changedProperties.get(name) : getPrefValue(name, defaultValue);
	}
	/**
	 * Sets the named preference with the given value.
	 * The value is NOT committed to persistent storage until the preferences are commited.
	 * @param name The name of the preference
	 * @param value The value of the Prefernce.
	 */
	public void setValue(String name, Object value) {
		if (null == changedProperties)
			changedProperties = new HashMap<String, Object>();
		
		changedProperties.put(name, value);
	}
 
	
	
	/**
	 * Checks if any values have been updated.
	 * If any value has been set but not yet committed to the persistent store this will return true.
	 * @return
	 */
	public boolean isDirty() {
		return null != changedProperties && changedProperties.size() > 0;
	}
	
	
	
	
	
	
	/**
	 * Gets the URL to use to navigate the browser when the user selects the Notification.
	 * 
	 * @return The URL navigated to when selecting the Notification.
	 */
	public String getOpenURL() {
		return getValue(PREF_OPEN_URL, DEFAULT_OPEN_URL).toString();
	}

	/**
	 * Sets the URL to use to navigate the browser when the user selects the Notification.
	 * 
	 * @param openURL The URL navigated to when selecting the Notification.
	 * @throws IllegalArgumentException if the openURL is an invalid URL format.
	 * Does not check if the URL actually points to anything valid.
	 */
	public void setOpenURL(String openURL) throws IllegalArgumentException {
		String openURLChecked = URI.create(openURL).toASCIIString();
		setValue(PREF_OPEN_URL, openURLChecked);
	}

	/**
	 * Gets the URL used to gather the state of the space.
	 * This URL is expected to return fixed, known string 'open' or 'closed'
	 * as simple text on the first line of the response.
	 * 
	 * @return the URL to poll for the current state.
	 */
	public String getPollURL() {
		return getValue(PREF_POLL_URL, DEFAULT_POLL_URL).toString();
	}

	/**
	 * Sets the URL used to gather the state of the space.
	 * 
	 * @param pollURL the URL used to gather the state of the space.
	 * 
	 * @throws IllegalArgumentException if the given pollURL is not a valid URL format.
	 */
	public void setPollURL(String pollURL) throws IllegalArgumentException {
		String pollURLChecked = URI.create(pollURL).toASCIIString();
		setValue(PREF_POLL_URL, pollURLChecked);
	}

	/**
	 * gets the time delay between each polling of the remote site to gather the latest state.
	 * The value is in seconds.  Setting the value to 60 would poll the site every minute.
	 * The default value is 180 or 3 minutes.
	 * 
	 * @return the number of seconds between each polling of the remote site.
	 */
	public long getPollDelay() {
		return Long.valueOf((Long)getValue(PREF_POLL_TIME, DEFAULT_POLL_TIME));

	}
	public void setPollDelay(long pollDelay) throws IllegalArgumentException {
		if (pollDelay < 1) {
			Log.e(LOG_TAG, "poll delay invalid, must be a minimum of one second. reverting to default of " + DEFAULT_POLL_TIME);
			pollDelay = DEFAULT_POLL_TIME;
		}
		
		setValue(PREF_POLL_TIME, pollDelay);

	}


	
	public boolean getStartOnBoot() {
		return Boolean.valueOf((Boolean)getValue(PREF_BOOT_START, DEFAULT_BOOT_START));
	}
	public void setStartOnBoot(boolean startOnBoot) {
		setValue(PREF_BOOT_START, startOnBoot);
	}
	
	
	public boolean getVibrateNotify() {
		return Boolean.valueOf((Boolean)getValue(PREF_NOTIFY_VIBRATE, DEFAULT_NOTIFY_VIBRATE));
	}
	public void setVibrateNotify(boolean vibrate) {
		setValue(PREF_NOTIFY_VIBRATE, vibrate);
	}
	
	
	public String getNotifySound() {
		return getValue(PREF_NOTIFY_SOUND, DEFAULT_NOTIFY_SOUND).toString();
	}
	public void setNotifySound(String notifySound) {
		setValue(PREF_NOTIFY_SOUND, notifySound);
	}
	
	
	/**
	 * Saves any updated preferences to the persistent store.
	 * If any values have changed since the last commit then these values will be written into the store.
	 * if no changes have been made, this does nothing.
	 * 
	 */
	public void commit() {
		if (isDirty()) {
			
			Editor editor = sharedPrefs.edit();
			
			for (Map.Entry<String, Object> pref : changedProperties.entrySet()) {
				if (Boolean.class.isAssignableFrom(pref.getValue().getClass()) )
					editor.putBoolean(pref.getKey(), (Boolean)pref.getValue());
				
				else if (Long.class.isAssignableFrom(pref.getValue().getClass()) )
					editor.putLong(pref.getKey(), (Long)pref.getValue());
				
				else if (Integer.class.isAssignableFrom(pref.getValue().getClass()) )
					editor.putInt(pref.getKey(), (Integer)pref.getValue());
				
				else
					editor.putString(pref.getKey(), pref.getValue().toString());
			}
			
			changedProperties.clear();
			editor.commit();

		}
		
	}

	/**
	 * Gets the named preference from the shared preferences.
	 * The values type is based on the given default value.
	 * If the defaultValue is a Boolean, then the shared preferences are searched for a Boolean with the given name.
	 * 
	 * @param name the name of the preference
	 * @param defaultValue the defualt value if the named prefernce is not set
	 * 
	 * @return The saved value for the given name or the given default value f the name is not known.
	 */
	private Object getPrefValue(String name, Object defaultValue) {
		
		Object result;
		
		if (Boolean.class.isAssignableFrom(defaultValue.getClass()) )
			result = sharedPrefs.getBoolean(name, (Boolean)defaultValue);
		
		else if (Long.class.isAssignableFrom(defaultValue.getClass()) )
			result = sharedPrefs.getLong(name, (Long)defaultValue);
		
		else if (Integer.class.isAssignableFrom(defaultValue.getClass()) )
			result = sharedPrefs.getInt(name, (Integer)defaultValue);
		
		else
			result = sharedPrefs.getString(name, defaultValue.toString());
		
		return result;
	}
	
	/**
	 * Clears ALL settings previously saved and reverts values back to default values.
	 */
	public void reset() {
		Editor editor = sharedPrefs.edit();
		editor.clear();
		editor.commit();
		
		if (isDirty())
			changedProperties.clear();
	}

	/*
	private void clearValue(String key) {
		Editor editor = sharedPrefs.edit();
		editor.remove(key);
		editor.commit();
		
		if (isDirty())
		    changedProperties.remove(key);
		
	}*/
		

}