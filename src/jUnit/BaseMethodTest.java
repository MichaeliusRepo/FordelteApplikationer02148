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

	@SuppressWarnings("unused")
	private final Server dinnerClub = new Server();
	private final String userName = "Steven Tyler";
	private final String kitchenName = "Love in an Elavator";
	private final User user = new User();
	private final int day = 29;
	private final int month = 2;
	private final int year = 2016;
	private final int milliseconds = 200;

	@Before
	public void setup() throws Exception {
		user.addUser(userName);
		Thread.sleep(milliseconds);

		user.createKitchen(kitchenName);

		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
	}

	@Test
	public void addDay() throws Exception {
		assertEquals(user.getFeedbackMsg(), "Day created.");

		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "Day already exists");
	}

	@Test
	public void addChef() throws Exception {
		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " was added as a chef.");

		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " is already a chef.");
	}

	@Test
	public void attendDay() throws Exception {
		user.command("attendDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " added with 0 attendees.");

		user.command("attendDay", kitchenName, day, month, year, 5);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " was already set to attend. Day has been updated with any guests added.");
	}

	@Test
	public void unattendDay() throws Exception {
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " added with 1 attendees.");

		user.command("unattendDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " is no longer attending on: " + day + "/" + month + "/" + year);

		user.command("unattendDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " isn't set to attend that day.");
	}

	@Test
	public void lockDay() throws Exception {
		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertTrue(user.getFeedbackMsg().contains("has no attendees"));

		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);

		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertTrue(user.getFeedbackMsg().contains("was locked"));

		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertFalse(user.getFeedbackMsg().contains("was locked"));
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertNotEquals(user.getFeedbackMsg().length(), 12);
		assertTrue(user.getFeedbackMsg().contains("locked"));
	}

	@Test
	public void removeDay() throws Exception {
		user.command("removeDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "Day was deleted.");

		user.command("removeDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "The chosen day doesn't exist.");
	}

	@Test
	public void getChef() throws Exception {
		user.command("getChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "No chefs assigned");

		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " was added as a chef.");

		user.command("getChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName);
	}

	@Test
	public void setPrice() throws Exception {
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);

		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains("was locked"));

		user.command("setPrice", kitchenName, day, month, year, 200);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "The price was already set to 0, but has been replaced.");
		
		user.command("setPrice", kitchenName, day, month, year, 300);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "The price was already set to 200, but has been replaced.");
	}

	@Test
	public void getPrice() throws Exception {
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);

		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains("was locked"));

		user.command("setPrice", kitchenName, day, month, year, 200);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "The price was already set to 0, but has been replaced.");

		user.command("getPrice", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains("200"));
	}

	@Test
	public void getAttendees() throws Exception {
		user.command("getAttendees", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertFalse(user.getFeedbackMsg().contains("Attendees:"));
		
		user.command("attendDay", kitchenName, day, month, year, 2);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " added with 2 attendees.");

		user.command("getAttendees", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
	}

	// TODO
	
	@Test
	public void resetBalance() throws Exception {
		getBalance();
		
		user.getUser("Lenny Wolf");
		Thread.sleep(milliseconds);
		assertEquals("Lenny Wolf",user.getUserName());
		
		user.command("resetUserBalance", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		user.command("getBalance", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertFalse(user.getFeedbackMsg().contains("100.0"));
		assertTrue(user.getFeedbackMsg().contains("0.0"));
	}

	@Test
	public void getBalance() throws Exception {
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);

		// Switch to another user
		user.addUser("Lenny Wolf");
		Thread.sleep(milliseconds);
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
		
		// Switch back to default user
		user.getUser(userName);
		Thread.sleep(milliseconds);
		
		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		user.command("setPrice", kitchenName, day, month, year, 200);
		Thread.sleep(milliseconds);
		user.command("getBalance", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertTrue(user.getFeedbackMsg().contains("-100.0"));
		
		user.getUser("Lenny Wolf");
		Thread.sleep(milliseconds);
		user.command("getBalance", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Day created.");
		assertTrue(user.getFeedbackMsg().contains("100.0"));
	}

}
