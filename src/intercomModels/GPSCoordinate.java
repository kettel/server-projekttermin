package intercomModels;

import java.io.Serializable;

/**
 * Class for storing a location in longitude and latitude.
 * @author robsi807
 *
 */
public class GPSCoordinate implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double longitude, latitude;

	/**
	 * Datatype containing two doubles, longitude and latitude.
	 * @param longitude
	 * @param latitude
	 */
	public GPSCoordinate(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
