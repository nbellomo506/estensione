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
import java.io.File;
import java.io.FileInputStream;

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
                // Leggi il tipo di operazione
                Object input = in.readObject();
                
                // Se l'input è un intero, è una scelta operazione
                if (input instanceof Integer) {
                    int scelta = (Integer) input;
                    
                    switch (scelta) {
                        case 1: { // Carica da file
                            String nomeFile = (String) in.readObject();
                            String risposta = "OK";
                            String dendrogrammaFile = null;
                            
                            try {
                                if (!nomeFile.toLowerCase().endsWith(".dat")) {
                                    risposta = "Il file deve avere estensione .dat";
                                } else {
                                    File file = new File(nomeFile);
                                    if (!file.exists()) {
                                        risposta = "Il file " + nomeFile + " non esiste";
                                    } else {
                                        try (ObjectInputStream fileIn = new ObjectInputStream(new FileInputStream(file))) {
                                            Object obj = fileIn.readObject();
                                            if (obj instanceof HierachicalClusterMiner) {
                                                HierachicalClusterMiner minerFile = (HierachicalClusterMiner) obj;
                                                dendrogrammaFile = minerFile.toString();
                                            } else {
                                                risposta = "Il file non contiene un dendrogramma valido";
                                            }
                                        } catch (Exception e) {
                                            risposta = "Errore durante la lettura del file: " + e.getMessage();
                                        }
                                    }
                                }
                            } finally {
                                out.writeObject(risposta);
                                if (risposta.equals("OK") && dendrogrammaFile != null) {
                                    out.writeObject(dendrogrammaFile);
                                }
                            }
                            break;
                        }
                        
                        case 2: { // Apprendi da database
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
                            out.writeObject("Operazione non valida");
                            break;
                    }
                }
                // Se l'input è una stringa, è il nome della tabella
                else if (input instanceof String) {
                    tabella = (String) input;
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
                    out.writeObject(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                in.close();
                out.close();
                db.closeConnection();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
