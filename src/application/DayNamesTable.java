package application;

import java.util.LinkedList;
import java.util.ListIterator;

import classes.User;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class DayNamesTable {
	private User user;
	private String kitchenName, name;
	private int day, month, year, i;
	private LinkedList<String> list;

	public DayNamesTable(User user, String kitchenName, int day, int month, int year, int i)
			throws InterruptedException {
		this.user = user;
		this.kitchenName = kitchenName;
		this.day = day;
		this.month = month;
		this.year = year;
		this.i = i;
		this.list = user.getAttendees(kitchenName, day, month, year);
	}

	public String getName() {
		return list.get(i);
	}

	public int listSize() {
		return list.size();
	}
}
