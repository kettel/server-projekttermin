package intercomModels;

public interface MissionIntergroupListener {

	/**
	 * Interface used for objects that wants to listen to changes in a mission. 
	 * Listeners gets notified for each update.
	 * @param missionUpdate the update that triggers the notification
	 */
	public void missionUpdated(MissionIntergroupUpdate missionUpdate);
	
}
