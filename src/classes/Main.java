package classes;

import java.util.Scanner;

public class Main {

	public static void main(String[] args) {

		Server dinnerClub = new Server();
		User user = null;
		Scanner in = new Scanner(System.in);
		int day,month,year;

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

		for (int i = 0; i < 10; i++)
		{
			System.out.println("Please enter 'day' if you want to add/edit a day: ");
			String cmd = in.nextLine();

			switch (cmd) {

			case "day":
				System.out.print("Enter day: ");
				day = Integer.parseInt(in.nextLine());
				System.out.print("Enter month: ");
				month = Integer.parseInt(in.nextLine());
				System.out.print("Enter year: ");
				year = Integer.parseInt(in.nextLine());
				System.out.println("Please enter 'Add Day', 'Add Chef', 'Attend Day', 'Unattend Day' or 'Lock Day': ");
				String dayCmd = in.nextLine();
				System.out.println("you entered: "+ dayCmd);
				switch(dayCmd){
				case "Add Day":
					System.out.println("adding day");
					user.addDay(day, month, year);
					break;
					
				case "Add Chef":
					user.addChef(day, month, year);
					break;

				case "Attend Day":
					user.attendDay(day, month, year);
					break;
					
				case "Unattend Day":
					user.unattendDay(day, month, year);
					break;
					
				case "Lock Day":
					user.lockDay(day, month, year);
					break;	
				}
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
