package application;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.Initializable;

public class OptionsController implements Initializable {

	@FXML
	private TextField tableNameInput; // For inputting the table name

    @FXML
    private RadioButton scelta1; // Option to load dendrogram from file

    @FXML
    private RadioButton scelta2; // Option to load dendrogram from database

    @FXML
    private ToggleGroup optionsGroup;
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    private RadioButton selectedRadioButton;  // Aggiungi questa variabile
    
    
    
    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        optionsGroup = new ToggleGroup();
        scelta1.setToggleGroup(optionsGroup);
        scelta2.setToggleGroup(optionsGroup);
        
        // Aggiungi listener per tracciare la selezione
        optionsGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedRadioButton = (RadioButton) newValue;
            }
        });
    }
    
    public void setStreams(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }
    
   
    
    @FXML
    public void handleAvantiButton(ActionEvent event) throws IOException {
        if (selectedRadioButton == null) {
            showAlert("Errore", "Seleziona un'opzione!");
            return;
        }

        String tableName = tableNameInput.getText();
        if (tableName.trim().isEmpty()) {
            showAlert("Errore", "Inserisci il nome della tabella!");
            return;
        }

        try {
            // Invia il nome della tabella al server
            out.writeObject(tableName);
            out.flush();

            // Attendi la risposta dal server
            String response = (String) in.readObject();
            
            // Se la risposta non è "OK", mostra l'errore e non procedere
            if (!response.equals("OK")) {
                showAlert("Errore", response);
                return;
            }

            // Se arriviamo qui, la tabella esiste e possiamo procedere
            FXMLLoader loader = new FXMLLoader(getClass().getResource("scelta.fxml"));
            Parent root = loader.load();
            
            SceltaController sceltaController = loader.getController();
            sceltaController.setStreams(out, in);
            sceltaController.setSelectedOption(selectedRadioButton.getText());
            sceltaController.setTableName(tableName);
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();

        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore di comunicazione con il server: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    
    

}