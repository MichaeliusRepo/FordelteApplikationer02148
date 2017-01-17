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
		
		result = user.getUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName));

		// Test that two users can log in.
		String userName2 = "Lenny Wolf";
		result = user.addUser(userName2);
		Thread.sleep(milliseconds);
		
		result = user.getUser(userName2);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName2));	
		
		// test alternating between usernames.
		result = user.getUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName));
		
		result = user.getUser(userName2);
		Thread.sleep(milliseconds);
		assertTrue(result);
		assertTrue(user.getFeedbackMsg().contains(userName2));	
		
		// user doesn't exist.
		result = user.getUser("Sir-not-appearing-in-this-movie.");
		Thread.sleep(milliseconds);
		assertFalse(result);
	}
	
	@Test
	public void massKitchenCreation() throws Exception {
		result = user.addUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
		assertTrue(result);
		
		result = user.createKitchen(kitchenName);
		Thread.sleep(milliseconds);
		assertTrue(result);
		
		result = user.createKitchen(kitchenName);
		Thread.sleep(milliseconds);
		assertFalse(result);
		
		result = user.createKitchen("Get It On");
		Thread.sleep(milliseconds);
		assertTrue(result);
		
		result = user.createKitchen("What Love Can Be");
		Thread.sleep(milliseconds);
		assertTrue(result);
		
		result = user.createKitchen("I Don't Wanna Miss A Thing");
		Thread.sleep(milliseconds);
		assertTrue(result);
		
		result = user.createKitchen("Don't Know What You Got ('Till It's Gone)");
		Thread.sleep(milliseconds);
		assertFalse(result);
	}
	
	@Test
	public void joinKitchen() throws Exception {
		result = user.addUser(userName);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
		assertTrue(result);
		
		assertFalse(user.joinKitchen(kitchenName));
		Thread.sleep(milliseconds);
		
		assertTrue(user.createKitchen(kitchenName));
		Thread.sleep(milliseconds);
		
		assertFalse(user.joinKitchen(kitchenName));
		Thread.sleep(milliseconds);
		
		user.addUser("Lenny Wolf");
		Thread.sleep(milliseconds);
		result = user.joinKitchen(kitchenName);
		assertTrue(result);
	}
	
	@Test
	public void invisibleKITCHENS() throws Exception {
		assertTrue(user.addUser(userName));
		Thread.sleep(milliseconds);
		
		assertTrue(user.createKitchen(kitchenName));
		Thread.sleep(milliseconds);
		
		assertTrue(user.addUser("Lenny Wolf"));
		Thread.sleep(milliseconds);
		
		assertTrue(user.getUser(userName));
		Thread.sleep(milliseconds);
		
		assertEquals(user.getKitchenName(0), kitchenName);
	}
	
	@Test
	public void getThemDays() throws Exception {
		user.addUser(userName);
		Thread.sleep(milliseconds);
		
		user.createKitchen(kitchenName);
		Thread.sleep(milliseconds);
		
		assertEquals(null, user.getDays(kitchenName));
		Thread.sleep(milliseconds);
		
		user.command("addDay", kitchenName, 7, 8, 9, 0);
		Thread.sleep(milliseconds);
		user.command("addDay", kitchenName, 4, 5, 6, 0);
		Thread.sleep(milliseconds);
		user.command("addDay", kitchenName, 1, 2, 3, 0);
		Thread.sleep(milliseconds);
		
		assertNotEquals(null, user.getDays(kitchenName));
		Thread.sleep(milliseconds);
		
		assertNotEquals(null, user.getDays(kitchenName));
		Thread.sleep(milliseconds);
		
	}
	
}
