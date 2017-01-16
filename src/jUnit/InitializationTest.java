package jUnit;

import org.junit.Test;
import static org.junit.Assert.*;

import classes.Server;
import classes.User;

public class InitializationTest {

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
	private boolean result;

	@Test // that the program can actually run.
	public void setup() throws Exception {
		
		// user doesn't exist
		result = user.getUser(userName);
		Thread.sleep(milliseconds);
		assertFalse(result);
		
		// add user
		result = user.addUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
		assertTrue(result);
		
		// user already exists
		result = user.addUser(userName);
		Thread.sleep(milliseconds);
		assertFalse(user.getFeedbackMsg().contains(userName));
		assertFalse(result);
		
		// get user successfully
		result = user.getUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName));
		
		result = user.createKitchen(kitchenName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains("was created"));
		assertTrue(result);
		
		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "Day created.");
	}
	
	@Test
	public void getUser() throws Exception {
		result = user.addUser(userName);
		Thread.sleep(milliseconds);
		
		// get user successfully
		result = user.getUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName));
	}
	
	@Test
	public void massKitchenCreation() throws Exception {
		fail();
	}
}
