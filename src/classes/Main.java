package classes;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {
		Server dinnerClub = new Server();
		User user = null;
		Scanner in = new Scanner(System.in);
		int day, month, year;

		while (user == null) {
			System.out.print("Please enter user name: (write 'new user' to create new user): ");
			String userName = in.nextLine();
			System.out.println("You entered: " + userName);

			if (userName.equals("new user")) {
				System.out.print("Please enter a new user name: ");
				userName = in.nextLine();
				System.out.println("You entered: " + userName);
				System.out.print("Please enter a dinner club's name: ");
				String kitchenName = in.nextLine();
				dinnerClub.newUser(userName, kitchenName);
			}
			
			user = dinnerClub.getUser(userName);
		}

		for (int i = 0; i < 10; i++) {
			System.out.println("Please enter 'day' if you want to add/edit a day\n"
					+ "or 'Get Balance'/'Reset Balance': ");
			String cmd = in.nextLine();

			switch (cmd) {

			case "day":
				System.out.print("Enter day: (e.g. 01) ");
				day = Integer.parseInt(in.nextLine());
				System.out.print("Enter month: (e.g. 01) ");
				month = Integer.parseInt(in.nextLine());
				System.out.print("Enter year: (e.g. 2017) ");
				year = Integer.parseInt(in.nextLine());
				System.out.println("Please enter either \n "
						+ "'Add Day', 'Add Chef', 'Attend Day', 'Unattend Day', 'Remove Day', 'Lock Day', 'Get Chef', 'Set Price', 'Get Price', 'Get Attendees': ");
				String dayCmd = in.nextLine();
				System.out.println("you entered: " + dayCmd);
				
				switch (dayCmd) {
				case "Add Day":
					user.command("addDay",day, month, year, 0);
					break;

				case "Add Chef":
					user.command("addChef",day, month, year, 0);
					break;

				case "Attend Day":
					System.out.print("Please enter how many guest you want to bring: (e.g. 0) ");
					int guest = Integer.parseInt(in.nextLine());
					user.command("attendDay",day, month, year, guest);
					break;

				case "Unattend Day":
					user.command("unattendDay",day, month, year, 0);
					break;

				case "Lock Day":
					user.command("lockDay",day, month, year, 0);
					break;
					
				case "Remove Day":
					user.command("removeDay",day, month, year, 0);
					break;

				case "Get Chef":
					user.command("getChef",day, month, year, 0);
					break;

				case "Set Price":
					System.out.print("Please enter the total price: (e.g. 200) ");
					int price = Integer.parseInt(in.nextLine());
					user.command("setPrice",day, month, year, price);
					break;

				case "Get Price":
					user.command("getPrice",day, month, year, 0);
					break;
				case "Get Attendees":
					user.command("getAttendees",day, month, year, 0);
					break;
				}
				break;

			case "Reset Balance":
				user.command("resetUserBalance", 0, 0, 0, 0);
				break;

			case "Get Balance":
				user.command("getBalance", 0, 0, 0, 0);
				break;
			}

		}
		System.out.println("Bye");

	}

}
