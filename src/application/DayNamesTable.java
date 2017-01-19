package application;

// 02148 Introduction to Coordination in Distributed Applications
// 19. Januar 2017
// Team 9 - Dinner Club
//	- Alexander Kristian Armstrong, s154302
//	- Michael Atchapero,  s143049
//	- Mathias Ennegaard Asmussen, s154219
//	- Emilie Isabella Dahl, s153762
//	- Jon Ravn Nielsen, s136448


import java.util.LinkedList;

import classes.User;

public class DayNamesTable {
	private int i;
	private LinkedList<String> list;

	// This class is important for the table rows display
	public DayNamesTable(User user, String kitchenName, int day, int month, int year, int i)
			throws InterruptedException {
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
