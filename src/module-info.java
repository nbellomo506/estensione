module estensione {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires java.management;
	requires javafx.graphics;
	requires javafx.swing;
	opens application to javafx.graphics, javafx.fxml;
}
