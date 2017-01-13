package application;

import java.util.LinkedList;

import classes.Server;
import classes.User;

public class DayTableColumn {
	private String date = "0", chef = "-", total = "0", attend = "0";
	private User user;
	private LinkedList<String> days;
	private int i = 0, day = 1, month = 2, year = 3;

	public DayTableColumn(User user, int i) {
		this.user = user;
		user.command("addDay", 1, 2, 3, 0);

		this.days = user.getDays();
		System.out.println("" + days);
		this.i = i;
		

	}


	public String getDate() {
		if (days == null) {
			return "";
		}
		return days.toString();
	}

	public String getChef() {
		user.command("getChef", day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public String getTotal() {
		user.command("getAttendees", day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public String getAttend() {
		user.command("getChef", day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public boolean haveNextDay() {
		if (days != null) {
			if (i < days.size()) {
				i++;
				return true;
			}
		}
		return false;
	}

}
