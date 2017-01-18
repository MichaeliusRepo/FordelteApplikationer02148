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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class KitchenController {
	private String username, kitchenName;
	private User user;

	@FXML
	public Label titleLabel;

	@FXML
	private Label noKitchenLabel;

	@FXML
	private ToggleGroup toggleGroup;

	@FXML
	private RadioButton kitchen1;

	@FXML
	private RadioButton kitchen2;

	@FXML
	private RadioButton kitchen3;

	@FXML
	private RadioButton kitchen4;

	@FXML
	private Button selectButton;

	@FXML
	private Button newKitchenButton;

	@FXML
	private Button joinKitchenButton;

	@FXML
	private Button logOutButton;

	@FXML
	void newKitchenButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/CreateDinnerClub.fxml");
	}

	@FXML
	void joinKitchenButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/JoinDinnerClub.fxml");
	}

	@FXML
	void selectDinnerClubButtonClicked(ActionEvent event) throws IOException {

		if (((RadioButton) toggleGroup.getSelectedToggle()).getText().length() > 0) {
			kitchenName = ((RadioButton) toggleGroup.getSelectedToggle()).getText();
			newScene(event, "/application/DayOverview.fxml");
		} else {
			noKitchenLabel.setText("Please select a dinner club");
		}

	}

	@FXML
	void logOutButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
	}

	////////////////////////
	// Create new kitchen

	@FXML
	private Label emptyLabel;

	@FXML
	private Button createKitchenButton;

	@FXML
	private TextField newKitchenTextField;

	@FXML
	private Button backButton;

	@FXML
	void createKitchenButtonClicked(ActionEvent event) throws Exception {

		if (user.createKitchen(newKitchenTextField.getText())) {
			newScene(event, "/application/SelectKitchen.fxml");
		} else {
			emptyLabel.setText(user.getFeedbackMsg());
		}
		
		if (!newKitchenTextField.getText().equals("")) {
		} else {
			emptyLabel.setText("not valid: Remember to write a name");
		}
	}

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/SelectKitchen.fxml");

	}

	////////////////////////
	// Join kitchen

	@FXML
	private TextField joinKitchenTextField;

	@FXML
	private Button joinExcistingKitchenButton;

	@FXML
	void joinExcistingKitchenButtonClicked(ActionEvent event) throws Exception {
		if (!joinKitchenTextField.getText().equals("")) {

			if (user.joinKitchen(joinKitchenTextField.getText())) {
				newScene(event, "/application/SelectKitchen.fxml");
			} else {
				emptyLabel.setText(user.getFeedbackMsg());
			}
		} else {
			emptyLabel.setText("not valid: Remember to write a name");
		}
	}

	//////////////////////////////
	// controller methods

	public void setUser(User user) {
		this.user = user;
	}

	public void findUsersKitchens(User user) {
		setUser(user);
		setRadioButtons(kitchen1, 0);
		setRadioButtons(kitchen2, 1);
		setRadioButtons(kitchen3, 2);
		setRadioButtons(kitchen4, 3);
	}

	private void setRadioButtons(RadioButton kitchen, int i) {
		if (user.getKitchenName(i) != "") {
			kitchen.setText(user.getKitchenName(i));
			kitchen.setVisible(true);
		} else {
			kitchen.setVisible(false);
		}
	}

	private void newScene(ActionEvent event, String path) throws IOException {
		int x = 400;
		int y = 400;
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();

		KitchenController kitchenController;

		switch (path) {
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(new User());
			break;

		case ("/application/SelectKitchen.fxml"):
			kitchenController = loader.getController();
			kitchenController.findUsersKitchens(user);
			break;

		case "/application/JoinDinnerClub.fxml":
		case "/application/CreateDinnerClub.fxml":
			kitchenController = loader.getController();
			kitchenController.setUser(user);
			break;

		case "/application/DayOverview.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
			try {
				dayController.updateTable(((RadioButton) toggleGroup.getSelectedToggle()).getText(), false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			x = 590;
			y = 490;
			break;

		}

		Scene scene = new Scene(root, x, y);
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
