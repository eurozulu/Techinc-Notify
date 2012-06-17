package org.spoofer.techinc.state;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import android.util.Log;

/**
 * The Preview Reader will retrieve the first 'n' amount of lines from a URL request stream.
 * The input stream of the given URL is opened and lines of characters are read into a buffer.
 * 
 * 
 * @author rob gilham
 *
 */
public class PreviewReader {
	
	private static final String LOG_TAG = PreviewReader.class.getSimpleName();
	
	private final URL url;
	
	
	
	public PreviewReader(String url) throws MalformedURLException {
		this(new URL(url));
	}
		
	public PreviewReader(URL url) {
		if (null == url)
			throw new NullPointerException("URL is null");
		
		this.url = url;
	}
	
	

	public String getPreview(int lineCount) throws IOException {
				
		URLConnection connect = url.openConnection();
		connect.setDoInput(true);
		connect.setUseCaches(false);

		Log.v(LOG_TAG, "Opening connection to " + url.toExternalForm());

		connect.connect();
		
		Log.v(LOG_TAG, "connection open, reading first line response.");

		BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
		StringBuilder buffer = new StringBuilder();
		
		for (int i=0; i < lineCount; i++) {
		
			String input = in.readLine();
			Log.v(LOG_TAG, "read line " + (i + 1) + ": " + input);
			buffer.append(input);
		}
		
		Log.v(LOG_TAG, "Closing connection.");
		in.close();

		return buffer.toString();
	}
	
	public String getURL() {
		try {
			return url.toURI().toString();
		
		} catch (URISyntaxException e) {
			Log.e(LOG_TAG, "Invalid state url", e);
			e.printStackTrace();
		}
		return "";
	}
	
}
