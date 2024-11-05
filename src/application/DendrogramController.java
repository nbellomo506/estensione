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
import javafx.stage.FileChooser;
import java.io.File;

public class DendrogramController {
    @FXML
    private Button backhomeiButton;
    @FXML
    private TableView<String> dendrogramTable;
    @FXML
    private TableColumn<String, String> dendrogramColumn;
    
    // Componenti per scelta 1 (File)
    @FXML
    private VBox fileComponents;
    
    @FXML
    private Button loadFileButton;
    @FXML
    private Button createButton;
    
    // Componenti per scelta 2 (Database)
    @FXML
    private VBox dbComponents;
    @FXML
    private TextField depthInput;
    @FXML
    private ComboBox<String> distanceType;
    @FXML
    private Button executeButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button runButton;
    

    
    private String selectedOption;
    private String tableName;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private File lastUsedDirectory;
    
    @FXML
    public void initialize() {
        try {
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
            runButton.setDisable(false);
            
            // Inizializza la TableView con una larghezza appropriata
            dendrogramColumn.prefWidthProperty().bind(dendrogramTable.widthProperty().multiply(0.98));
            
            // Aggiungi listener per validazione input
            depthInput.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    depthInput.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
        } catch (Exception e) {
            showAlert("Errore di inizializzazione", e.getMessage());
        }
    }
    
    public void setSelectedOption(String option) {
        this.selectedOption = option;
        
        System.out.println("Opzione selezionata: " + option); // Debug
        
        // Mostra solo i componenti relativi alla scelta selezionata
        if (option.equals("File")) {
            fileComponents.setVisible(true);
            fileComponents.setManaged(true);
            dbComponents.setVisible(false);
            dbComponents.setManaged(false);
            
            System.out.println("Mostro componenti File"); // Debug
            
        } else if (option.equals("Database")) {
            fileComponents.setVisible(false);
            fileComponents.setManaged(false);
            dbComponents.setVisible(true);
            dbComponents.setManaged(true);
            
            System.out.println("Mostro componenti Database"); // Debug
        } else {
            System.out.println("Opzione non riconosciuta: " + option); // Debug
        }
        
        // Pulisci sempre la TableView
        dendrogramTable.getItems().clear();
        
        // Reset dei componenti non utilizzati
        if (option.equals("File")) {
            depthInput.clear();
            distanceType.getSelectionModel().clearSelection();
           
        }
    }
    @FXML
    void handleBackHome(ActionEvent event) throws IOException {
        Main.Gohome(event); // Torna alla scena Home
    }
    @FXML
    private void handleLoadFile() {
        try {
            // Crea un FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleziona file dendrogramma");
            
            // Imposta il filtro per i file .dat
            FileChooser.ExtensionFilter datFilter = 
                new FileChooser.ExtensionFilter("File DAT (*.dat)", "*.dat");
            fileChooser.getExtensionFilters().add(datFilter);
            
            // Imposta la directory iniziale
            if (lastUsedDirectory != null) {
                fileChooser.setInitialDirectory(lastUsedDirectory);
            }
            
            // Mostra il dialog per selezionare il file
            File selectedFile = fileChooser.showOpenDialog(loadFileButton.getScene().getWindow());
            
            if (selectedFile != null) {
                // Aggiorna l'ultima directory usata
                lastUsedDirectory = selectedFile.getParentFile();
                
                loadDendrogramFromFile(selectedFile);
            }
        } catch (Exception e) {
            showAlert("Errore", "Errore durante la selezione del file: " + e.getMessage());
        }
    }

    private void loadDendrogramFromFile(File file) {
        try {
            // Pulisci la tabella prima di caricare il nuovo file
            dendrogramTable.getItems().clear();
            
            // Invia la scelta e il nome del file al server
            out.writeObject(1);
            out.flush();
            out.writeObject(file.getAbsolutePath());
            out.flush();

            // Gestione della risposta
            String risposta = (String) in.readObject();
            if (risposta.equals("OK")) {
                String dendrogramma = (String) in.readObject();
                if (dendrogramma != null && !dendrogramma.trim().isEmpty()) {
                    String[] rows = dendrogramma.split("\n");
                    dendrogramTable.getItems().addAll(Arrays.asList(rows));
                    
                    // Opzionale: mostra un messaggio di successo
                    showSuccess("Caricamento completato", 
                              "Il dendrogramma è stato caricato correttamente");
                } else {
                    showAlert("Errore", "Il dendrogramma è vuoto");
                }
            } else {
                showAlert("Errore", risposta);
            }
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore di comunicazione con il server: " + e.getMessage());
        }
    }

    @FXML
    private void handleReload() {
        // Rimuovi il controllo su fileNameInput
        // if (fileNameInput.getText().isEmpty()) {
        //     showAlert("Errore", "Nessun file precedentemente caricato");
        //     return;
        // }

        // Non è più necessario gestire il fileNameInput
        // File currentFile = new File(fileNameInput.getText());
        // if (!currentFile.exists()) {
        //     showAlert("Errore", "Il file non esiste più");
        //     return;
        // }

        // Puoi chiamare direttamente il metodo per caricare il file
        // Se hai un modo alternativo per ottenere il file, implementalo qui
    }
    @FXML
    private void handleCreate() {
        setSelectedOption("Database");
    }
    @FXML
    private void handleRun() {
        setSelectedOption("File");
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
                runButton.setDisable(false);
            } else {
                showAlert("Errore", risposta);
                // Disabilita il salvataggio in caso di errore
                saveButton.setDisable(true);
                runButton.setDisable(true);
            }

        } catch (IOException | ClassNotFoundException e) {
            showAlert("Errore", "Errore di comunicazione con il server: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        try {
            // Crea un FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Dendrogramma");
            
            // Imposta il filtro per i file .dat
            FileChooser.ExtensionFilter datFilter = 
                new FileChooser.ExtensionFilter("File DAT (*.dat)", "*.dat");
            fileChooser.getExtensionFilters().add(datFilter);
            
            // Mostra il dialog per salvare il file
            File file = fileChooser.showSaveDialog(saveButton.getScene().getWindow());
            
            if (file != null) {
                // Assicurati che il file abbia l'estensione .dat
                if (!file.getName().toLowerCase().endsWith(".dat")) {
                    file = new File(file.getAbsolutePath() + ".dat");
                }

                // Invia nome file al server
                out.writeObject(file.getAbsolutePath());
                out.flush();

                // Ricevi risposta del salvataggio
                String rispostaSalvataggio = (String) in.readObject();
                if (rispostaSalvataggio.equals("OK")) {
                    showSuccess("Salvataggio completato", "Il file è stato salvato correttamente");
                } else {
                    showAlert("Errore", rispostaSalvataggio);
                }
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

    // Aggiungi questi metodi per gestire lo stato dei componenti
    public void setStreams(ObjectOutputStream out, ObjectInputStream in) {
        this.out = out;
        this.in = in;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
