package replication;

import model.ModelInterface;

public class ReplicationManager {

	private Replication replication;

	public ReplicationManager(ModelInterface m) {
		replication = new Replication(17234);
		replication.start();
		replication.sendReplicationData(m);
	}
}
