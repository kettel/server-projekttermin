package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servern som hanterar anslutningar mellan olika klienter
 * 
 * *******Fyll på*********
 * 
 * @author kristoffer & nikola
 * 
 */
public class Server {

	// Porten som används för anslutningar till servern
	private static final int port = 17234;
	// Tillåter klienter att ansluta till servern
	private static ServerSocket serverSocket = null;
	// En boolean som avgör om servern lyssnar på anslutningar
	private static boolean listening = true;
	private static ConcurrentHashMap<String, OutputStream> hashMap;
	private static OutputStream out = null;

	public static void main(String[] args) {
		try {
			hashMap = new ConcurrentHashMap<String, OutputStream>();
			serverSocket = new ServerSocket(port);

			// Skapar en ny tråd som lyssnar på kommandon
			new ServerTerminal().start();
			// Lyssnar på anslutningar och skapar en ny tråd per anslutning så
			// länge servern lyssnar efter anslutningar
			while (listening) {
				Socket client = serverSocket.accept();
				out = client.getOutputStream();
				// skapa output
				// länka med ip med client.getoutput?
				new MultiServerThread(client, hashMap).start();
				hashMap.put(client.getInetAddress().toString(), out);
			}

			// Stänger socketen, anslutningar är inte längre tillåtna
			serverSocket.close();
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static void send(String msg, OutputStream output) {
		System.out.println("SKICKARÅÅÅÅÅÅÅÅÅ");
		PrintWriter pr = new PrintWriter(output, true);
		pr.write(msg);
	}
}
