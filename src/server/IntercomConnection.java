package server;

import intercomModels.GPSCoordinate;
import intercomModels.MissionID;
import intercomModels.MissionIntergroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.security.KeyStore;
import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import model.Assignment;
import model.AssignmentStatus;

public class IntercomConnection  extends Thread implements HandshakeCompletedListener{
	private static String serverIP = "ipgoeshere";
	private static int serverPort = 3802;
	private int id = 0;
	private char password[] = "password".toCharArray();
	private boolean stayConnected = false;
	private boolean connected = false;
	private BufferedReader input = null;
	private PrintWriter output = null;
	private char faction = 'f';
	private KeyStore keystore = null;
	private KeyManagerFactory keyMangamentFactory = null;
	private SSLContext sslContext = null;
	private TrustManagerFactory trustManagerFactory = null;
	private SSLSocketFactory sslSocketFactory = null;
	private Queue <String> intercomQueue = new LinkedList<String>();
	private WgspointConverter WgsC = new WgspointConverter();
	private Gson gson = new Gson();
	private Server server = null;
	
	public IntercomConnection(Server server){
		this.server = server;
			try {
				keystore = KeyStore.getInstance("JKS");
				keystore.load(new FileInputStream(new File(getClass().getClassLoader().getResource("server/masterserver.jks").getPath())),password);
				keyMangamentFactory = keyMangamentFactory.getInstance(keyMangamentFactory.getDefaultAlgorithm());
				keyMangamentFactory.init(keystore, password);
				trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
				trustManagerFactory.init(keystore);
				sslContext = SSLContext.getInstance("TLS");
				sslContext.init(keyMangamentFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
				sslSocketFactory = sslContext.getSocketFactory();
			} catch (Exception e) {
				System.out.println("SSL initiation failed due to " + e.toString());
			}
	}
	
	
	public synchronized void closeConncetion(){
		this.stayConnected = false;
		setConnected(false);
	}
	
	private synchronized void stayConnected(){
		this.stayConnected = true;
		setConnected(true);
	}
	
	private synchronized boolean isStayConnected(){
		return this.stayConnected;
	}
	
	private synchronized void setConnected(boolean newValue){
		this.connected = newValue;
	}
	
	private synchronized boolean isConnected(){
		return this.connected;
	}
	
	public synchronized void addIntercomAssignment(Assignment assigmentToSend){
		String region = assigmentToSend.getRegion();
		double lon = WgsC.WgsStringToDoubels(region)[0];
		double lat = WgsC.WgsStringToDoubels(region)[1];
		MissionIntergroup conveted = new MissionIntergroup(new MissionID(faction, id), new GPSCoordinate(lon, lat), assigmentToSend.getName(), assigmentToSend.getAssignmentDescription(), new Date(assigmentToSend.getTimeStamp()));
		String outgoing = gson.toJson(conveted);
		intercomQueue.add(outgoing);
	}
	private synchronized Queue <String> getQueue(){
		return intercomQueue;
	}
	
	public void run(){
		while(isStayConnected()){
			SSLSocket sslSocket = null;
			try {
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverIP,serverPort);
				sslSocket.addHandshakeCompletedListener(this);
				sslSocket.startHandshake();
				if(!isConnected() && isStayConnected()){
					this.wait(500);	
				}
			} catch (Exception e) {
				System.out.println("SSL socket creation failed due to: " + e.toString());
				setConnected(false);
			}
			if(isConnected()){
				// get stuff thread
				new Thread(){
					public void run() {
						while(isConnected()){
							try {
								String incomeing = input.readLine();
								if(incomeing != "&"){
									if(incomeing.contains("\"identifier\":\"@Missonintergroup@\"")){
										System.out.println("incoming misson fron itercom server");
										MissionIntergroup intercom = gson.fromJson(incomeing, MissionIntergroup.class);
										double[] latAndLon = {intercom.getLocation().getLatitude(),intercom.getLocation().getLongitude()};
										String region = WgsC.DoubelToGsonWgsString(latAndLon);
										Assignment misson = new Assignment(intercom.getTitle(),region,"intercom", false, intercom.getDescription(), "", AssignmentStatus.STARTED, "", "");
										server.sendToAll(gson.toJson(misson));
									}else if(incomeing.contains("\"identifier\":\"@MissonUpdateInter@\"")){
										System.out.println("uppdate, nothing done");
									}
								}
							} catch (Exception e) {
								System.out.println("Crash in intercom input thread, due to " + e.toString() + System.getProperty("line.separator") + "Dissconnecting from intercom server");
								setConnected(false);
							}
						}
					};
				}.start();
				//send stuff
				new Thread(){
					public void run(){
						while(isConnected()){
						try {
							 Queue <String> q = getQueue();
							if(!q.isEmpty()){
								output.println(q.peek());
								if(output.checkError()){
									setConnected(false);
									System.out.println("Dissconnecting from intercom server");
								}else{
									q.poll();
								}
							}
							this.wait(50);
						} catch (Exception e) {
							System.out.println("Crash in output thread due to: " + e.toString() + System.getProperty("line.separator") + "Dissconnecting from intercom server");
							setConnected(false);
						}
						}
					}
				}.start();
			}
		}
	}
	
	@Override
	public void handshakeCompleted(HandshakeCompletedEvent arg0) {
		System.out.println("SSL handshake with intercom server compleated");
		setConnected(true);
	}

}
