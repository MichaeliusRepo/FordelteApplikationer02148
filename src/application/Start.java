package application;

import java.util.Optional;

import classes.Server;
import classes.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Start extends Application {
	@Override
	public void start(Stage primaryStage) {

		try {
			User user = new User();
			TextInputDialog dialog = new TextInputDialog("Server IP");
			dialog.setTitle("Find Server");
			dialog.setHeaderText("Where is the server?");
			String contentText = "Write the server IP";
			dialog.setContentText(contentText);
			Optional<String> result = dialog.showAndWait();
			if (result.isPresent()) {
				user.setServerIP(result.get());

				FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/Login.fxml"));
				Parent root = loader.load();

				LoginController controller = loader.getController();
				controller.setUser(user);
				Scene scene = new Scene(root, 400, 400);
				scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
				primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent event) {
						Platform.exit();
						System.exit(0);
					}
				});
				primaryStage.setScene(scene);
				primaryStage.setTitle("Dinner Club");
				primaryStage.show();
			} else {
				System.exit(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// new Server();
		launch(args);
	}
}
