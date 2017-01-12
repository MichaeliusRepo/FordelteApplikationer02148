package jUnit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import classes.Server;
import classes.User;

public class BaseMethodTest {

	// File > Import > Git
	// Use this link below
	// https://github.com/junit-team/junit4
	// Right click Dinner Club Project >
	// Properties > Java Build Path > Project > Add > JUnit

	private final Server dinnerClub = new Server();
	private final String userName = "Lenny Wolf";
	private final String kitchenName = "What Love Can Be";
	private User user;
	private final int day = 29;
	private final int month = 2;
	private final int year = 2016;

	@Before
	public void setup() throws Exception {
		dinnerClub.newUser(userName, kitchenName);
		user = dinnerClub.getUser(userName);

		user.command("addDay", day, month, year, 0);
		Thread.sleep(100);
	}

	@Test
	public void addDay() throws Exception {
		assertEquals(user.getFeedbackMsg(), "Dagen blev lavet");

		user.command("addDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), "Dagen findes allerede");
	}

	@Test
	public void addChef() throws Exception {
		user.command("addChef", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " was added as a chef.");

		user.command("addChef", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " is already a chef.");

		String str = "Mathias";
		dinnerClub.newUser(str, kitchenName);
		User user2 = dinnerClub.getUser(str);
		
		String user2Feedback = null;
		
		user2.command("addChef", day, month, year, 0);
		Thread.sleep(100);
		user2Feedback = user.getFeedbackMsg();
		assertNotEquals(user2Feedback, null);
		assertNotEquals(user2Feedback, "Den valgte dag findes ikke");
		assertEquals(user2.getFeedbackMsg(), "Mathias was added as a chef.");

		String str2 = "Emilie";
		dinnerClub.newUser(str2, kitchenName);
		User user3 = dinnerClub.getUser(str2);
		user3.command("addChef", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user3.getFeedbackMsg(), "There are already two chefs.");
	}

	@Test
	public void attendDay() throws Exception {
		user.command("attendDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " added with 0 attendees.");

		user.command("attendDay", day, month, year, 5);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " added with 5 attendees.");
	}

	@Test
	public void unattendDay() throws Exception {
		user.command("attendDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " added with 0 attendees.");

		user.command("unattendDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " is no longer attending on: " + day + "/" + month + "/" + year);

		user.command("unattendDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), userName + " isn't set to attend that day.");
	}

	@Test
	public void lockDay() throws Exception {
		String feedback = null;
		user.command("lockDay", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);

		feedback = null;
		user.command("lockDay", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void removeDay() throws Exception {
		user.command("removeDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), "Dagen er blevet slettet");

		user.command("removeDay", day, month, year, 0);
		Thread.sleep(100);
		assertEquals(user.getFeedbackMsg(), "Den valgte dag findes ikke");
	}

	@Test
	public void getChef() throws Exception {		
		String feedback = null;
		user.command("getChef", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void setPrice() throws Exception {
		String feedback = null;
		user.command("setPrice", day, month, year, 200);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void getPrice() throws Exception {
		String feedback = null;
		user.command("getPrice", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void getAttendees() throws Exception {
		String feedback = null;
		user.command("getAttendees", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void resetBalance() throws Exception {
		String feedback = null;
		user.command("resetUserBalance", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void getBalance() throws Exception {
		String feedback = null;
		user.command("getBalance", day, month, year, 0);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

}

