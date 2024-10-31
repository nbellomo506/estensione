package application;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

public class OptionsController {

	@FXML
	private TextField tableNameInput; // For inputting the table name

    @FXML
    private RadioButton scelta1; // Option to load dendrogram from file

    @FXML
    private RadioButton scelta2; // Option to load dendrogram from database

    @FXML
    private ToggleGroup optionsGroup;
    
    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and set it to the radio buttons
        optionsGroup = new ToggleGroup();
        scelta1.setToggleGroup(optionsGroup);
        scelta2.setToggleGroup(optionsGroup);
    }
    
    @FXML
    public void avanti() {
        // Check if the optionsGroup is initialized (it should be)
        if (optionsGroup == null) {
            showAlert("Error", "Options group is not initialized!");
            return;
        }

        // Retrieve the selected radio button
        RadioButton selectedRadioButton = (RadioButton) optionsGroup.getSelectedToggle();
        String selectedOption = selectedRadioButton != null ? selectedRadioButton.getText() : "None";
        String tableName = tableNameInput.getText();
        
        // Display the selected option and table name
        showAlert("Selected Option", "Option: " + selectedOption + "\nTable Name: " + tableName);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    
    
    

}
