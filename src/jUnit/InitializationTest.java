package jUnit;

import org.junit.Test;
import static org.junit.Assert.*;

import classes.Server;
import classes.User;

public class InitializationTest {
	@SuppressWarnings("unused")
	private final Server dinnerClub = new Server();
	private final String userName = "Steven Tyler";
	private final String kitchenName = "Love in an Elavator";
	private User user;
	private final int day = 29;
	private final int month = 2;
	private final int year = 2016;
	private final int milliseconds = 200;

	@Test
	public void setup() throws Exception {
		user = new User("","");
		user.userRequests("addUser", userName, kitchenName);
		Thread.sleep(milliseconds);
		
		user.command("addDay", day, month, year, 0);
		Thread.sleep(milliseconds);
		assertEquals(user.getFeedbackMsg(),"Day created.");
	}
}
