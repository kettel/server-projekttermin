package server;

public class intercomCommand implements CommandInterface {
	private IntercomConnection intercom = null;
	public intercomCommand(IntercomConnection intercom){
		this.intercom = intercom;
	}
	
	@Override
	public void commandTask() {
		intercom.start();
		intercom.stayConnected();		
	}

	@Override
	public String commandLine() {
		
		return "startIntercom";
	}

}
