package intercomModels;

import java.io.Serializable;

/**
 * The ID that is used for missions. Contains a long that is unique for each
 * mission and a char that defines what organization created the mission(
 * R�ddningstj�nsten = 'r', Polisen = 'p', Sjukv�rden = 's', F�rsvarsmakten =
 * 'f'). The ID has to be unique each organization char. Example: r00000001 and
 * p00000001 has the same ID but as the organization char is different which
 * makes the IDs unique.
 * 
 * @author robsi807
 * 
 */
public class MissionID implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private char organizationChar;
	private long idLong;

	/**
	 * Creates an object that is used to store a ID for missions.
	 * 
	 * @param organizationChar
	 *            is a unique char for each organization. R�ddningstj�nsten =
	 *            'r', Polisen = 'p', Sjukv�rden = 's', F�rsvarsmakten = 'f'
	 * @param id
	 *            an ID that is unique for each mission for a specific
	 *            organization. Organization is defined by the organizationChar
	 */
	public MissionID(char organizationChar, long id) {
		this.organizationChar = organizationChar;
		this.idLong = id;
	}
	
	/**
	 * @return a String formated as: organizationChar + id
	 */
	public String idToString(){
		return new String(organizationChar + Long.toString(idLong));
	}
	
	/**
	 * Checks if this MissionId is the same as another one.
	 * @param missionId the MissionID that you want to compare to this one
	 * @return true if they are the same
	 */
	public Boolean checkIfEqual(MissionID missionId){
		if(organizationChar == missionId.getOrganizationChar()){
			if(idLong == missionId.getIdLong()){
				return true;
			}
		}
		return false;
	}


	public char getOrganizationChar() {
		return organizationChar;
	}

	/**
	 * 
	 * @return the id long that has to be unique for each organizationChar
	 */
	public long getIdLong() {
		return idLong;
	}

}
