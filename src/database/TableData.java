package database;

import java.util.List;

import data.EmptySetException;
import data.Example;
import data.MissingNumberException;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

class TableData {
	
	private DbAccess db;
	
	TableData(DbAccess db){
		db = new DbAccess();
	}
	
	
/*		Comportamento: interroga la tabella con nome table nel database e restituisce la
		lista di Example memorizzata nella tabella. Solleva e propoga una istanza di:
		SLQException in caso di errore nella interrogazione, EmptySetException in caso di
		tabella vuota , MissingNumberException in presenza di attributi non numerici.
*/
	List<Example> getDistinctTransazioni(String table) throws SQLException,
	EmptySetException,MissingNumberException{
		List <Example> transactions = null;
		Connection conn = null;
		
		
		try {
			
			conn = db.getConnection();
			Statement st = conn.createStatement();
			st.executeQuery("SELECT * FROM" + table +" WHERE 1;");
			
			db.closeConnection();
		} catch (DatabaseConnectionException e) {
			
			e.printStackTrace();
		}
	
		
		//db.hashCode();
		return transactions;
		
	}

	
}
