package application;

import javafx.fxml.FXML;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import javafx.beans.property.SimpleStringProperty;
import java.util.Arrays;

public class SceltaController {
    @FXML
    private TableView<String> dendrogramTable;
    @FXML
    private TableColumn<String, String> dendrogramColumn;
    
    // Componenti per scelta 1 (File)
    @FXML
    private VBox fileComponents;
    @FXML
    private TextField fileNameInput;
    @FXML
    private Button loadFileButton;
    
    // Componenti per scelta 2 (Database)
    @FXML
    private VBox dbComponents;
    @FXML
    private TextField depthInput;
    @FXML
    private ComboBox<String> distanceType;
    @FXML
    private Button executeButton;
    
    private String selectedOption;
    private String tableName;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    
    @FXML
    public void initialize() {
        // Inizializza la colonna della TableView
        dendrogramColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue()));
        
        // Nascondi inizialmente tutti i componenti specifici
        fileComponents.setVisible(false);
        fileComponents.setManaged(false);
        dbComponents.setVisible(false);
        dbComponents.setManaged(false);
        
        // Setup ComboBox per il tipo di distanza
        distanceType.getItems().addAll("Single Link", "Average Link");
        
        // Disabilita inizialmente i controlli di salvataggio
        saveButton.setDisable(true);
        saveFileInput.setDisable(true);
        
        // Inizializza la TableView con una larghezza appropriata
        dendrogramColumn.prefWidthProperty().bind(dendrogramTable.widthProperty().multiply(0.98));
        
        // Aggiungi listener per validazione input
        depthInput.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                depthInput.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    
    public void setSelectedOption(String option) {
        this.selectedOption = option;
        
        // Mostra solo i componenti relativi alla scelta selezionata
        if (option.equals("File")) {
            fileComponents.setVisible(true);
            fileComponents.setManaged(true);
            dbComponents.setVisible(false);
            dbComponents.setManaged(false);
            
            // Pulisci la TableView
            dendrogramTable.getItems().clear();
            
            // Reset componenti scelta 2
            depthInput.clear();
            distanceType.getSelectionModel().clearSelection();
            saveFileInput.clear();
            
        } else {
            fileComponents.setVisible(false);
            fileComponents.setManaged(false);
            dbComponents.setVisible(true);
            dbComponents.setManaged(true);
            
            // Pulisci la TableView
            dendrogramTable.getItems().clear();
            
            // Reset componenti scelta 1
            fileNameInput.clear();
        }
    }
    
    @FXML
    private void handleLoadFile() {
        try {
            // Invia esplicitamente la scelta 1
            out.writeObject(1);
            out.flush();
            
            String fileName = fileNameInput.getText().trim();
            if (fileName.isEmpty()) {
                showAlert("Errore", "Inserire il nome del file");
                return;
            }

            out.writeObject(fileName);
            out.flush();

            String risposta = (String) in.readObject();
            if (risposta.equals("OK")) {
                String dendrogramma = (String) in.readObject();
                String[] rows = dendrogramma.split("\n");
                dendrogramTable.getItems().clear();
                dendrogramTable.getItems().addAll(Arrays.asList(rows));
            } else {
                showAlert("Errore", risposta);
            }
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore di comunicazione con il server: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleExecute() {
        try {
            // Invia esplicitamente la scelta 2
            out.writeObject(2);
            out.flush();
            
            // Validazione input
            if (depthInput.getText().trim().isEmpty()) {
                showAlert("Errore", "Inserire la profondità");
                return;
            }
            if (distanceType.getValue() == null) {
                showAlert("Errore", "Selezionare il tipo di distanza");
                return;
            }

            // Invia profondità
            int depth;
            try {
                depth = Integer.parseInt(depthInput.getText().trim());
                out.writeObject(depth);
                out.flush();
            } catch (NumberFormatException e) {
                showAlert("Errore", "La profondità deve essere un numero intero");
                return;
            }

            // Invia tipo distanza (1 per Single Link, 2 per Average Link)
            int dType = distanceType.getValue().equals("Single Link") ? 1 : 2;
            out.writeObject(dType);
            out.flush();

            // Ricevi risposta
            String risposta = (String) in.readObject();
            if (risposta.equals("OK")) {
                String dendrogramma = (String) in.readObject();
                // Popola la TableView
                String[] rows = dendrogramma.split("\n");
                dendrogramTable.getItems().clear();
                dendrogramTable.getItems().addAll(Arrays.asList(rows));
                
                // Abilita il pulsante di salvataggio
                saveButton.setDisable(false);
                saveFileInput.setDisable(false);
            } else {
                showAlert("Errore", risposta);
                // Disabilita il salvataggio in caso di errore
                saveButton.setDisable(true);
                saveFileInput.setDisable(true);
            }

        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore di comunicazione con il server: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            String fileName = saveFileInput.getText().trim();
            if (fileName.isEmpty()) {
                showAlert("Errore", "Inserire il nome del file per il salvataggio");
                return;
            }

            // Invia nome file al server
            out.writeObject(fileName);
            out.flush();

            // Ricevi risposta del salvataggio
            String rispostaSalvataggio = (String) in.readObject();
            if (rispostaSalvataggio.equals("OK")) {
                showSuccess("Salvataggio completato", "Il file è stato salvato correttamente");
            } else {
                showAlert("Errore", rispostaSalvataggio);
            }

        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore durante il salvataggio: " + e.getMessage());
        }
    }

    // Metodo di utilità per mostrare messaggi di successo
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Aggiungi questi campi alla classe
    @FXML
    private Button saveButton;
    @FXML
    private TextField saveFileInput;

    // Aggiungi questi metodi per gestire lo stato dei componenti
    public void setStreams(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
