package server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.security.KeyStore;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class IntercomConnection  extends Thread implements HandshakeCompletedListener{
	private static String serverIP = "ipgoeshere";
	private static int serverPort = 3802;
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
	
	public IntercomConnection(){
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
	}
	private synchronized void stayConnected(){
		this.stayConnected = true;
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
	
	public void run(){
		while(isStayConnected()){
			SSLSocket sslSocket = null;
			try {
				sslSocket = (SSLSocket) sslSocketFactory.createSocket(serverIP,serverPort);
				sslSocket.addHandshakeCompletedListener(this);
				sslSocket.startHandshake();
				while(!isConnected()){
					this.wait(10);	
				}
				
			} catch (Exception e) {
				System.out.println("SSL socket creation failed due to: " + e.toString());
				setConnected(false);
			}
			
		}
	}
	@Override
	public void handshakeCompleted(HandshakeCompletedEvent arg0) {
		System.out.println("SSL handshake with intercom server compleated");
		setConnected(true);
	}

}
