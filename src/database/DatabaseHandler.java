package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import model.ModelInterface;

public abstract class DatabaseHandler {
	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;
    
	protected String url = "jdbc:mysql://localhost:3306/TDDD36";
	protected String user = "serverUser";
	protected String password = "handdukMandel";
	
    /**
     * Lägg till en modell till databasen.
     * @param m		ModelInterface 	Modellen som ska läggas in i databasen.
     */
	public abstract void addModel(ModelInterface m);
	
	public abstract void updateModel(ModelInterface m);
	
	/**
	 * Ta bort en modell från databasen
	 * @param table	String	Tabellnamnet från vilken önskad modell ska tas bort.
	 * @param id	String	Egentligen en int, men radnummer för modell som ska bort.
	 */
	public void removeModel(String table, String id) {
		try {
            con = DriverManager.getConnection(url, user, password);
            pst = con.prepareStatement("DELETE FROM " + table +" WHERE Id="+id);
            pst.executeQuery();

        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
	}
	
	/**
	 * Returnerar antalet poster i databasen för vald modell.
	 * @param table	String	Tabellen över vilken det ska räknas
	 * @return		int		Antalet rader i databasen.
	 */
	public int getTotal(String table) {
		int nofRows = 0;
		try {
			// Initiera en anslutning till databasen
            con = DriverManager.getConnection(url, user, password);
            
            // Fråga efter allt från Messages
            pst = con.prepareStatement("SELECT * FROM "+ table);
            
            // Utför frågan
            rs = pst.executeQuery();
            
            // Räkna antal rader
            while(rs.next()){
            	nofRows++;
            }
        } catch (SQLException ex) {

        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }

            } catch (SQLException ex) {
            }
        }
		return nofRows;
	}

	/**
	 * Returnerar alla modeller som en arraylist
	 * @return
	 */
	public abstract List<ModelInterface> getAllModels(ModelInterface m);

}
