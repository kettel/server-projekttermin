package server;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class GCM{
	private static String myApiKey = "AIzaSyDm4alrdLjfaZTVqIyLB2YstGiR62_46hk";
	private ArrayList<String> devices = new ArrayList<String>();

	public void sendMessage() {
		loadDevices();
		Sender sender = new Sender(myApiKey);
		Message message = new Message.Builder().build();
		MulticastResult result = null;
		try {
			result = sender.send(message, devices, 5);
			for (Result r : result.getResults()) {
				if (r.getMessageId() != null) {
					String canonicalRegId = r.getCanonicalRegistrationId();
					if (canonicalRegId != null) {
						// same device has more than on registration ID: update
						// database
						System.out.println("GCM:multiple ids");
					}
				} else {
					String error = r.getErrorCodeName();
					if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
						// application has been removed from device - unregister
						// database
						System.out.println("GCM:not reg");
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void loadDevices() {
		System.out.println("load devices");
		
	}
}
