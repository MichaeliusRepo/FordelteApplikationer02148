package application;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.ListIterator;
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
import javafx.stage.WindowEvent;

public class DayController {
	private User user;
	private String kitchenName, daySelected;
	private int day, month, year;

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
	void viewPreviousCheckBoxClicked(ActionEvent event) throws Exception {
		System.out.println("box " + viewPreviousCheckBox.isSelected());
		updateTable(kitchenName, viewPreviousCheckBox.isSelected());
	}

	@FXML
	void updateButtonClicked(ActionEvent event) throws Exception {
		updateTable(kitchenName, true);

	}

	public void updateTable(String kitchenName, boolean previous) throws Exception {
		dayTableView.getItems().clear();
		totalTableColumn.setStyle("-fx-alignment: CENTER;");
		attendTableColumn.setStyle("-fx-alignment: CENTER;");
		this.kitchenName = kitchenName;
		titleLabel.setText(kitchenName);
		DayTable dayTable = new DayTable(user, kitchenName, 0);
		System.out.println("previous " + previous);
		for (int i = 0; i < dayTable.daysSize(); i++) {
			if (previous) {
				dayTableView.getItems().add(new DayTable(user, kitchenName, i));
			} else if (Calendar.getInstance().after(dayTable.getDate())) {
				System.out.println(
						"NOT previous " + dayTable.getDate() + " " + Calendar.getInstance().after(dayTable.getDate()));
				dayTableView.getItems().add(new DayTable(user, kitchenName, i));
			}
			System.out.println(Calendar.getInstance().after(dayTable.getDate()));
		}

		dateTableColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
		chefTableColumn.setCellValueFactory(new PropertyValueFactory<>("chef"));
		totalTableColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		attendTableColumn.setCellValueFactory(new PropertyValueFactory<>("attend"));

		dayTableView.setOnMousePressed(new EventHandler<MouseEvent>() {

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
			System.out.println("in add day: " + user.getFeedbackMsg());
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
	private TableView<DayNamesTable> dayNamesTable;

	@FXML
	private TableColumn<DayNamesTable, String> dayNameColumn;

	@FXML
	private TextArea dayNote;

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
		user.setFeedbackMsg(null);
		user.command("addChef", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		feedbackMessage("Add Chef", user.getFeedbackMsg());
		updateDay();

	}

	@FXML
	void attendDayButtonClicked(ActionEvent event) throws InterruptedException {
		user.setFeedbackMsg(null);

		TextInputDialog dialog = new TextInputDialog("No. of attendees");
		dialog.setTitle("Attend Day");
		dialog.setHeaderText("How many people should be expected?");
		dialog.setContentText(
				"Type 1 if you're coming alone, \n 2 if you're bringing a guest etc. \n If you no longer wish to attend type 0.");
		Optional<String> result = dialog.showAndWait();
		user.command("attendDay", kitchenName, day, month, year, Integer.parseInt(result.get()));

		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}

		if (result.isPresent()) {
			feedbackMessage("Attend Day", user.getFeedbackMsg());
		}
		updateDay();
	}

	@FXML
	void setPriceButtonClicked(ActionEvent event) throws InterruptedException {
		user.setFeedbackMsg(null);

		TextInputDialog dialog = new TextInputDialog("No. of attendees");
		dialog.setTitle("Set Price");
		dialog.setHeaderText("How much did the meal cost in total?");
		dialog.setContentText("Total price: ");
		Optional<String> result = dialog.showAndWait();

		user.command("setPrice", kitchenName, day, month, year, Integer.parseInt(result.get()));

		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}

		if (result.isPresent()) {
			feedbackMessage("Set Price", user.getFeedbackMsg());
		}
		updateDay();
	}

	@FXML
	void lockDayButtonClicked(ActionEvent event) throws InterruptedException {
		user.setFeedbackMsg(null);

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Lock Day");
		alert.setHeaderText("You are about to lock this day.");
		alert.setContentText("Once this is done it cannot be undone! \n Are you sure you want to continue?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == ButtonType.OK) {
			user.command("lockDay", kitchenName, day, month, year, 0);

			while (user.getFeedbackMsg() == null) {
				Thread.sleep(10);
			}

			if (result.isPresent()) {
				feedbackMessage("Lock Day", user.getFeedbackMsg());
			}

		} else {
			feedbackMessage("Lock Day", "Day was not locked.");
		}

		updateDay();
	}

	@FXML
	void setNoteButtonClicked(ActionEvent event) {

	}

	public void updateDay() throws InterruptedException {
		System.out.println(daySelected);
		setValues(daySelected);
		dateLabel.setText(daySelected);
		chefsLabel.setText(getChef());
		priceLabel.setText(getPrice());
		shopperLabel.setText(getShopper());
		perPriceLabel.setText(getPricePer());

	}

	public String getChef() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getChef", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public String getPrice() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getPrice", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public String getPricePer() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getPricePer", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public String getShopper() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getShopper", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public void setValues(String date) {
		day = Integer.parseInt(date.substring(0, date.indexOf("/")));
		String str = date.substring(date.indexOf("/") + 1, date.length());
		month = Integer.parseInt(str.substring(0, str.indexOf("/")));
		str = str.substring(str.indexOf("/") + 1, str.length());
		year = Integer.parseInt(str);
	}

	public void feedbackMessage(String cmd, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(cmd);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}
	//////////////////////////////
	// controller methods

	public void setUser(User user) {
		this.user = user;
	}

	public void setDay(String date) {
		this.daySelected = date;
	}

	private void newScene(Event event, String path) throws IOException {
		if (event != null)
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
				dayController.updateTable(kitchenName, true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;

		case "/application/DayWindow.fxml":
			x = 590;
			y = 490;
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