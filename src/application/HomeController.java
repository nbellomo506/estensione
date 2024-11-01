package application;

import java.io.IOException;
import java.net.Socket;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;

public class HomeController {

    @FXML
    private Button connetti;
    
    @FXML 
    private TextField hostNameInput;
    
    @FXML 
    private TextField portInput;
    static Parent root;
    @FXML
    private void connetti(ActionEvent event) throws IOException {
        String ip = hostNameInput.getText();
        int port = Integer.parseInt(portInput.getText());
        
        try {
            Socket socket = new Socket(ip, port);
            AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("Options.fxml"));
            Scene scene = new Scene(root,600,400);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            Main.scene_scaling(scene, root);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di Connessione");
            alert.setContentText("Impossibile connettersi al server: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    
    
    

}
