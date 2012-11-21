package server;

/**
 * Ett interface för att skapa nya kommandon till server terminalen
 * 
 * @author kristoffer
 * 
 */
public interface CommandInterface {

	/**
	 * Den uppgift kommandot ska utföra
	 */
	public void commandTask();

	/**
	 * Det som skrivs för att kalla på kommandot
	 * 
	 * @return
	 */
	public String commandLine();

}
