package application;

import java.io.IOException;
import java.util.Optional;

import classes.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class BudgetController {
	private User user;
	private String kitchenName;

	@FXML
	private Label titleLabel;

	@FXML
	private Button logOutButton;

	@FXML
	private Button backButton;
	
	@FXML
	private Button resetButton;

	@FXML
	private Label balanceLabel;

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/DayOverview.fxml");
	}
	
	@FXML
	void resetButtonClicked(ActionEvent event) throws InterruptedException{
		user.setFeedbackMsg(null);
		
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Pay Your Balance");
		alert.setHeaderText("You are about to reset your balance");
		alert.setContentText("Once this is done it cannot be undone! \n Are you sure you want to continue?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			user.command("resetUserBalance", kitchenName, 0, 0, 0, 0);

			while (user.getFeedbackMsg() == null) {
				System.out.println("budget: "+user.getFeedbackMsg());
				Thread.sleep(10);
			}

			feedbackMessage("Reset Balance", user.getFeedbackMsg());
			

		} else {
			feedbackMessage("Balance", "Balance was not reset.");
		}

	}

	@FXML
	void logOutButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
	}

	//////////////////////////////
	// controller methods

	public void feedbackMessage(String cmd, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(cmd);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}
	
	public void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;
	}

	public void setBalance() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getBalance", kitchenName, 0, 0, 0, 0);

		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}

		balanceLabel.setText(user.getFeedbackMsg());

	}

	public void setUser(User user) {
		this.user = user;
	}

	@SuppressWarnings("unused")
	private void newScene(ActionEvent event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		int x = 400;

		switch (path) {
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(new User());
			break;

		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
			try {
				dayController.updateTable(kitchenName, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			x = 600;
			break;

		}

		Scene scene = new Scene(root, x, 400);
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		Stage stage = new Stage();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
		stage.setScene(scene);
		stage.setTitle("Dinner Club");
		stage.show();
	}

}