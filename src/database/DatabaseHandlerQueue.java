package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.Contact;
import model.ModelInterface;
import model.QueueItem;

public class DatabaseHandlerQueue extends DatabaseHandler {

	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	public QueueItem pop() {
		QueueItem poppedItem = null;
		try {
			con = DriverManager.getConnection(url, user, password);
			// Hämta det sista objektet i tabellen queue.
			pst = con
					.prepareStatement("SELECT * FROM queue ORDER BY Id ASC LIMIT 1");
			rs = pst.executeQuery();

			long lastId = -1;

			// Gå igenom det sista objektet
			while (rs.next()) {
				lastId = Long.valueOf(rs.getInt(1));
				poppedItem = new QueueItem(Long.valueOf(rs.getInt(1)), // Id
						Long.valueOf(rs.getString(2)), // ContactId
						rs.getString(3)); // JSON-sträng
			}
			// Stäng DB-kopplingarna
			rs.close();
			pst.close();
			con.close();

			// Och öppna igen
			con = DriverManager.getConnection(url, user, password);
			// Ta bort det hämtade objektet
			pst = con.prepareStatement("DELETE FROM queue WHERE Id = "
					+ Long.toString(lastId));
			rs = pst.executeQuery();

		} catch (SQLException ex) {
			System.out.println("Fel: " + ex);
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
		return poppedItem;
	}

	@Override
	public void addModel(ModelInterface m) {
		QueueItem q = (QueueItem) m;
		try {
			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);

			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO queue(ContactID, json) VALUES(?,?)");

			// Sätt in rätt värden till rätt plats i frågan
			pst.setString(1, Long.toString(q.getContactId()));
			pst.setString(2, q.getJSON());

			// Utför frågan och lägg till objektet i databasen
			pst.executeUpdate();

		} catch (SQLException ex) {
			System.out.println("Fel: " + ex);
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

	@Override
	public void updateModel(ModelInterface m) {
		// TODO Auto-generated method stub

	}

	/**
	 * Förutsätter att man skickar in ett QueueItem som innehåller det contactId
	 * för den användare som man önskar hämta alla kö-items ifrån.
	 */
	@Override
	public List<ModelInterface> getAllModels(ModelInterface m) {
		List<ModelInterface> queueList = new ArrayList<ModelInterface>();
		QueueItem q = (QueueItem) m;
		try {
			con = DriverManager.getConnection(url, user, password);
			// Hämta alla kö-objekt som finns i listan kopplat till önskad
			// användare
			pst = con
					.prepareStatement("SELECT * FROM queue WHERE contact_Id = "
							+ Long.toString(q.getContactId())
							+ " ORDER BY Id ASC");
			rs = pst.executeQuery();

			// Gå igenom den funna mängden och lägg till kö-items till returlistan
			while (rs.next()) {
				queueList.add((ModelInterface) new QueueItem(
						Long.valueOf(rs.getInt(1)), // Id
						Long.valueOf(rs.getString(2)), // ContactId
						rs.getString(3))); // JSON-sträng
			}
			
		} catch (SQLException ex) {
			System.out.println("Fel: " + ex);
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
		return queueList;
	}

}
