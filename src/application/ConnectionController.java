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
import javafx.scene.layout.BorderPane;
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
    private ObjectOutputStream out;
    private ObjectInputStream in;
    public void setStreams(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }
    @FXML
    private void connetti(ActionEvent event) throws IOException {
        String ip = hostNameInput.getText();
        int port = Integer.parseInt(portInput.getText());
        
        try {
            Socket socket = new Socket(ip, port);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MiningOptions.fxml"));
            VBox root = loader.load();

            MiningOptionsController optionsController = loader.getController();
            optionsController.setStreams(out, in);

            Scene scene = new Scene(root, 600, 400);
            
            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
            //Main.scene_scaling(scene, root);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errore di Connessione");
            alert.setContentText("Impossibile connettersi al server: " + e.getMessage());
            alert.showAndWait();
        }
    }
    
    
    
    

}
