package classes;

import java.io.IOException;
import java.net.UnknownHostException;

public class UserMain {

	public static void main(String[] args) throws Exception {
		String k�kken = "kitchen 6";
		User user = new User();
		user.addUser("Mathias24");
		user.createKitchen("kokken1221");
		user.command("addDay", k�kken, 1, 1, 1, 0);
		user.command("attendDay", k�kken, 1, 1, 1, 1);
		user.command("addChef", k�kken, 1, 1, 1, 0);
		user.command("lockDay", k�kken, 1, 1, 1, 0);
		user.command("setPrice", null, 1, 1, 1, 200);
		user.command("getBalance", k�kken, 0, 0, 0, 0);
		user.command("resetUserBalance", k�kken, 0, 0, 0, 0);
		
	}

}
