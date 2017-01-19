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

import java.util.LinkedList;
import classes.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

public class DayTable {
	private String kitchenName;
	private User user;
	private LinkedList<String> days, attendees;
	private int i, day, month, year;

	// This class is created to show the rows in the table
	public DayTable(User user, String kitchenName, int i) throws Exception {
		this.user = user;
		this.kitchenName = kitchenName;
		this.i = i;
		this.days = user.getDays(kitchenName);

		if (days != null) {
			if (days.size() > i) {
				setValues(days.get(i));
				attendees = user.getAttendees(kitchenName, day, month, year);
			}
		}
	}

	public String getDate() {
		return days.get(i);
	}

	public String getChef() throws InterruptedException {
		user.command("getChef", kitchenName, day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public String getTotal() throws InterruptedException {
		return "" + attendees.size();
	}

	public Button getAttend() throws InterruptedException {
		Button button = new Button();

		if (attendees.contains(user.getUserName())) {
			button.setText("Unattend");
		} else {
			button.setText("Attend");
		}

		// Adding an action to the button in the table
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String str = "";
				
				// Changing the method call and setting the buttons text
				if (button.getText().equals("Attend")) {
					try {
						user.command("attendDay", kitchenName, day, month, year, 1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					str = "Unattend";
				} else {
					try {
						user.command("attendDay", kitchenName, day, month, year, 0);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					str = "Attend";
				}
				// Letting the user know if the day is locked
				if (user.getFeedbackMsg().contains("Day is locked")) {
					feedbackMessage("Attend Day", "Sorry, the day has been locked");
					str = "Locked";
				}
				button.setText(str);
			}
		});

		return button;
	}

	public int daysSize() {
		return days.size();
	}

	// Changing the day-string to integers
	public void setValues(String date) {
		day = Integer.parseInt(date.substring(0, date.indexOf("/")));
		String str = date.substring(date.indexOf("/") + 1, date.length());
		month = Integer.parseInt(str.substring(0, str.indexOf("/")));
		str = str.substring(str.indexOf("/") + 1, str.length());
		year = Integer.parseInt(str);
	}

	public void feedbackMessage(String cmd, String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.initStyle(StageStyle.UNDECORATED);
		alert.setTitle(cmd);
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

}
