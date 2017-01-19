package application;

/* 02148 Introduction to Coordination in Distributed Applications
*  19. Januar 2017
*  Team 9 - Dinner Club
*	- Alexander Kristian Armstrong, s154302
*	- Michael Atchapero,  s143049
*	- Mathias Ennegaard Asmussen, s154219
*	- Emilie Isabella Dahl, s153762
*	- Jon Ravn Nielsen, s136448
*/

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
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class BudgetController {
	private User user;
	private String kitchenName;

	@FXML
	private Label titleLabel;
	
	@FXML
	private Label balanceLabel;

	@FXML
	private Button logOutButton;

	@FXML
	private Button backButton;

	@FXML
	private Button resetButton;
	
	@FXML
	private Button getPaymentButton;
	
	@FXML
	private Button payButton;

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		// Changing the scene to DayOverviw
		newScene(event, "/application/DayOverview.fxml");
	}

	@FXML
	void resetButtonClicked(ActionEvent event) throws InterruptedException {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setTitle("Pay Your Balance");
		alert.setHeaderText("You are about to reset your balance");
		alert.setContentText("Once this is done it cannot be undone! \n Are you sure you want to continue?");
		
		// Waiting for a response from the user
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == ButtonType.OK) {
			user.command("resetUserBalance", kitchenName, 0, 0, 0, 0);

			setBalance();
			feedbackMessage("Reset Balance", user.getFeedbackMsg());
		} else {
			feedbackMessage("Balance", "Balance was not reset.");
		}
	}

	@FXML
	void logOutButtonClicked(ActionEvent event) throws IOException {
		// Changing the scene to Login
		newScene(event, "/application/Login.fxml");
	}
	
	@FXML
	void payButtonClicked(ActionEvent event) throws InterruptedException{
		// Creating a text input dialog
		buttonClicked("amount $$", "How much are you paying?", "Total amount: ", true);
	}
	
	@FXML
	void getPaymentButtonClicked(ActionEvent event) throws InterruptedException{
		// Creating a text input dialog
		buttonClicked("amount $$", "How much are you receiving?", "Total amount: ", false);
	}

	
	
	private void buttonClicked(String textInput, String headText, String contentText, boolean pay) throws InterruptedException{

		TextInputDialog dialog = new TextInputDialog(textInput);
		dialog.setHeaderText(headText);
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setContentText(contentText);

		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			while (!isNumeric(result.get())) {
				dialog.setContentText(contentText + "\nPlease enter a number");
				result = dialog.showAndWait();
				if (!result.isPresent())
					break;
			}
			// Checking that the new result isn't cancel
			if (result.isPresent()) {
				int payment = Integer.parseInt(result.get());
				if(pay)
					payment = payment-(2*payment);
				user.command("addUserBalance", kitchenName, 0, 0, 0, payment);
				feedbackMessage("Update balance", user.getFeedbackMsg());
				setBalance();
			}
		}
	}
	
	//////////////////////////////
	// controller methods
	
	public static boolean isNumeric(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c))
				return false;
		}
		return true;
	}

	// Alert template to display feedback
	public void feedbackMessage(String cmd, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setTitle(cmd);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;
	}
	
	public void setUser(User user) {
		this.user = user;
	}

	// updating the balance label
	public void setBalance() throws InterruptedException {
		user.command("getBalance", kitchenName, 0, 0, 0, 0);
		balanceLabel.setText(user.getFeedbackMsg());
	}

	private void newScene(ActionEvent event, String path) throws IOException {
		// Hiding the displayed window
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		int x = 400;

		switch (path) {
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			// Setting the user to a new user, so all the information is reset.
			loginController.setUser(new User());
			break;

		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
			try {
				// The boolean is added so the previous dinner days aren't shown
				dayController.updateTable(kitchenName, false);
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