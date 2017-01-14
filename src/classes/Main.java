package classes;

import java.util.Scanner;

public class Main {

	static Server dinnerClub = new Server();
	static User user = new User("", "");
	static Scanner in = new Scanner(System.in);
	static int day;
	static int month;
	static int year;

	public static void main(String[] args) throws Exception {
		logIn();
		runApplication();
	}

	private static void logIn() throws Exception {
		while (user.getUserName().equals("")) {
			System.out.print("Write 'addUser' to create a new user, or 'getUser' to log in to an existing account.");
			String cmd = in.nextLine();
			String userName;
			String kitchenName;

			if (cmd.equals("addUser") || cmd.equals("getUser")) {
				System.out.print("Please enter an user name: ");
				userName = in.nextLine();
				System.out.print("Please enter a dinner club's name: ");
				kitchenName = in.nextLine();
				user.userRequests(cmd, userName, kitchenName);
				Thread.sleep(200);
			}
		}
	}

	private static void logOut() throws Exception {
		user = new User("", "");
		logIn();
	}

	private static void runApplication() throws Exception {

		while (true) {
			Thread.sleep(200);

			System.out.println("Type help for all commands. Please enter 'day' if you want to add/edit a day.");
			String cmd = in.nextLine();

			switch (cmd) {

			case "day":
			case "Day":
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
					System.out.print("Please enter how many guest you want to bring: (e.g. 0) ");
					int guest = Integer.parseInt(in.nextLine());
					user.command(dayCmd, day, month, year, guest);
					break;
				case "setPrice":
					System.out.print("Please enter the total price: (e.g. 200) ");
					int price = Integer.parseInt(in.nextLine());
					user.command(dayCmd, day, month, year, price);
					break;
				case "addDay":
				case "addChef":
				case "unattendDay":
				case "lockDay":
				case "removeDay":
				case "getChef":
				case "getPrice":
				case "getAttendees":
					user.command(dayCmd, day, month, year, 0);
					break;
				}
				break;

			case "resetUserBalance":
			case "getBalance":
			case "getDays":
				user.command(cmd, 0, 0, 0, 0);
				break;

			case "help":
			case "Help":
				System.out.println("Valid commands:");
				System.out.println("day/Day");
				System.out.println("Reset Balance");
				System.out.println("Get Balance");
				System.out.println("Get Days");
				System.out.println("exit/Log Out/logout");
				break;

			case "exit":
			case "Log Out":
			case "logout":
				logOut();
				break;
			}
		}
	}

}
