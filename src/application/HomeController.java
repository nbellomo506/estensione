package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class HomeController {

    @FXML
    private Button connetti;
    
    @FXML 
    private TextField hostNameInput;
    
    @FXML 
    private TextField portInput;
    
    @FXML
    private void connetti() {
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("Options.fxml"));
        String ip = hostNameInput.getText();
        int port = Integer.parseInt(portInput.getText());
        
	}
    
    
    
    

}
