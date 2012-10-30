import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;


public class Provider {

	
	private static ServerSocket providerSocket;
	private static Socket connection = null;
	private static InputStreamReader streamReader;
	private static BufferedReader buffReader;
	private static String message;
	
	
	public static void main(String[] args) {
		
		Provider server = new Provider();
		while(true){
			server.run();
		}
	}
	
	private void run(){
		try{
			
			providerSocket = new ServerSocket(1304);
			connection = providerSocket.accept();
			
			streamReader = new InputStreamReader(connection.getInputStream());
			buffReader = new BufferedReader(streamReader); 
			
			
				try{
					message = buffReader.readLine();
					System.out.println("Message: " + message);
					
				}catch(IOException exception){
					System.out.println(exception);
				}
			
			
		}catch(IOException ioException){
			System.out.println(ioException);
		}
		finally{
			try{
				streamReader.close();
				buffReader.close();
				providerSocket.close();
			}catch(IOException e){
				System.out.println(e);
			}
		}
	}
}