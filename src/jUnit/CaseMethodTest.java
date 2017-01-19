
package jUnit;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import classes.Server;
import classes.User;

public class CaseMethodTest {

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
		Thread.sleep(milliseconds);

		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
	}

	@Test
	public void lockThenSetPrice() throws Exception {
		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), "Dagen findes allerede");
		assertTrue(user.getFeedbackMsg().contains("was locked"));
		
		String feedback = null;

		user.command("setPrice", kitchenName, day, month, year, 200);
		Thread.sleep(milliseconds);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}

	@Test
	public void setTwoChefs() throws Exception {
		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " was added as a chef.");

		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " is already a chef.");

		user.addUser("Mathias");
		Thread.sleep(milliseconds);
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertNotEquals(user.getFeedbackMsg(), null);
		assertNotEquals(user.getFeedbackMsg(), "Den valgte dag findes ikke");
		assertEquals(user.getFeedbackMsg(), "Mathias was added as a chef.");

		user.addUser("Emilie");
		Thread.sleep(milliseconds);
		user.command("attendDay", kitchenName, day, month, year, 1);
		Thread.sleep(milliseconds);
		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertFalse(user.getFeedbackMsg().contains("Emilie"));
	}

	@Test
	public void getTwoChefs() throws Exception {
		setTwoChefs();

		user.command("getChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertTrue(user.getFeedbackMsg().contains(userName));
		assertTrue(user.getFeedbackMsg().contains("Mathias"));
	}

}
