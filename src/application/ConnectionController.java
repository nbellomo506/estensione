package application;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;

public class ConnectionController {

    @FXML
    private Button connetti;
    
    @FXML 
    private TextField hostNameInput;
    
    @FXML 
    private TextField portInput;
    static Parent root;

    Socket socket;
    
    @FXML
    private void connetti(ActionEvent event) throws IOException {
        String ip = hostNameInput.getText();
        int port = 0;
        try {
        	port = Integer.parseInt(portInput.getText());
        }catch (NumberFormatException ne){
        	port = 0;
        }
       
        
        try {
        	if (ip.isBlank() || ip.isEmpty())
            	throw new Exception("Nome dell'host non valido\n");
        	
        	if(port == 0)
        		throw new Exception("Numero della porta non valido\n");
        	
            socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MiningOptions.fxml"));
            VBox root = loader.load();

            MiningOptionsController optionsController = loader.getController();
            optionsController.setStreams(out, in);

            Scene scene = new Scene(root);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(true);
            
            stage.show();
        
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di Connessione");
            alert.setContentText("Impossibile connettersi al server: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    
    
    

}
