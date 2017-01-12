
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
	
	private Server dinnerClub = new Server();
	private String userName = "Lenny Wolf";
	private String kitchenName = "What Love Can Be";
	private User user;
	private int day = 29;
	private int month = 2;
	private int year = 2016;
	
	@Before
	public void setup() throws Exception {
		dinnerClub.newUser(userName, kitchenName);
		user = dinnerClub.getUser(userName);
		
		user.command("addDay", day, month, year, 0);
		Thread.sleep(100);
		
		user.command("attendDay", day, month, year, 0);
		Thread.sleep(100);
	}
	
	@Test
	public void lockThenSetPrice() throws Exception {
		assertEquals(user.getFeedbackMsg(),userName + " added with 0 attendees.");
		
		user.command("lockDay", day, month, year, 0);
		Thread.sleep(100);
		// assertEquals(user.getFeedbackMsg,"Dagen findes allerede");
		
		String feedback = null;
		
		user.command("setPrice", day, month, year, 200);
		Thread.sleep(100);
		feedback = user.getFeedbackMsg();
		assertNotEquals(feedback, null);
	}
	
}
