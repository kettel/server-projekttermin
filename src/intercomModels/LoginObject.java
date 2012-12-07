package intercomModels;

import java.io.Serializable;

/**
 * Skickar detta object till servern. Servern identifierar att det �r ett
 * loginobject som kommer in med objectStreamen (instanceof) och lagrar socketen
 * tillsammans med organisationen i en hashmap. Hashmapen anv�nds f�r att
 * referera till outputstream d�r object skrivs �ver n�tverket.
 * 
 * @author robsi807
 * 
 */
public class LoginObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private String identifier = "@login@";
	private char organizationChar;

	public LoginObject(char organizationChar) {
		this.organizationChar = organizationChar;
	}

	public char getOrganizationChar() {
		return organizationChar;
	}

}
