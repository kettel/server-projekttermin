package replication;

import model.ModelInterface;

public class ReplicationManager {

	private Replication replication;

	public ReplicationManager(ModelInterface m) {
		replication = new Replication(18234);
		replication.start();
		replication.sendReplicationData(m);
	}
}
