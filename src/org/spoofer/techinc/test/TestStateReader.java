package org.spoofer.techinc.test;

import java.io.IOException;

import junit.framework.TestCase;

import org.spoofer.techinc.state.PreviewReader;
import org.spoofer.techinc.state.StateReader;

public class TestStateReader extends TestCase {

	//private static final String TEST_URL = "http://localhost/statetest";
	private static final String TEST_URL = "http://techinc.nl/space/spacestate";

	public void testGetStringState() {
		
		try {
			PreviewReader reader = new PreviewReader(TEST_URL);
			
			String result = reader.getPreview(1);
			assertNotNull(result);
		
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	
	
	public void testGetBooleanState() {
		
		try {
			StateReader reader = new StateReader(TEST_URL);
			
			boolean result = reader.getState();
			assertFalse(result);
		
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}		
	}
	
	

}
