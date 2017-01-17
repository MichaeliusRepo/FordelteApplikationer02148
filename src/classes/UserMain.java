package classes;

import java.io.IOException;
import java.net.UnknownHostException;

public class UserMain {

	public static void main(String[] args) throws Exception {
		String køkken = "kitchen 6";
		User user = new User();
		user.addUser("Mathias24");
		user.createKitchen("kokken1221");
		user.command("addDay", køkken, 1, 1, 1, 0);
		user.command("attendDay", køkken, 1, 1, 1, 1);
		user.command("addChef", køkken, 1, 1, 1, 0);
		user.command("lockDay", køkken, 1, 1, 1, 0);
		user.command("setPrice", null, 1, 1, 1, 200);
		user.command("getBalance", køkken, 0, 0, 0, 0);
		user.command("resetUserBalance", køkken, 0, 0, 0, 0);
		
	}

}
