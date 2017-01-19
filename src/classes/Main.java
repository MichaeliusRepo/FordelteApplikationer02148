package classes;

// 02148 Introduction to Coordination in Distributed Applications
// 19. Januar 2017
// Team 9 - Dinner Club
//	- Alexander Kristian Armstrong, s154302
//	- Michael Atchapero,  s143049
//	- Mathias Ennegaard Asmussen, s154219
//	- Emilie Isabella Dahl, s153762
//	- Jon Ravn Nielsen, s136448

import java.util.Scanner;

public class Main {
	// Creating a server so the user can communicate with the other classes
	static Server dinnerClub = new Server();
	static String str = "What Love Can Be";
	static User user = new User();
	static Scanner in = new Scanner(System.in);
	static int day;
	static int month;
	static int year;

	public static void main(String[] args) throws Exception {
		logIn();
		user.createKitchen(str);
		runApplication();
	}

	private static void logIn() throws Exception {
		
		while (user.getUserName().equals("")) {
			System.out.print("Write 'addUser' to create a new user, or 'getUser' to log in to an existing account.");
			String cmd = in.nextLine();
			String userName;

			System.out.print("Please enter an user name: ");
			userName = in.nextLine();

			if (cmd.equals("addUser"))
				user.addUser(userName);
			if (cmd.equals("getUser"))
				user.getUser(userName);

			Thread.sleep(200);
		}
	}

	private static void logOut() throws Exception {
		user = new User();
		logIn();
	}

	private static void runApplication() throws Exception {

		while (true) {
			System.out.println("Type help for all commands. Please enter 'day' if you want to add/edit a day.");
			String cmd = in.nextLine();

			switch (cmd) {

			case "day":
			case "Day":
				// Does not check for wrong inputs, but mainly intended for own testing
				System.out.print("Enter day: (e.g. 01) ");
				day = Integer.parseInt(in.nextLine());
				System.out.print("Enter month: (e.g. 01) ");
				month = Integer.parseInt(in.nextLine());
				System.out.print("Enter year: (e.g. 2017) ");
				year = Integer.parseInt(in.nextLine());
				System.out.println("Please enter either \n "
						+ "'addDay', 'addChef', 'attendDay', 'unattendDay', 'removeDay', 'lockDay', 'getChef', 'setPrice', 'getPrice', 'getAttendees':");
				String dayCmd = in.nextLine();

				switch (dayCmd) {
				case "attendDay":
					System.out.print("How many people will you bring? (including yourself) ");
					int guest = Integer.parseInt(in.nextLine());
					user.command(dayCmd, str, day, month, year, guest);
					break;
				case "setPrice":
					System.out.print("Please enter the total price: (e.g. 200) ");
					int price = Integer.parseInt(in.nextLine());
					user.command(dayCmd, str, day, month, year, price);
					break;
				case "addDay":
				case "addChef":
				case "unattendDay":
				case "lockDay":
				case "removeDay":
				case "getChef":
				case "getPrice":
				case "getAttendees":
					user.command(dayCmd, str, day, month, year, 0);
					break;
				}
				break;

			case "resetUserBalance":
			case "getBalance":
			case "getDays":
				user.command(cmd, str, 0, 0, 0, 0);
				break;

			case "help":
			case "Help":
				System.out.println("Valid commands:");
				System.out.println("day/Day");
				System.out.println("resetUserBalance");
				System.out.println("getBalance");
				System.out.println("getDays");
				System.out.println("exit/Log Out/logout");
				break;

			case "exit":
			case "Log Out":
			case "logout":
				logOut();
				break;
			}
			Thread.sleep(200);
		}
	}

}
