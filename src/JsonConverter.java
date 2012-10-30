import com.google.gson.Gson;

/**
 * Konverterar mellan objekt och json-filer
 * @author kristoffer & nikola
 *
 */
public class JsonConverter {

	
	private static Gson gson = new Gson();
	
	/**
	 * Konverterar ett objekt till en sträng som skrivs till en json-fil
	 * @param o
	 * Objektet som ska konverteras
	 * @return
	 * Json-fil-strängen
	 */
	public static String toJson(Object o) {

		String json = gson.toJson(o);
		return json;
	}

	/**
	 * Konverterar en sträng från en json-fil till ett objekt 
	 * @param s
	 * Json-fil-strängen som ska konverteras
	 * @return
	 * Java-objektet
	 */
	public static Object toObj(String s) {

		Object json = gson.fromJson(s, Object.class);
		return json;
	}

}
