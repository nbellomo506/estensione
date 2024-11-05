/**
 * Sample Skeleton for 'Scena1.fxml' Controller Class
 */

package application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.Priority;

/** Scena1Controller
 * 
 *  classe che gestisce la scena 1.
 *
 */
public class InfoController {

	/**
	 * Attributo utilizzato per l'interfaccia grafica di FXML.
	 *
	 */
	static Stage stage;
	/**
	 * Attributo utilizzato per l'interfaccia grafica di FXML.
	 *
	 */
	static Scene scene;
	/**
	 * Attributo utilizzato per l'interfaccia grafica di FXML.
	 *
	 */
	static Parent root;
    
    /**
     * metodo che porta alla scena 2.
     * @param event
     * @throws IOException
     */
    @FXML
    void GoScena2(ActionEvent event) throws IOException {
    	VBox root = (VBox)FXMLLoader.load(getClass().getResource("Home.fxml"));
    	stage = (Stage)((Node)event.getSource()).getScene().getWindow();
    	scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
    	stage.setScene(scene);
    	stage.setResizable(true);
    	stage.show();
        Main.scene_scaling(scene, root);
    	VBox.setVgrow(root, Priority.ALWAYS);
    }
}