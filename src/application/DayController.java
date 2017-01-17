package application;

import java.io.IOException;

import classes.User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
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
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
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
	void updateButtonClicked(ActionEvent event) throws Exception {

		updateTabel(titleLabel.getText());

	}

	public void updateTabel(String kitchenName) throws Exception {
		this.kitchenName = kitchenName;
		titleLabel.setText(kitchenName);
		int i = 0;

		DayTable dayTable = new DayTable(user, kitchenName, i);

		while (dayTable.haveNextDay()) {
			dayTableView.getItems().add(new DayTable(user, kitchenName, i));
			i++;
		}

		dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		chefTableColumn.setCellValueFactory(new PropertyValueFactory<>("chef"));
		totalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		attendTableColumn.setCellValueFactory(new PropertyValueFactory<>("attend"));

		dayTableView.setOnMousePressed(new EventHandler<MouseEvent>() {
		    private String daySelected;

			@Override 
		    public void handle(MouseEvent event) {
		        if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
		        	try {
		        		daySelected = dayTableView.getSelectionModel().getSelectedItem().getDate();
		        		System.out.println(daySelected);
						newScene(event, "/application/DayWindow.fxml");
					} catch (IOException e) {
						e.printStackTrace();
					}                   
		        }
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
			user.command("addDay", kitchenName, day, month, year, 0);
			newScene(event, "/application/DayOverview.fxml");
		} else {
			noDayLabel.setText("  Insert a date");
		}
	}

	//////////////////////////////
	// day window

	@FXML
	private Button dayUpdateButton;

	@FXML
	private TableColumn<?, ?> dayNameColumn;

	@FXML
	private TableColumn<?, ?> dayAttendingColumn;

	@FXML
	private TextArea dayNote;

	@FXML
	void dayUpdateButtonClicked(ActionEvent event) {

	}

	//////////////////////////////
	// controller methods

	public void setUser(User user) {
		this.user = user;
	}

	private void newScene(Event event, String path) throws IOException {
		if (event != null)
			((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		KitchenController kitchenController;
		int x = 400, y = 400;

		switch (path) {
		case "/application/Login.fxml":
			LoginController loginController = loader.getController();
			loginController.setUser(new User());
			break;

		case ("/application/SelectKitchen.fxml"):
			kitchenController = loader.getController();
			kitchenController.findUsersKitchens(user);
			break;

		case "/application/DayWindow.fxml":
			y = 500;
		case "/application/DayOverview.fxml":
			x = 600;
		case "/application/AddDay.fxml":
			DayController dayController = loader.getController();
			dayController.setKitchenName(kitchenName);
			dayController.setUser(user);
			break;

		case "/application/Budget.fxml":
			BudgetController budgetController = loader.getController();
			budgetController.setUser(user);
			budgetController.setBalance();
			budgetController.setKitchenName(kitchenName);
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

	private void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;

	}

}