package application;

// 02148 Introduction to Coordination in Distributed Applications
// 20. Januar 2017
// Team 9 - Dinner Club
//	- Alexander Kristian Armstrong, s154302
//	- Michael Atchapero,  s143049
//	- Mathias Ennegaard Asmussen, s154219
//	- Emilie Isabella Dahl, s153762
//	- Jon Ravn Nielsen, s136448

import classes.Server;
import classes.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class Start extends Application {
	@Override
	public void start(Stage primaryStage) {
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Login.fxml"));
			Parent root = loader.load();
			
			LoginController controller = loader.getController();
			controller.setUser(new User());
			Scene scene = new Scene(root,400,400);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setOnCloseRequest(new EventHandler <WindowEvent>() {
				public void handle(WindowEvent event) {
					Platform.exit();
					System.exit(0);
				}
			});
			primaryStage.setScene(scene);
			primaryStage.setTitle("Dinner Club");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// Creating the server, so the user class can call it when sending tuples
		new Server();
		launch(args);
	}
}
