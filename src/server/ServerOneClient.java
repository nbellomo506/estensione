package server;

import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;

import database.DatabaseConnectionException;
import database.DbAccess;
import data.Data;
import data.NoDataException;
import clustering.HierachicalClusterMiner;
import clustering.NegativeDepthException;
import clustering.InvalidDepthException;
import distance.*;

/** ServerOneClient
 * 
 * Classe che mette in comunicazione il server con il client.
 *
 */
public class ServerOneClient extends Thread {

    private Socket socket; // Socket per la comunicazione con il client
    private ObjectInputStream in; // Stream per ricevere oggetti dal client
    private ObjectOutputStream out; // Stream per inviare oggetti al client
    private DbAccess db; // Accesso al database
	private String tabella;

    public ServerOneClient(Socket s) throws IOException {
    	tabella = "";
        socket = s; // Inizializza il socket
        in = new ObjectInputStream(socket.getInputStream()); // Inizializza lo stream di input
        out = new ObjectOutputStream(socket.getOutputStream()); // Inizializza lo stream di output
        out.flush();
        db = new DbAccess(); // Crea un'istanza di DbAccess per interagire con il database
        start(); // Avvia il thread
    }
    
    // Metodo per controllare se una tabella esiste nel database
    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next(); // Restituisce true se esiste almeno una corrispondenza
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Legge il nome della tabella
                tabella = (String) in.readObject();
                
                // Verifica l'esistenza della tabella
                String message;
                try {
                    if (tableExists(db.getConnection(), tabella)) {
                        message = "OK";
                    } else {
                        message = "La tabella " + tabella + " non esiste nel database.";
                    }
                } catch (SQLException | DatabaseConnectionException e) {
                    message = "Errore durante la verifica della tabella: " + e.getMessage();
                }

                // Invia la risposta al client
                out.writeObject(message);

                // Se la tabella non esiste, continua con la prossima iterazione
                if (!message.equals("OK")) {
                    continue;
                }

                // Legge la scelta del client
                int scelta = (Integer) in.readObject();
                
                switch (scelta) {
                    case 1: {
                        String nomeFile = (String) in.readObject(); // Legge il nome del file
                        String risposta = "OK";
                        String dendrogrammaFile = null;
                        try {
                        	HierachicalClusterMiner minerFile = HierachicalClusterMiner.loadHierachicalClusterMiner(nomeFile); // Carica il miner dal file
                        	dendrogrammaFile = minerFile.toString(); // Ottiene il dendrogramma
                        }
                      
                        catch(FileNotFoundException e) {
                        	risposta = "Il file specificato è inesistente";
                        	
                        }catch(Exception e) {
                        	risposta = "Errore in fase di caricamento del miner da file";
                        }
                        finally {
                            out.writeObject(risposta); // Invia la risposta al client
                        }
             
                        if(risposta == "OK")
                        	out.writeObject(dendrogrammaFile); // Invia il dendrogramma al client
                        break;
                    }
                    case 2: {
                        int depth = (Integer) in.readObject(); // Legge la profondità richiesta
                        int dType = (Integer) in.readObject(); // Legge il tipo di distanza
                        ClusterDistance distance = null;
                        if(dType == 1)
                        	distance = new SingleLinkDistance();
                        else
                        	distance = new AverageLinkDistance();

                   
                        // Apprende il dendrogramma e gestisce eventuali eccezioni
                        String dendrogramma = null;
                        String risposta = "OK";
                        HierachicalClusterMiner minerDB = null;
                        
                        Data data = null;
						try {
							data = new Data(tabella);
						} catch (NoDataException e) {
							// TODO Auto-generated catch block
							risposta = "Errore. La tabella indicata è vuota";
						}
                        try {
                            // Creazione di un'istanza di HierachicalClusterMiner con profondità
                            minerDB = new HierachicalClusterMiner(depth); 
                            minerDB.mine(data,distance); // Apprende il dendrogramma
                            dendrogramma = minerDB.toString();
                        }
                      
                        catch (NegativeDepthException e) {
                        	risposta = "Errore. Profondità del dendrogramma non valida.\n";
                        	risposta += "Deve essere > 0.";
                    
                        } catch (InvalidDepthException e) {
                        	risposta = "Errore. Profondità del dendrogramma non valida.\n";
                        	risposta += "Deve essere <= " + data.getNumberOfExamples();
                        	
                        } finally {
                        	//risponde al client con un ok oppure errore
                            out.writeObject(risposta);
                        }
                        
                        if(risposta != "OK")
                        	break;
                     
             
                        /* Invia il dendrogramma al client */
                        out.writeObject(dendrogramma); 
                        String fileName = (String) in.readObject(); // Legge il nome del file di salvataggio
                        String rispostaSalvataggio = "OK";
                        try {
                            minerDB.salva(fileName); // Salva il miner nel file
                        }catch(FileNotFoundException e){
                        	rispostaSalvataggio = "Impossibile salvare il file nel percorso specificato.\n"
                        			+ "Il percorso potrebbe essere errato oppure non si dispongono dei permessi\n"
                        			+ "per accedere alla cartella";
                        }catch(IOException e){
                        	rispostaSalvataggio = "Errore nel salvataggio del file, controllare che\n"
                        			+ "- Lo spazio su disco non sia esaurito;\n"
                        			+ "- Il percorso non sia protetto;\n"
                        			+ "- La scrittura non sia stata interrotta;\n"
                        			+ "- Il file system non sia corrotto.";
                        }catch(Exception e) {
                        	rispostaSalvataggio = e.getMessage();
                        }
                        finally {
                            if (rispostaSalvataggio != "OK")
                            	rispostaSalvataggio = "Operazione annullata:\n" + rispostaSalvataggio;
                            out.writeObject(rispostaSalvataggio);
                        }
                        break;

                    }
                    default:
                        System.out.println("Scelta non valida."); // Gestisce scelte non valide
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace(); // Stampa l'eccezione in caso di errore
		} finally {
            try {
                in.close(); // Chiude lo stream di input
                out.close(); // Chiude lo stream di output
                socket.close(); // Chiude il socket
                db.closeConnection(); // Assicurati di chiudere la connessione al database
            } catch (IOException e) {
                e.printStackTrace(); // Stampa l'eccezione in caso di errore
            } catch (SQLException e) {
				e.printStackTrace(); // Stampa l'eccezione in caso di errore
			}
        }
    }
}
