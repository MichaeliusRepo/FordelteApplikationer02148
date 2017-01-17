package application;

import java.util.Calendar;
import java.util.LinkedList;
import classes.User;

public class DayTable {
	private String date, chef, total, attend, kitchenName;
	private User user;
	private LinkedList<String> days;
	private int i, day, month, year;

	public DayTable(User user, String kitchenName, int i) throws Exception {
		this.user = user;
		this.kitchenName = kitchenName;
		this.i = i;
		
		System.out.println("dayTable: " + user.getDays(kitchenName));
		this.days = user.getDays(kitchenName);
		
		if (days != null) {
			if(days.size()>i)
				setValues(days.get(i));
		}
	}

	public String getDate() {
		return days.get(i);
	}

	public String getChef() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getChef", kitchenName, day, month, year, 0);
		while(user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public String getTotal() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getPrice", kitchenName, day, month, year, 0);
		while(user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		return user.getFeedbackMsg();
	}

	public String getAttend() throws InterruptedException {
		user.setFeedbackMsg(null);
		user.command("getAttendees", kitchenName, day, month, year, 0);
		while(user.getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
		System.out.println(user.getFeedbackMsg());
		return user.getFeedbackMsg();
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

}
