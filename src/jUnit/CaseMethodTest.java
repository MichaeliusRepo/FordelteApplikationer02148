
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

		user.command("addDay", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
	}

	@Test
	public void lockThenSetPrice() throws Exception {
		assertEquals(user.getFeedbackMsg(), userName + " added with 0 attendees.");

		user.command("lockDay", kitchenName, day, month, year, 0);
		Thread.sleep(100);
		assertNotEquals(user.getFeedbackMsg(), "Dagen findes allerede");

		String feedback = null;

		user.command("setPrice", kitchenName, day, month, year, 200);
		Thread.sleep(100);
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

		User user2 = new User();
		String str2 = "Mathias";
		user2.addUser(str2);
		Thread.sleep(milliseconds);

		user2.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		String user2Feedback = user.getFeedbackMsg();
		assertNotEquals(user2Feedback, null);
		assertNotEquals(user2Feedback, "Den valgte dag findes ikke");
		assertEquals(user2.getFeedbackMsg(), "Mathias was added as a chef.");

		User user3 = new User();
		String str3 = "Emilie";
		user3.addUser(str3);
		Thread.sleep(milliseconds);

		user3.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		String user3Feedback = user.getFeedbackMsg();
		assertEquals(user3Feedback, "There are already two chefs.");
	}

	@Test
	public void getTwoChefs() throws Exception {
		user.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(), userName + " was added as a chef.");

		User user2 = new User();
		String str2 = "Mathias";
		user2.addUser(str2);
		Thread.sleep(milliseconds);

		user2.command("addChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		String user2Feedback = user.getFeedbackMsg();
		assertNotEquals(user2Feedback, null);
		assertNotEquals(user2Feedback, "Den valgte dag findes ikke");
		assertEquals(user2.getFeedbackMsg(), "Mathias was added as a chef.");

		user2.command("getChef", kitchenName, day, month, year, 0);
		Thread.sleep(milliseconds);
		user2Feedback = user.getFeedbackMsg();
		assertTrue(user2Feedback.contains(userName));
		assertTrue(user2Feedback.contains(str2));
	}

}
