package org.spoofer.techinc.state;

import java.io.IOException;
import java.net.MalformedURLException;

import android.util.Log;

/**
 * The Preview Reader will retrieve a State from a given URL
 * 
 * @author rob gilham
 *
 */
public class StateReader extends PreviewReader {
	
	private static final String LOG_TAG = StateReader.class.getSimpleName();

	private static final String STATE_OPEN = "open";
	//private static final String STATE_CLOSED = "closed";
	
	
	public StateReader(String stateLocation) throws MalformedURLException {
		super(stateLocation);
	}
	
	
	public boolean getState() throws IOException {
		String value = getPreview(1);
		boolean state = STATE_OPEN.equalsIgnoreCase(value.trim()); 
		Log.v(LOG_TAG, "Retrieving STATE as " + value);
		return state;
	}
		
}
