package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.management.InstanceNotFoundException;

/**
 * Gestisce l'accesso al DB per  la lettura dei dati di training
 * @author Map Tutor
 *
 */
public class DbAccess {

	private String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
    private  String DBMS = "jdbc:mysql";
    private  String SERVER = "localhost";
    private  String DATABASE = "mapdb";
    private  int PORT = 3306;
    private  String USER_ID = "root";
    private  String PASSWORD = "1234";

    private Connection conn;

    /**
     * Inizializza la connessione al database.
     *
     * @throws DatabaseConnectionException Eccezione lanciata se la connessione al database fallisce.
     */
    
    public void initConnection() throws DatabaseConnectionException
    {
        try {
            Class.forName(DRIVER_CLASS_NAME);
        } catch(ClassNotFoundException e) {
            System.out.println("[!] Driver not found: " + e.getMessage());
            throw new DatabaseConnectionException(e.toString());
        }
        String connectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DATABASE
        			+ "?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";

        
        try {
            conn = DriverManager.getConnection(connectionString, USER_ID, PASSWORD);
        } catch(SQLException e) {
           
            throw new DatabaseConnectionException(e.toString());
        }
    }

    /**
     * Restituisce la connessione al database.
     *
     * @return Connessione al database.
     * @throws DatabaseConnectionException Eccezione lanciata se la connessione al database fallisce.
     */
    public Connection getConnection() throws DatabaseConnectionException{
        this.initConnection();
        return conn;
    }

    /**
     * Chiude la connessione al database.
     *
     * @throws SQLException Eccezione lanciata se si verifica un errore durante la chiusura della connessione.
     */
    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            this.conn.close();
        }
    }


}
