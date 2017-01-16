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

	@Test // that the program can actually run.
	public void setup() throws Exception {
		user.addUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
		
		user.createKitchen(kitchenName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains("was created"));
		
		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), "Day created.");
	}
}
