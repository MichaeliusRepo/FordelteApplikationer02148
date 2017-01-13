package application;

import classes.Server;

public class DayTableColumn {
	private String date = "0", chef = "-", total = "0", attend = "0", user = "";
	private Server server;
	private String days[];
	private int i = 0, day = 1, month = 2, year = 3;

	public DayTableColumn(Server server, String user, int i) {
		this.server = server;
		server.getUser(user).command("addDay", 1, 2, 3, 0);

		this.days = server.getUser(user).getDays();
		System.out.println("" + days);
		this.i = i;
		this.user = user;

	}

	public String getDate() {
		if (days == null) {
			return "";
		}
		return days[i];
	}

	public String getChef() {
		server.getUser(user).command("getChef", day, month, year, 0);
		return server.getUser(user).getFeedbackMsg();
	}

	public String getTotal() {
		server.getUser(user).command("getAttendees", day, month, year, 0);
		return server.getUser(user).getFeedbackMsg();
	}

	public String getAttend() {
		server.getUser(user).command("getChef", day, month, year, 0);
		return server.getUser(user).getFeedbackMsg();
	}

	public boolean haveNextDay() {
		if (days != null) {
			if (i < days.length) {
				i++;
				return true;
			}
		}
		return false;
	}

}
