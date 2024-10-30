module estensione {
	requires javafx.controls;
	requires javafx.fxmll;
	
	opens application to javafx.graphics, javafx.fxml;
}
