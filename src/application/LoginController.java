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

import classes.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class LoginController {
	private User user;
	private String tempName;

	///////////////////////////////////
	// Login Scene

	@FXML
	private Button newUserButton;

	@FXML
	private Button loginButton;

	@FXML
	private TextField usernameTextField;

	@FXML
	private Label wrongUsernameLabel;

	@FXML
	void newUserButtonClicked(ActionEvent event) throws IOException {
		this.tempName = usernameTextField.getText();
		newScene(event, "/application/NewUser.fxml");
	}

	@FXML
	void loginButtonClicked(ActionEvent event) throws Exception {
		String username = usernameTextField.getText();
		if (user.getUser(username)) {
			wrongUsernameLabel.setText("");
			newScene(event, "/application/SelectKitchen.fxml");
		} else {
			wrongUsernameLabel.setText("  Unknown username");
		}

	}

	@FXML // Type enter to activate this code
	void loginEnterTyped(ActionEvent event) {
		usernameTextField.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent ke) {

				if (ke.getCode().equals(KeyCode.ENTER)) {
					try {
						loginButtonClicked(event);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	///////////////////////////////////
	// new user window

	@FXML
	private TextField newUserNameTextField;

	@FXML
	private Label newUserLabel;

	@FXML
	private Button createNewUserButton;

	@FXML
	private Button backButton;

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
	}

	@FXML
	void createNewUserButtonClicked(ActionEvent event) throws Exception {
		String username = newUserNameTextField.getText();

		if (username.equals("")) {
			newUserLabel.setText("Please enter a username");
		} else if (user.addUser(username)) {
			// Saving the name written in the textField
			this.tempName = username;
			newScene(event, "/application/Login.fxml");
		} else {
			newUserLabel.setText("Please enter another username, " + username + " already exist");
		}
	}

	//////////////////////////////
	// controller methods

	public void setUser(User user) {
		this.user = user;
	}

	private void newScene(ActionEvent event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		LoginController loginontroller;

		switch (path) {
		case "/application/Login.fxml":
			loginontroller = loader.getController();
			loginontroller.setUser(user);
			// Sending the tempName so it can be displayed as input
			loginontroller.usernameTextField.setText(tempName);
			break;

		case "/application/NewUser.fxml":
			loginontroller = loader.getController();
			loginontroller.setUser(user);
			loginontroller.newUserNameTextField.setText(tempName);
			break;

		case "/application/SelectKitchen.fxml":
			KitchenController kitchenController = loader.getController();
			kitchenController.findUsersKitchens(user);
			kitchenController.titleLabel.setText("Hi " + usernameTextField.getText() + "!");
			break;
		}

		Scene scene = new Scene(root, 400, 400);
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
