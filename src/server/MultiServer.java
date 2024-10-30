package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

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
	 * costruttore della classe MultiServer.
	 * @param port
	 */
	public MultiServer(int port) {
		this.PORT = port;
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
}
