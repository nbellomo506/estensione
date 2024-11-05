package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import database.*;

public class Data {
   
	private List<Example> data = new ArrayList<>(); // rappresenta il dataset
    private int numberOfExamples; // rappresenta il numero di esempi nel dataset

    // Nuovo costruttore che legge i dati dal database
    public Data(String tableName) throws NoDataException {
        try {
            // Connessione al database (modificare la stringa di connessione con i dettagli reali)
        	
        	DbAccess db = new DbAccess();
        	Connection conn = null;
        	
        	try {
				db.initConnection();
				conn = db.getConnection();
			} catch (DatabaseConnectionException e) {
				
				e.printStackTrace();
			} 
            
            // Query per ottenere i dati dalla tabella specificata
            String query = "SELECT * FROM " + tableName;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            
            // Popolare il dataset con i dati della tabella
            while (rs.next()) {
                Example e = new Example();
                // Supponiamo che la tabella abbia 3 colonne di tipo DOUBLE.
                e.add(rs.getDouble(1)); // Prima colonna
                e.add(rs.getDouble(2)); // Seconda colonna
                e.add(rs.getDouble(3)); // Terza colonna
                data.add(e);
            }
            
            // Numero di esempi nel dataset
            numberOfExamples = data.size();
            
            // Chiudere la connessione al database
            rs.close();
            stmt.close();
            conn.close();
            
            if (numberOfExamples == 0) {
                throw new NoDataException("Nessun dato trovato nella tabella " + tableName);
            }
        } catch (SQLException e) {
            throw new NoDataException("Errore nel leggere i dati dal database: " + e.getMessage());
        }
    }

    public int getNumberOfExamples() {
        return numberOfExamples;
    }
    
    public Example getExample(int exampleIndex) {
        Iterator<Example> itr = data.iterator();
        Example e = itr.next();
        for (int i = 0; i < data.size(); i++) {
            if (i == exampleIndex)
                return e;
            e = itr.next();
        }
        return e;
    }
    
    public double[][] distance() {
        double[][] distances = new double[numberOfExamples][numberOfExamples];
        
        for (int i = 0; i < numberOfExamples; i++) {
            for (int j = 0; j < i; j++) {
                distances[i][j] = 0.0;
            }
            for (int j = i + 1; j < numberOfExamples; j++) {
                try {
                    distances[i][j] = getExample(i).distance(getExample(j));
                } catch (InvalidSizeException e) {
                    distances[i][j] = 0;
                }
            }
        }
        return distances;
    }
    
    @Override
    public String toString() {
        Iterator<Example> itr = data.iterator();
        StringBuilder s = new StringBuilder();
        
        for (int i = 0; i < data.size(); i++) {
            s.append(i).append(":").append(itr.next()).append("\n");
        }
        return s.toString();
    }
}
