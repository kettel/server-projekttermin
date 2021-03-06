package gcm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

public class SendAll {
	private static final int MULTICAST_SIZE = 1000;

	private Sender sender;

	private static final Executor threadPool = Executors.newFixedThreadPool(5);

	public SendAll() {
		super();
		// TODO Auto-generated constructor stub
		sender = new Sender("AIzaSyDm4alrdLjfaZTVqIyLB2YstGiR62_46hk");
	}

	public void sendAll() throws IOException {
		List<String> devices = Datastore.getDevices();
		String status;
		if (devices.isEmpty()) {
			status = "Message ignored as there is no device registered!";
			System.out.println("GCM: No devices");
		} else {
			// NOTE: check below is for demonstration purposes; a real
			// application
			// could always send a multicast, even for just one recipient
			// send a multicast message using JSON
			// must split in chunks of 1000 devices (GCM limit)
			int total = devices.size();
			List<String> partialDevices = new ArrayList<String>(total);
			int counter = 0;
			int tasks = 0;
			for (String device : devices) {
				counter++;
				partialDevices.add(device);
				int partialSize = partialDevices.size();
				if (partialSize == MULTICAST_SIZE || counter == total) {
					asyncSend(partialDevices);
					partialDevices.clear();
					tasks++;
				}
			}
			status = "Asynchronously sending " + tasks
					+ " multicast messages to " + total + " devices";
		}

		// req.setAttribute(HomeServlet.ATTRIBUTE_STATUS, status.toString());
		// getServletContext().getRequestDispatcher("/home").forward(req, resp);
	}

	public void singleSend(String id) {
		String status;
		// send a single message using plain post
		Message message = new Message.Builder().addData("action", "pull").build();
		Result result;
		try {
			result = sender.send(message, id, 5);
			status = "Sent message to one device: " + result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendLogout(String id){
		String status;
		// send a single message using plain post
		Message message = new Message.Builder().addData("logout", "true").build();
		Result result;
		try {
			result = sender.send(message, id, 5);
			status = "Sent message to one device: " + result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void asyncSend(List<String> partialDevices) {
		// make a copy
		final List<String> devices = new ArrayList<String>(partialDevices);
		threadPool.execute(new Runnable() {

			public void run() {
				Message message = new Message.Builder().addData("action", "pull").build();
				MulticastResult multicastResult;
				try {
					multicastResult = sender.send(message, devices, 5);
				} catch (IOException e) {
					System.out.println("error posting message");
					return;
				}
				List<Result> results = multicastResult.getResults();
				// analyze the results
				for (int i = 0; i < devices.size(); i++) {
					String regId = devices.get(i);
					Result result = results.get(i);
					String messageId = result.getMessageId();
					if (messageId != null) {
						System.out
								.println("Succesfully sent message to device: "
										+ regId + "; messageId = " + messageId);
						String canonicalRegId = result
								.getCanonicalRegistrationId();
						if (canonicalRegId != null) {
							// same device has more than on registration id:
							// update it
							System.out.println("canonicalRegId "
									+ canonicalRegId);
							Datastore.updateRegistration(regId, canonicalRegId);
						}
					} else {
						String error = result.getErrorCodeName();
						if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
							// application has been removed from device -
							// unregister it
							System.out.println("Unregistered device: " + regId);
							Datastore.unregister(regId);
						} else {
							System.out.println("Error sending message to "
									+ regId + ": " + error);
						}
					}
				}
			}
		});
	}
}
