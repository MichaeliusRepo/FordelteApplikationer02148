package application;

import java.util.LinkedList;

import classes.Server;
import classes.User;

public class DayTable {
	private String date, chef, total, attend;
	private User user;
	private LinkedList<String> days;
	private int i, day, month, year, counter;

	public DayTable(User user, int i) {
		this.user = user;
		//user.command("addDay", 1, 1, 1, 0);
		//user.command("addDay", 2, 2, 2, 0);

		this.days = user.getDays();
		setValues(i);
	}


	public String getDate() {
		
		if (days == null) {
			return "null";
		}
		return ""+days.get(i);
	}

	public String getChef() {
		//user.command("getChef", day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public String getTotal() {
		//user.command("getPrice", day, month, year, 0);
		return user.getFeedbackMsg();
	}

	public String getAttend() {
		//user.command("getAttendees", day, month, year, 0);
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


	public void setValues(int i) {
		//testing gui
		if(days == null){
			days = new LinkedList<String>();
			days.add("01/01-2017");
			days.add("02/02-2017");
			//System.out.println("in DayTable: user: " + user +" days(null): " + days);
		}
		this.i = i;
		
		day = Integer.parseInt(days.get(i).substring(0, days.get(i).indexOf("/")));
		month = Integer.parseInt(days.get(i).substring(days.get(i).indexOf("/")+1, days.get(i).indexOf("-")));
		year = Integer.parseInt(days.get(i).substring(days.get(i).indexOf("-")+1, days.get(i).length()));
	}

}
