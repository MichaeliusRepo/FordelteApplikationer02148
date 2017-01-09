package classes;

import java.util.Scanner;

public class Main {
	
	public static void main(String[] args){
		Server dinnerClub = new Server();
		
		Scanner in = new Scanner(System.in);
	    System.out.print("Please enter user name: (write 'new user' to create new user)");
	    String userName = in.nextLine();      
	    System.out.println("You entered: " + userName);
	    if(userName.equals("new user")){
	    	System.out.print("Please enter a new user name: ");
		    userName = in.nextLine();      
		    System.out.println("You entered: " + userName);
	    	dinnerClub.newUser(userName);
	    }
	    User user = dinnerClub.getUser(userName);
	    
	    
	    System.out.print("Please enter our command: ");
	    String cmd = in.nextLine();
	    
	    switch (cmd) {
	    	case "Add Day":
	    		System.out.print("Enter day: ");
	    		//int day = in.nextInt();
	    		System.out.print("Enter mounth: ");
	    		//int month = in.nextInt();
	    		System.out.print("Enter year: ");
	    		//int year = in.nextInt();
	    		user.addDay(1, 1, 2017);
	    	break;
	    	
	    	case "Get my budget":
	    		
		    	break;
		    	
	    	case "something":
	    		
		    	break;
	    
	    }
	    
		
	}
	

}
