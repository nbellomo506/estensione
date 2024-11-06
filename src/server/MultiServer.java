package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/** MultiServer
 * 
 * classe che gestisce la connessione al server.
 *
 */
public class MultiServer {

	/**
	 * attributo che indica il numero di porta associata al server.
	 */
	private int PORT = 8000;

	/**
	 * lista dei socket attivi.
	 */
	private List<Socket> activeSockets = new ArrayList<>();

	private ScheduledExecutorService inactivityTimer;
	private static final int INACTIVITY_TIMEOUT = 60; // Timeout di inattività in secondi

	/**
	 * costruttore della classe MultiServer.
	 * @param port
	 */
	public MultiServer(int port) {
		this.PORT = port;
		startInactivityTimer();
		run();
	}
	
	/**
	 * metodo che inizializza la connessione.
	 */
	private void run() {
		
		try {
			System.out.println("new Server:");

			ServerSocket serv = new ServerSocket(PORT);
			System.out.println("Server avviato sulla porta: " + PORT);
			try{
				
                while (true) {
                    Socket socket = serv.accept();
                    System.out.println("Connessione in corso... " + socket);
                    try{
                    	
                        new ServerOneClient(socket);
                 
                    } catch (IOException e){
                        socket.close();
                    }
                }
            } finally {
                serv.close();
                System.err.println("Disconnettendo il server... ");
            }
		} catch (IOException exc) {
			System.out.println(exc.toString());
		}
		
	}
	
	 /**
     * metodo che avvia il server.
     * @param args
     */
    public static void main(String args[]){
        new MultiServer(8000);
    }

	/**
	 * metodo che chiude tutte le connessioni attive.
	 */
	public void closeConnections() {
		for (Socket socket : activeSockets) {
			try {
				if (!socket.isClosed()) {
					socket.setSoTimeout(1000); // Imposta un timeout di 1 secondo
					socket.close();
					System.out.println("Connessione chiusa: " + socket);
				}
			} catch (SocketTimeoutException e) {
				System.err.println("Timeout durante la chiusura del socket " + socket + ": " + e.getMessage());
			} catch (IOException e) {
				System.err.println("Errore durante la chiusura del socket " + socket + ": " + e.getMessage());
			}
		}
		activeSockets.clear();
		System.out.println("Tutte le connessioni sono state chiuse.");
		inactivityTimer.shutdown(); // Ferma il timer di inattività
	}

	private void startInactivityTimer() {
		inactivityTimer = Executors.newSingleThreadScheduledExecutor();
		inactivityTimer.schedule(() -> {
			System.out.println("Timeout di inattività raggiunto. Chiudendo il server...");
			closeConnections();
			System.exit(0); // Chiudi l'applicazione
		}, INACTIVITY_TIMEOUT, TimeUnit.SECONDS);
	}

	public void resetInactivityTimer() {
		inactivityTimer.shutdownNow(); // Ferma il timer attuale
		startInactivityTimer(); // Riavvia il timer
	}

    public void stop() {
        closeConnections(); // Chiudi tutte le connessioni attive
        System.out.println("Server fermato."); // Messaggio di conferma
        System.exit(0); // Termina l'applicazione
    }
}
