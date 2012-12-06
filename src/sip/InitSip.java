package sip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import model.AuthenticationModel;
import model.ModelInterface;

/**
 * Klass för att provisionera SIP-användare
 * @author kettel
 *
 */
public class InitSip {
	List<ModelInterface> list;
	private static String externalIp;
	
	public InitSip(List<ModelInterface> list){
		System.out.println("pwd: " + pwd());
		provisionUsers(list);
		
	}
	
	public static void provisionUsers(List<ModelInterface> list) {
		externalIp = getExternalIp();
		
		provisionUsers(list);
		makeSipConf(list);
		makeExtensionsConf(list);
		sudoMoveAsteriskConf("sip.conf");
		sudoMoveAsteriskConf("extensions.conf");
		sudoReloadAsterisk();
	}
	
	/**
	 * Ladda om asterisk
	 */
	private static void sudoReloadAsterisk() {
		System.out.println("InitSip: Ska ladda om asterisk...");
		String[] cmd = {"/bin/bash","-c","echo mandelHandduk | sudo -S asterisk -x reload"}; 
		Runtime run = Runtime.getRuntime(); 
		Process pr = null; 
		try { 
			pr = run.exec(cmd); 
			System.out.println("InitSip: Laddade om asterisk");
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		try { 
			pr.waitFor(); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
		String line = ""; 
		try { 
			while ((line=buf.readLine())!=null) { 
				System.out.println(line); 
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		
	}
	private static String pwd(){
		System.out.println("InitSip: Ska hämta aktuell mapp...");
		String cmd = "pwd";
		Runtime run = Runtime.getRuntime(); 
		Process pr = null; 
		String pwd = new String();
		try { 
			pr = run.exec(cmd); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		try { 
			pr.waitFor(); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
		String line = ""; 
		try { 
			while ((line=buf.readLine())!=null) { 
				pwd = line;
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		System.out.println("InitSip: Aktuell mapp är: "+pwd);
		return pwd;
	}
	/**
	 * Skapa sip.conf för alla kontakter med lösen
	 * @param list
	 */
	private static void makeSipConf(List<ModelInterface> list){
		System.out.println("InitSip: Ska skapa sip.conf");
		try{
			// Skapa filen
			FileWriter fstream = new FileWriter("sip.conf");
			BufferedWriter out = new BufferedWriter(fstream);
			// Header
			out.write("[general]\n");
			out.write("nat = yes\n");
			out.write("externip = "+externalIp+"\n");
			out.write("fromdomain = "+externalIp+"\n");
			out.write("localnet = 192.168.1.1/255.255.255.0\n");
			out.write("qualify = yes\n");
			out.write("context = default");
			out.write("bindport = 5060\n");
			out.write("bindaddr = 0.0.0.0\n");
			out.write("tcpbindaddr = 0.0.0.0\n");
			out.write("tcpenable = yes\n");
			out.write("videosupport = yes\n");
			
			// Provisionera användare
			for(ModelInterface m : list){
				AuthenticationModel contact = (AuthenticationModel) m;
				String userName = contact.getUserName();
				out.write("[" + userName + "]\n");
				out.write("type = friend\n");
				out.write("callerid = " + userName + "<"+userName+">\n");
				out.write("secret = "+contact.getPasswordHash()+"\n");
				out.write("host = dynamic\n");
				out.write("canreinvite = no\n");
				out.write("dtmfmode = rfc2833\n");
				out.write("mailbox = 1001\n");
				out.write("disallow = all\n");
				out.write("allow = alaw\n");
				out.write("allow = ulaw\n");
				out.write("allow = h263p\n");
				out.write("transport = udp\n");
			}
			//Close the output stream
			out.close();
			System.out.println("InitSip: Skapade sip.conf");
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Skapa extensions-filen för alla kontakter
	 * @param list
	 */
	private static void makeExtensionsConf(List<ModelInterface> list){
		System.out.println("InitSip: Ska skapa extensions.conf");
		try{
			// Create file 
			FileWriter fstream = new FileWriter("extensions.conf");
			BufferedWriter out = new BufferedWriter(fstream);
			// Header
			out.write("[general]\n");
			out.write("static = yes\n");
			out.write("writeprotect = no\n");
			out.write("\n[default]\n");
			for(ModelInterface m : list){
				AuthenticationModel contact = (AuthenticationModel) m;
				String userName = contact.getUserName();
				out.write("exten => " + userName + "1,Answer()\n");
				out.write("exten => " + userName + "n,Dial(SIP/"+userName+",20,tr)\n");
				out.write("exten => " + userName + "n,Hangup\n");
			}
			//Close the output stream
			out.close();
			System.out.println("InitSip: Skapade extensions.conf");
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
	
	/**
	 * Flytta conf-filer till rätt plats
	 * @param file
	 */
	public static void sudoMoveAsteriskConf(String file) { 
		System.out.println("InitSip: Ska flytta "+file+"...");
		String[] cmd = {"/bin/bash","-c","echo mandelHandduk | sudo -S mv "+file+" /etc/asterisk/"+file}; 
		Runtime run = Runtime.getRuntime(); 
		Process pr = null; 
		try { 
			pr = run.exec(cmd); 
			System.out.println("InitSip: Lyckades flytta "+file);
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		try { 
			pr.waitFor(); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
		String line = ""; 
		try { 
			while ((line=buf.readLine())!=null) { 
				System.out.println(line); 
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
	}
	
	/**
	 * Hämta extern IP-adress
	 * @return
	 */
	public static String getExternalIp() {
		System.out.println("InitSip: Ska hämta extern IP...");
		String cmd = "curl ifconfig.me";
		Runtime run = Runtime.getRuntime(); 
		Process pr = null; 
		String externalIp = new String();
		try { 
			pr = run.exec(cmd); 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		try { 
			pr.waitFor(); 
		} catch (InterruptedException e) { 
			e.printStackTrace(); 
		} 
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream())); 
		String line = ""; 
		try { 
			while ((line=buf.readLine())!=null) { 
				externalIp = line;
			} 
		} catch (IOException e) { 
			e.printStackTrace(); 
		} 
		System.out.println("InitSip: Extern IP är: "+externalIp);
		return externalIp;
	}
}
