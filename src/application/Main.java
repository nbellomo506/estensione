package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale; 
import javafx.scene.image.Image;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.control.Labeled;
import java.util.HashMap;
import java.util.Map;

import server.MultiServer;

public class Main extends Application {
	private static MultiServer server;

	@Override
	public void start(Stage primaryStage) throws Exception {
		// Avvia il server in un thread separato
		Thread serverThread = new Thread(() -> {
			server = new MultiServer(8000);
		});
		serverThread.setDaemon(true);  // Il thread terminerà quando l'applicazione viene chiusa
		serverThread.start();

		try {
			primaryStage.setTitle("H-CLUS APPLICATION");
			Image icon = new Image("Logo.png");
			primaryStage.getIcons().add(icon);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Info.fxml"));

			VBox root = (VBox) loader.load();
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			
			primaryStage.setScene(scene);
			primaryStage.show();
			scene_scaling(scene, root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	//Scala la scena in base alle dimensioni della finestra
	public static void scene_scaling(Scene scene, final Pane root) {
		final double initialWidth = scene.getWidth();
		final double initialHeight = scene.getHeight();
		
		scene.widthProperty().addListener((obs, oldVal, newVal) -> {
			double scale = Math.min(newVal.doubleValue() / initialWidth, 2.0); // limita lo scale massimo a 2
			root.setScaleX(scale);
			root.setScaleY(scale);
		});
		
		scene.heightProperty().addListener((obs, oldVal, newVal) -> {
			double scale = Math.min(newVal.doubleValue() / initialHeight, 2.0); // limita lo scale massimo a 2
			root.setScaleX(scale);
			root.setScaleY(scale);
		});
		
		// Imposta il layout per il BorderPane
		if (root instanceof BorderPane) {
			BorderPane.setAlignment(((BorderPane) root).getTop(), Pos.CENTER);
			BorderPane.setAlignment(((BorderPane) root).getCenter(), Pos.CENTER);
			BorderPane.setAlignment(((BorderPane) root).getBottom(), Pos.CENTER);
		} else if (root instanceof VBox) {
			// Imposta l'allineamento per VBox se necessario
			VBox.setVgrow(root, Priority.ALWAYS);
		}
	}

	/**SceneSizeChangeListener
	 * 
	 * Classe che cambia le dimensione della scena.
	 *
	 */
	public static class SceneSizeChangeListener implements ChangeListener<Number> {
		private final Scene scene;
		private final VBox contentPane;

		public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth,
				VBox contentPane) {
			this.scene = scene;
			this.contentPane = contentPane;
			
			// Impostiamo il comportamento di base del contentPane
			contentPane.setFillWidth(true);
			contentPane.setAlignment(Pos.CENTER);
		}

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
			double newWidth = scene.getWidth();
			double newHeight = scene.getHeight();
			
			// Aggiorniamo direttamente le dimensioni del contentPane
			contentPane.setPrefWidth(newWidth);
			contentPane.setPrefHeight(newHeight);
			
			// Aggiorniamo il layout
			contentPane.requestLayout();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
