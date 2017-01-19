package application;

//02148 Introduction to Coordination in Distributed Applications
//20. Januar 2017
//Team 9 - Dinner Club
//	- Alexander Kristian Armstrong, s154302
//	- Michael Atchapero,  s143049
//	- Mathias Ennegaard Asmussen, s154219
//	- Emilie Isabella Dahl, s153762
//	- Jon Ravn Nielsen, s136448

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

public class DayController {
	private User user;
	private String kitchenName, daySelected;
	private int day, month, year;

	//////////////////////////////
	// Day Overview window controls
	
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
		// Changing the scene to AddDay
		newScene(event, "/application/AddDay.fxml");
	}

	@FXML
	void backButtonClicked(ActionEvent event) throws IOException {
		// Changing the scene to SelectKitchen
		newScene(event, "/application/SelectKitchen.fxml");
	}

	@FXML
	void bugdetButtonClicked(ActionEvent event) throws IOException {
		// Changing the scene to Budget
		newScene(event, "/application/Budget.fxml");
	}

	@FXML
	void logOutButtonClicked(ActionEvent event) throws IOException {
		// Changing the scene to Login
		newScene(event, "/application/Login.fxml");
	}

	@FXML
	void viewPreviousCheckBoxClicked(ActionEvent event) throws Exception {
		// Updating the day overview table
		updateTable(kitchenName, viewPreviousCheckBox.isSelected());
	}

	@FXML
	void updateButtonClicked(ActionEvent event) throws Exception {
		// Updating the day overview table
		updateTable(kitchenName, viewPreviousCheckBox.isSelected());
	}

	public void updateTable(String kitchenName, boolean previous) throws Exception {
		this.kitchenName = kitchenName;
		titleLabel.setText(kitchenName);

		dayTableView.getItems().clear();
		totalTableColumn.setStyle("-fx-alignment: CENTER;");
		attendTableColumn.setStyle("-fx-alignment: CENTER;");

		DayTable dayTable = new DayTable(user, kitchenName, 0);

		// Finding the current day and the day the dinner club is held
		Calendar current = Calendar.getInstance();
		Calendar dinnerDay = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		for (int i = 0; i < dayTable.daysSize(); i++) {
			dayTable = new DayTable(user, kitchenName, i);
			dinnerDay.setTime(sdf.parse(dayTable.getDate() + " 23:59:59"));
			// To show all including the previous or only the significant ones
			if (previous) {
				dayTableView.getItems().add(dayTable);
			} else if (!current.after(dinnerDay)) {
				dayTableView.getItems().add(dayTable);
			}
		}
		
		// Setting the columns up
		dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		chefTableColumn.setCellValueFactory(new PropertyValueFactory<>("chef"));
		totalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		attendTableColumn.setCellValueFactory(new PropertyValueFactory<>("attend"));

		// enable the double tap
		dayTableView.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					if (dayTableView.getSelectionModel().getSelectedItem() != null) {
						try {
							// Changing the window to a specific day
							daySelected = dayTableView.getSelectionModel().getSelectedItem().getDate();
							newScene(event, "/application/DayWindow.fxml");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	//////////////////////////////
	// AddDay Window controls

	@FXML
	private DatePicker newDateDatePicker;
	
	@FXML
	private Button createDayButton;

	@FXML
	private Button backToOverviewButton;

	@FXML
	private Label noDayLabel;

	@FXML
	void backToOverviewButtonClicked(ActionEvent event) throws IOException {
		// Changing the window to DayOverviw
		newScene(event, "/application/DayOverview.fxml");
	}

	@FXML
	void createDayButtonClicked(ActionEvent event) throws IOException, InterruptedException {

		if (newDateDatePicker.getValue() != null) {
			int day = newDateDatePicker.getValue().getDayOfMonth();
			int month = newDateDatePicker.getValue().getMonthValue();
			int year = newDateDatePicker.getValue().getYear();
			user.command("addDay", kitchenName, day, month, year, 0);
			newScene(event, "/application/DayOverview.fxml");
		} else {
			noDayLabel.setText("  Insert a date");
		}
	}

	//////////////////////////////
	// day window controls
	
	@FXML
	private Label dateLabel;

	@FXML
	private Label chefsLabel;

	@FXML
	private Label shopperLabel;

	@FXML
	private Label priceLabel;

	@FXML
	private Label perPriceLabel;
	
	@FXML
	private Button dayUpdateButton;

	@FXML
	private TableView<DayNamesTable> dayNamesTable;

	@FXML
	private TableColumn<DayNamesTable, String> dayNameColumn;

	@FXML
	private TextArea dayNote;

	@FXML
	void dayUpdateButtonClicked(ActionEvent event) throws Exception {
		updateDay();
		updateDayTable();
	}

	@FXML
	void updateDayTable() throws InterruptedException {
		dayNamesTable.getItems().clear();
		DayNamesTable nameTable = new DayNamesTable(user, kitchenName, day, month, year, 0);

		for (int i = 0; i < nameTable.listSize(); i++) {
			dayNamesTable.getItems().add(new DayNamesTable(user, kitchenName, day, month, year, i));
		}

		dayNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
	}

	@FXML
	void setChefsButtonClicked(ActionEvent event) throws Exception {
		user.command("addChef", kitchenName, day, month, year, 0);
		feedbackMessage("Add Chef", user.getFeedbackMsg());
		updateDay();
	}

	@FXML
	void attendDayButtonClicked(ActionEvent event) throws InterruptedException {

		// Setting up a dialog display
		TextInputDialog dialog = new TextInputDialog("No. of attendees");
		dialog.initStyle(StageStyle.UNDECORATED);
		dialog.setTitle("Attend Day");
		dialog.setHeaderText("How many people should be expected?");
		String contentText = "Type 1 if you're coming alone, \n 2 if you're bringing a guest etc. \n If you no longer wish to attend type 0.";
		dialog.setContentText(contentText);
		
		// Waiting for a respond from the user
		Optional<String> result = dialog.showAndWait();
		
		if (result.isPresent()) {
			// Checking input
			while (!isNumeric(result.get())) {
				dialog.setContentText(contentText + "\nplease enter a no.");
				result = dialog.showAndWait();
				if (!result.isPresent())
					break;
			}
			// User respond 
			if (result.isPresent()) {
				user.command("attendDay", kitchenName, day, month, year, Integer.parseInt(result.get()));
				feedbackMessage("Attend Day", user.getFeedbackMsg());
			}
			updateDay();
		}
	}

	@FXML
	void setPriceButtonClicked(ActionEvent event) throws InterruptedException {

		// Setting up a dialog display
		TextInputDialog dialog = new TextInputDialog("Total price $$");
		dialog.setTitle("Set Price");
		dialog.setHeaderText("How much did the meal cost in total?");
		dialog.initStyle(StageStyle.UNDECORATED);
		String contentText = "Total price: ";
		dialog.setContentText(contentText);

		// Waiting for a respond from the user
		Optional<String> result = dialog.showAndWait();

		if (result.isPresent()) {
			// Checking input
			while (!isNumeric(result.get())) {
				dialog.setContentText(contentText + "\nPlease enter a no.");
				result = dialog.showAndWait();
				if (!result.isPresent())
					break;
			}
			// User respond
			if (result.isPresent()) {
				user.command("setPrice", kitchenName, day, month, year, Integer.parseInt(result.get()));
				feedbackMessage("Set Price", user.getFeedbackMsg());
			}
			updateDay();
		}
	}

	@FXML
	void lockDayButtonClicked(ActionEvent event) throws InterruptedException {

		// Setting up an alert display
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setTitle("Lock Day");
		alert.setHeaderText("You are about to lock this day.");
		alert.setContentText("Once this is done it cannot be undone! \n Are you sure you want to continue?");

		// Waiting for a respond from the user
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == ButtonType.OK) {
			user.command("lockDay", kitchenName, day, month, year, 0);

			if (result.isPresent()) {
				feedbackMessage("Lock Day", user.getFeedbackMsg());
			}

		} else {
			feedbackMessage("Lock Day", "Day was not locked.");
		}
		updateDay();
	}

	@FXML // Not implemented yet
	void setNoteButtonClicked(ActionEvent event) {
	}

	// Updating labels
	public void updateDay() throws InterruptedException {
		setValues(daySelected);
		dateLabel.setText(daySelected);
		chefsLabel.setText(getInfo("getChef"));
		priceLabel.setText(getInfo("getPrice"));
		shopperLabel.setText(getInfo("getShopper"));
		perPriceLabel.setText(getInfo("getPricePer"));
	}

	// Running the get-methods from the user class
	public String getInfo(String cmd) throws InterruptedException {
		user.command(cmd, kitchenName, day, month, year, 0);
		return user.getFeedbackMsg();
	}

	// Setting up the day values
	public void setValues(String date) {
		day = Integer.parseInt(date.substring(0, date.indexOf("/")));
		String str = date.substring(date.indexOf("/") + 1, date.length());
		month = Integer.parseInt(str.substring(0, str.indexOf("/")));
		str = str.substring(str.indexOf("/") + 1, str.length());
		year = Integer.parseInt(str);
	}

	// Displaying an alert with the a message
	public void feedbackMessage(String cmd, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setTitle(cmd);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
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

	public void setUser(User user) {
		this.user = user;
	}

	public void setDay(String date) {
		this.daySelected = date;
	}

	private void newScene(Event event, String path) throws IOException {
		((Node) event.getSource()).getScene().getWindow().hide();
		FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
		Parent root = loader.load();
		KitchenController kitchenController;
		DayController dayController;

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

		case "/application/DayOverview.fxml":
			x = 590;
			y = 490;
			try {
				dayController = loader.getController();
				dayController.setUser(user);
				dayController.updateTable(kitchenName, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "/application/DayWindow.fxml":
			x = 600;
			y = 500;
			try {
				dayController = loader.getController();
				dayController.setUser(user);
				dayController.setKitchenName(kitchenName);
				dayController.setDay(daySelected);
				dayController.updateDay();
				dayController.updateDayTable();
			} catch (Exception e) {
				e.printStackTrace();
			}

		case "/application/AddDay.fxml":
			dayController = loader.getController();
			dayController.setUser(user);
			dayController.setKitchenName(kitchenName);
			break;

		case "/application/Budget.fxml":
			BudgetController budgetController = loader.getController();
			budgetController.setUser(user);
			budgetController.setKitchenName(kitchenName);
			try {
				budgetController.setBalance();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
		if (path.equals("/application/DayWindow.fxml"))
			stage.setResizable(false);
		stage.show();
	}

	private void setKitchenName(String kitchenName) {
		this.kitchenName = kitchenName;

	}

}