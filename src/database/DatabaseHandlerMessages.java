package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.MessageModel;
import model.ModelInterface;

public class DatabaseHandlerMessages extends DatabaseHandler {

	private Connection con = null;
	private Statement st = null;
	private ResultSet rs = null;
	private PreparedStatement pst = null;

	@Override
	public void addModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			MessageModel message = (MessageModel) m;

			// Initiera en anslutning till databasen
			con = DriverManager.getConnection(url, user, password);

			// SQL-frågan
			pst = con
					.prepareStatement("INSERT INTO message(Content, Receiver, Sender, MessageTimestamp, IsRead) "
							+ "VALUES (AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?),"
							+ "AES_ENCRYPT(?,?))");

			// Sätt in rätt värden till rätt plats i frågan
			pst.setString(1, message.getMessageContent().toString());
			pst.setString(2, AES_PASSWORD);
			pst.setString(3, message.getReciever().toString());
			pst.setString(4, AES_PASSWORD);
			pst.setString(5, message.getSender());
			pst.setString(6, AES_PASSWORD);
			pst.setString(7, Long.toString(message.getMessageTimeStamp()));
			pst.setString(8, AES_PASSWORD);
			pst.setString(9, Boolean.toString(message.isRead()));
			pst.setString(10, AES_PASSWORD);

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
	public List<ModelInterface> getAllModels(ModelInterface m) {

		List<ModelInterface> returnList = new ArrayList<ModelInterface>();
		try {
			con = DriverManager.getConnection(url, user, password);
			pst = con.prepareStatement("SELECT Id," + " AES_DECRYPT(Content,?)," +
					"AES_DECRYPT(Receiver,?)," +
					"AES_DECRYPT(Sender,?)," +
					"AES_DECRYPT(MessageTimestamp,?)," +
					"AES_DECRYPT(IsRead,?) FROM message");
			for(int i = 1; i < 6; i++){
				pst.setString(i, AES_PASSWORD);
			}
			rs = pst.executeQuery();

			while (rs.next()) {
				MessageModel tempMess = new MessageModel(rs.getInt(1), // Id
						rs.getString(2), // Content
						rs.getString(3), // Receiver
						rs.getString(4), // Sender
						Long.valueOf(rs.getString(5)), // Timestamp
						Boolean.parseBoolean(rs.getString(6))); // isRead
				returnList.add((ModelInterface) tempMess);
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
		return returnList;
	}

	@Override
	public void updateModel(ModelInterface m) {
		try {
			// Casta ModelInterface m till MessageModel
			MessageModel message = (MessageModel) m;

			con = DriverManager.getConnection(url, user, password);
			st = con.createStatement();

			// Sätt autocommit till falskt
			con.setAutoCommit(false);

			// Sätt in rätt värden till rätt plats i frågan och uppdatera dessa
			st.executeUpdate("UPDATE " + message.getDatabaseRepresentation()
					+ " SET Content = AES_ENCRYPT(\"" + message.getMessageContent()	+ "\",\""+AES_PASSWORD+"\")," +
					" Receiver = AES_ENCRYPT(\"" + message.getReciever() + "\",\""+AES_PASSWORD+"\")," +
					" Sender = AES_ENCRYPT(\"" + message.getSender() + "\",\""+AES_PASSWORD+"\")," +
					" IsRead = AES_ENCRYPT(\"" + Boolean.toString(message.isRead())	+ "\",\""+AES_PASSWORD+"\")" +
					" WHERE Id = " + message.getId());

			// Commita db-uppdateringarna (?)
			con.commit();

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

}
