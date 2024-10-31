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


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			primaryStage.setTitle("DENDROGRAMMA APPLICATION");
			Image icon = new Image("Logo.png");
			primaryStage.getIcons().add(icon);
			FXMLLoader loader = new FXMLLoader(getClass().getResource("Info.fxml"));

			VBox root = (VBox) loader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.show();
			scene_scaling(scene, root);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void scene_scaling(Scene scene, final VBox root) {
		// TODO Auto-generated method stub
		final double i_height= scene.getHeight();
		final double i_width = scene.getWidth();
		final double rto = i_width / i_height;
		SceneSizeChangeListener change_listener = new SceneSizeChangeListener(scene, rto, i_height, i_width, root);
		scene.heightProperty().addListener(change_listener);
		scene.widthProperty().addListener(change_listener);
	}

	/**SceneSizeChangeListener
	 * 
	 * Classe che cambia le dimensione della scena.
	 *
	 */
	public static class SceneSizeChangeListener implements ChangeListener<Number> {
		private final Scene scene;
		private final double ratio;
		private final double initHeight;
		private final double initWidth;
		private final VBox contentPane;

		/**
		 * costruttore della classe SceneSizeChangeListener.
		 * @param scene
		 * @param ratio
		 * @param initHeight
		 * @param initWidth
		 * @param contentPane
		 */
		public SceneSizeChangeListener(Scene scene, double ratio, double initHeight, double initWidth,
				VBox contentPane) {
			this.scene = scene;
			this.ratio = ratio;
			this.initHeight = initHeight;
			this.initWidth = initWidth;
			this.contentPane = contentPane;
		}

		@Override
		public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
			final double newWidth = scene.getWidth();
			final double newHeight = scene.getHeight();

			double scaleFactor = newWidth / newHeight > ratio ? newHeight / initHeight : newWidth / initWidth;

			if (scaleFactor >= 1) {
				Scale scale = new Scale(scaleFactor, scaleFactor);
				scale.setPivotX(0);
				scale.setPivotY(0);
				scene.getRoot().getTransforms().setAll(scale);

				contentPane.setPrefWidth(newWidth / scaleFactor);
				contentPane.setPrefHeight(newHeight / scaleFactor);
			} else {
				contentPane.setPrefWidth(Math.max(initWidth, newWidth));
				contentPane.setPrefHeight(Math.max(initHeight, newHeight));
			}

		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
