package jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;

public class JettyServer {
	private Server server;

	public JettyServer() {
		this(16783);
	}

	public JettyServer(int runningPort) {
		System.out.println("Är jag startad 1? " + server.isStarted());
		server = new Server(runningPort);
		System.out.println("Är jag startad 2? " + server.isStarted());
	}

	public void setHandler(ContextHandlerCollection contexts) {
		server.setHandler(contexts);
	}

	public void start() throws Exception {
		server.start();
	}

	public void stop() throws Exception {
		server.stop();
		server.join();
	}

	public boolean isStarted() {
		return server.isStarted();
	}

	public boolean isStopped() {
		return server.isStopped();
	}
}
