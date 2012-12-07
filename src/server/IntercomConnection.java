package server;

import intercomModels.GPSCoordinate;
import intercomModels.LoginObject;
import intercomModels.MissionID;
import intercomModels.MissionIntergroup;
import intercomModels.MissionIntergroupUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import com.google.gson.Gson;
import database.Database;
import model.Assignment;
import model.AssignmentStatus;
import model.ModelInterface;

public class IntercomConnection  extends Thread implements HandshakeCompletedListener{
	private static String serverIP = "79.136.60.158";
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
	private SSLSocket sslSocket = null;
	private Server server = null;
	private Database db = null;
	private List<ModelInterface> list;
	
	public IntercomConnection(Server server){
		this.server = server;
		db = new Database();
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
	
	public synchronized void stayConnected(){
		this.stayConnected = true;
//		setConnected(true);
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
		System.out.println("assigment added to interkomm queue");
		String region = assigmentToSend.getRegion();
		double lon = 0;
		double lat = 0;
		if(region != null){
			lon = WgsC.WgsStringToDoubels(region)[0];
			lat = WgsC.WgsStringToDoubels(region)[1];
		}
		MissionIntergroup conveted = new MissionIntergroup(new MissionID(faction, id), new GPSCoordinate(lon, lat), assigmentToSend.getName(), assigmentToSend.getAssignmentDescription(), new Date(assigmentToSend.getTimeStamp()));
		id++;
		String outgoing = gson.toJson(conveted);
		intercomQueue.add(outgoing);
	}
	private synchronized Queue <String> getQueue(){
		return intercomQueue;
	}
	private synchronized void syncWait (int time) {
	   try {
		this.wait(time);
	} catch (InterruptedException e) {
		System.out.println("waitfail");
		e.printStackTrace();
	}
	}
	
	public void run(){
		while(isStayConnected()){
			if(!isConnected()){
				try {
					sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverIP,serverPort);
					sslSocket.addHandshakeCompletedListener(this);
					sslSocket.startHandshake();
					if(!isConnected() && isStayConnected()){
						syncWait(500);	
					}
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("SSL socket creation failed due to: " + e.toString());
					setConnected(false);
				}
			
				// get stuff thread
				new Thread(){
					public void run() {
						System.out.println("get stuff thread started anew");
						while(isConnected()){
							try {
								String incomeing = input.readLine();
								if(incomeing != null && incomeing != "&"){
									if(incomeing.contains("\"identifier\":\"@Missonintergroup@\"")){
										MissionIntergroup intercom = gson.fromJson(incomeing, MissionIntergroup.class);
										if(intercom.getId().getOrganizationChar() != faction){
											double[] latAndLon = {intercom.getLocation().getLatitude(),intercom.getLocation().getLongitude()};
											String region = WgsC.DoubelToGsonWgsString(latAndLon);
											Assignment misson = new Assignment(intercom.getTitle(),region,"intercom", false, intercom.getDescription(), "", AssignmentStatus.STARTED, "", "");
											System.out.println("New assignment from InterCommServer named: " + misson.getName());
											misson.setGlobalID(intercom.getId().idToString());
											db.addToDB(misson);
											server.sendToAll(gson.toJson(misson));
										}else{
											System.out.println("A assgiment we created was confirmed by the intercomServer");
										}
									}else if(incomeing.contains("\"identifier\":\"@MissonUpdateInter@\"")){
										System.out.println("Misson uppdate from InterCommServer");
										MissionIntergroupUpdate update = gson.fromJson(incomeing, MissionIntergroupUpdate.class);
										list = db.getAllFromDB(new Assignment());
										if (list.size() > 0) {
											for (ModelInterface m : list) {
												Assignment ass = (Assignment) m;
												if (update.getMissionId().idToString().equals(ass.getGlobalID())) {
													System.out.println("==============================UPPPDATE=======================");
													if(update.getContent().equals(MissionIntergroupUpdate.UpdateContent.DESCRIPTION)){
														ass.setAssigmentDescripton((String)update.getNewValue());
													}else if (update.getContent().equals(MissionIntergroupUpdate.UpdateContent.TITLE)) {
														ass.SetName((String)update.getNewValue());
													}else if (update.getContent().equals(MissionIntergroupUpdate.UpdateContent.LOCATION)) {
														GPSCoordinate GPS = gson.fromJson((String)update.getNewValue(), GPSCoordinate.class);
														double[] array = {GPS.getLongitude(),GPS.getLatitude()};
														ass.setRegion(WgsC.DoubelToGsonWgsString(array));
													}	
													db.updateModel(ass);
													server.sendToAll(gson.toJson(ass));
												}
											}
										}
										
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								System.out.println("Crash in intercom input thread, due to " + e.toString() + System.getProperty("line.separator") + "Dissconnecting from intercom server");
								setConnected(false);
							}
						}
					};
				}.start();
				//send stuff
				new Thread(){
					public void run(){
						System.out.println("send stuff thread started anew");
						while(isConnected()){
						try {
							 Queue <String> q = getQueue();
							if(!q.isEmpty()){
								output.println(q.peek());
								System.out.println("sending");
								if(output.checkError()){
									setConnected(false);
									System.out.println("Dissconnecting from intercom server");
								}else{
									q.poll();
								}
							}
							syncWait(50);
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
		if(isConnected()){
			return;
		}
		System.out.println("SSL handshake with intercom server compleated");
		try {
			input = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			output = new PrintWriter(sslSocket.getOutputStream(), true);
			output.println(gson.toJson(new LoginObject(faction)));
		} catch (Exception e) {
			System.out.println("input and output streamcreation  failed due to:" + e.toString() + System.getProperty("line.separator") + "Dissconnecting from intercom server");
			setConnected(false);
			return;
		}
		System.out.println("now connected with intercomServer");
		setConnected(true);
	}

}
