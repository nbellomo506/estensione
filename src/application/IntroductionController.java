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
public class IntroductionController {

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
    Main.Gohome(event);
    }
}