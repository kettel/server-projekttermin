package intercomModels;

import java.io.Serializable;
import java.sql.Time;

/**
 * Class used to update missions.
 * @author robsi807
 *
 */
public class MissionIntergroupUpdate implements Serializable{
	/**
	 * 
	 */
	private final String identifier = "@MissonUpdateInter@";
	private static final long serialVersionUID = 1L;
	private MissionID missionId;
	private Object newValue;
	private UpdateContent content;
	private Long timestamp;

	/**
	 * Constructor used for updating all missi
	 * @param missionId the unique ID of the mission that you want to update
	 * @param content the type of content you want to update. See enum UpdateContent for details
	 * @param newValue the value of the update as a String. REMEMBER to write an integer as a String when updating NR_OF_... Else error will find you. Use Json to convert the GPSCoordinate to a String when updating LOCATION.
	 */
	public MissionIntergroupUpdate(MissionID missionId, UpdateContent content,
			Object newValue) {
		this.missionId = missionId;
		this.content = content;
		this.newValue = newValue;
		
		if(content == UpdateContent.LOCATION){
			this.content = UpdateContent.COMMENT;
			System.out.println("THIS IS " + content.toString());
			this.newValue = "ERROR WRONG CONSTRUCTOR USED! Location was not updated! Value of the failed update was: " + newValue;
		}
		timestamp = new Time(System.currentTimeMillis()).getTime();
	}

	/**
	 * This constructor is used to update the location of a mission. For updating other values then LOCATION, see the other constructor.
	 * @param missionId the unique ID of the mission that you want to update
	 * @param location the new location of a mission as a GPSCoordinate.
	 */
	
	public MissionID getMissionId() {
		return missionId;
	}

	/**
	 * 
	 * @return the value in the update
	 */
	public Object getNewValue() {
		return newValue;
	}

	/**
	 * 
	 * @return what type of content that will be updated
	 */
	public UpdateContent getContent() {

		return content;
	}
	
	/**
	 * 
	 * @return the time when the update was created
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * The different types of possible updates. Use Json to convert the GPSCoordinate to a String when updating LOCATION.
	 * @author robsi807
	 *
	 */
	public enum UpdateContent {
		LOCATION, TITLE, DESCRIPTION, COMMENT, NR_OF_POLICE, NR_OF_FIREBRIGADE, NR_OF_MILITARY, NR_OF_PARAMEDICS;
	}
}
