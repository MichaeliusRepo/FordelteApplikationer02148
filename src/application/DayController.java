package application;

import java.io.IOException;

import classes.Server;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DayController {
	private User user;
	private String kitchenName;

	@FXML
	private Label titleLabel;

	@FXML
	private TableView<DayTable> dayTableView;

	@FXML
	private TableColumn<DayTable, String> dateTableColumn;

	@FXML
	private TableColumn<DayTable, String> chefTableColumn;

	@FXML
	private TableColumn<DayTable, String> totalTableColumn;

	@FXML
	private TableColumn<DayTable, String> attendTableColumn;

	@FXML
	private CheckBox viewPreviousCheckBox;

	@FXML
	private Button logOutButton;

	@FXML
	private Button backButton;

	@FXML
	private Button addDayButton;

	@FXML
	private Button budgetButton;

	@FXML
	private Button updateButton;

	@FXML
	void addDayButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/AddDay.fxml");
	}

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/SelectKitchen.fxml");
	}

	@FXML
	void bugdetButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Budget.fxml");
	}

	@FXML
	void logOutButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/Login.fxml");
	}

	@FXML
	void viewPreviousCheckBoxClicked(ActionEvent event) {

	}

	@FXML
	void updateButtonClicked(ActionEvent event) {

		updateTabel(titleLabel.getText());

	}

	public void updateTabel(String kitchenName) {
		titleLabel.setText(kitchenName);
		int i = 0;

		DayTable dayTable = new DayTable(user, i);

		while (dayTable.haveNextDay()) {
			dayTableView.getItems().add(new DayTable(user, i));
			i++;
		}

		dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		chefTableColumn.setCellValueFactory(new PropertyValueFactory<>("chef"));
		totalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		attendTableColumn.setCellValueFactory(new PropertyValueFactory<>("attend"));

		dayTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				System.out.println("selected day: " + newSelection.getDate());
			}
		});

	}

	//////////////////////////////
	// AddDay

	@FXML
	private Button createDayButton;

	@FXML
	private DatePicker newDateDatePicker;

	@FXML
	private Button backToOverviewButton;
	
	@FXML
	private Label noDayLabel;

	@FXML
	void backToOverviewButtonClicked(ActionEvent event) throws IOException {
		newScene(event, "/application/DayOverview.fxml");
	}

	@FXML
	void createDayButtonClicked(ActionEvent event) throws IOException {
		if (newDateDatePicker.getValue() != null) {
			int day = newDateDatePicker.getValue().getDayOfMonth();
			int month = newDateDatePicker.getValue().getMonthValue();
			int year = newDateDatePicker.getValue().getYear();
			System.out.println("Creating the day: " + newDateDatePicker.getValue().toString());
			user.command("addDay", day, month, year, 0);
			newScene(event, "/application/DayOverview.fxml");
		} else {
			noDayLabel.setText("  Insert a date");
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
		KitchenController kitchenController;
		int x = 400;

		switch (path) {
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(new User());
			break;

		case ("/application/SelectKitchen.fxml"):
			kitchenController = loader.getController();
			kitchenController.findUsersKitchens(user);
			break;

		case "/application/DayOverview.fxml":
		case "/application/AddDay.fxml":
			DayController dayController = loader.getController();
			dayController.setUser(user);
			x = 500;
			break;

		case "/application/Budget.fxml":
			BudgetController budgetController = loader.getController();
			budgetController.setUser(user);
			budgetController.setBalance();
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