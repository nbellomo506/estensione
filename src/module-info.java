module estensione {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires java.management;
	requires javafx.graphics;
	
	opens application to javafx.graphics, javafx.fxml;
}
