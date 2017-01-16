package application;

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
		newScene(event, "/application/NewUser.fxml");
	}

	@FXML
	void loginButtonClicked(ActionEvent event) throws IOException {
		String username = usernameTextField.getText();
		
		if (user.getUser(username)) {
			wrongUsernameLabel.setText("");
			newScene(event, "/application/SelectKitchen.fxml");
		} else {
			wrongUsernameLabel.setText("  Unknown username");
			System.out.println("wrong");
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
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

	///////////////////////////////////
	// new user

	@FXML
	private TextField newUserNameTextField;

	@FXML
	private TextField kitchenNameTextField;

	@FXML
	private Button createNewUserButton;

	@FXML
	private Button backButton;

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
	}

	@FXML
	void createNewUserButtonClicked(ActionEvent event) throws IOException {
		String username = newUserNameTextField.getText();
		String kitchenName = newUserNameTextField.getText();
		
		if (!username.equals("") && !kitchenName.equals("") && user.addUser(username)) {
			System.out.println("new user has been created: "+ username + " and " + kitchenName);
		} else {
			System.out.println("Remember to insert kitchenName and Username, or a user has not been made.");
		}
		 
		newScene(event, "/application/Login.fxml");
	}

	//////////////////////////////
	// controller methods

	private void newScene(ActionEvent event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();

		if (path.equals("/application/Login.fxml") || path.equals("/application/NewUser.fxml")) {
			LoginController controller = loader.getController();
			controller.setUser(user);
		} else if (path.equals("/application/SelectKitchen.fxml")) {
			KitchenController controller = loader.getController();
			controller.findUsersKitchens(user);
			controller.titleLabel.setText("Hi " + usernameTextField.getText() + "!");
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

	public void setUser(User user) {
		System.out.println("1: " + user);
		System.out.println("2: " + this.user);
		this.user = user;
		System.out.println("3: " + this.user);

	}
}
