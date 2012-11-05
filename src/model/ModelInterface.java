package model;


public interface ModelInterface {

	/**
	 * Returnerar datatyp f�r den aktuella modellen
	 * @return		String		namn p� datatypen
	 */
	
	public String getDatabaseRepresentation();

	public long getId();
}
