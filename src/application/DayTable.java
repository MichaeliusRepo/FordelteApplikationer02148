package application;

import java.util.LinkedList;
import classes.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.StageStyle;

public class DayTable {
	private String date, chef, total, attend, kitchenName;
	private User user;
	private LinkedList<String> days, attendees = new LinkedList();
	private int i, day, month, year;

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
		user.setFeedbackMsg(null);
		user.command("getChef", kitchenName, day, month, year, 0);
		while (user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
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

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				user.setFeedbackMsg(null);
				String str = "";
				if (button.getText().equals("Attend")) {
					user.command("attendDay", kitchenName, day, month, year, 1);
					str = "Unattend";
				} else {
					user.command("attendDay", kitchenName, day, month, year, 0);
					str = "Attend";
				}
				while (user.getFeedbackMsg() == null) {
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
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
