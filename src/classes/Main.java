package classes;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Server dinnerClub = new Server();
		User user = null;
		Scanner in = new Scanner(System.in);

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

		for (int i = 0; i < 5; i++)
		{
			System.out.println("Please enter your command: ");
			String cmd = in.nextLine();

			switch (cmd) {

			case "Add Day":
				System.out.print("Enter day: ");
				int day = in.nextInt();
				System.out.print("Enter month: ");
				int month = in.nextInt();
				System.out.print("Enter year: ");
				int year = in.nextInt();
				user.addDay(day, month, year);
				break;

			case "Get my budget":
				break;

			case "something":
				break;
			}

		}
		System.out.println("Bye");

	}

}
