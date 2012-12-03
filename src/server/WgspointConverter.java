package server;

import com.google.gson.Gson;

public class WgspointConverter {
	private Gson gson = new Gson();
	private String start = "[";
	private String end = "]";
	/**
	 * Empty constructor.
	 */
	public WgspointConverter(){
		
	}
	/**
	 * Creates a array of doubles from a WGS gson string.
	 * @param WGSGsonString
	 * @return
	 */
	public double[] WgsStringToDoubels(String WGSGsonString){
		WGSGsonString = WGSGsonString.replace("\"lat\":", "");
		WGSGsonString = WGSGsonString.replace("\"lon\":", "");
		WGSGsonString = WGSGsonString.replace("{", "");
		WGSGsonString = WGSGsonString.replace("}", "");
		double[] coordinates = gson.fromJson(WGSGsonString, double[].class);
		return coordinates;
	}
	/**
	 * 
	 * @param latAndLong
	 * @return
	 */
	public String DoubelToGsonWgsString(double[] latAndLon){
		String WGSString = start;
		boolean lat = true;
		if(latAndLon.length < 2 ){
			System.out.println("WGS to Gson string convertion failed, less then two items in input array");
			return null;
		}else if (latAndLon.length % 2 != 0) {
			System.out.println("WGS to Gson string convertion failed, uneven nummeber of items");
		}
		for (int i = 0; i < latAndLon.length; i++) {
			if(lat){
				WGSString = WGSString + "{\"lat\":" + latAndLon[i] +",";
				lat = false;
			}else if (!lat) {
				WGSString = WGSString +"\"lon\":" + latAndLon[i] + "},";
				lat = true;
			}
		}
		
		return (WGSString.substring(0, WGSString.length()-1) + end);

	}
}
