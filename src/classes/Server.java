package classes;

import classes.User;
import classes.Kitchen;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;
import org.cmg.resp.topology.VirtualPort;

@SuppressWarnings("unused")
// A guy is standing on the corner of the street smoking one cigarette after
// another. A lady walking by notices him and says "Hey, don't you know that
// those things can kill you? I mean, didn't you see the giant warning on the
// box?!" "That's OK" says the guy, puffing casually, "I'm a computer
// programmer" "So? What's that got to do with anything?" "We don't care about
// warnings. We only care about errors."

public class Server {

	public static VirtualPort vp = new VirtualPort(1337);
	public ArrayList<User> users = new ArrayList<User>();
	public ArrayList<Kitchen> kitchens = new ArrayList<Kitchen>();

	public Server() {
		Node server = new Node("Server", new TupleSpace());
		server.addPort(vp);
		Agent monitor = new Monitor("Server Monitor");
		server.addAgent(monitor);
		server.start();

		// Adding temporary users:
	
		User user2 = new User("Mathias", "kitchen 6");
		User user3 = new User("Emilie", "kitchen 6");
		User user4 = new User("Jon", "kitchen 6");
		User user5 = new User("Michael", "kitchen 6");
		User user1 = new User("Alexander", "kitchen 6");

		users.add(user1);
		users.add(user2);
		users.add(user3);
		users.add(user4);
		users.add(user5);
		

		Kitchen kitchen = new Kitchen("kitchen 6");
		kitchens.add(kitchen);
	}

	public class Monitor extends Agent {

		Tuple t;
		Tuple tupleData;
		Template what = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			while (true) {

				try {

					// example of how to use getAll methods from jRESP
					// LinkedList<Tuple> list = new LinkedList<Tuple>();

					// use this to copy all tuples
					// list = queryAll(what);

					// use this to obtain all tuples
					// list = getAll(what);

					t = get(what, Self.SELF);
					tupleData = t.getElementAt(Tuple.class, 1);

					switch (t.getElementAt(String.class, 0)) {
					case "addDay": case "addChef":
						String message;
						if(t.getElementAt(String.class, 0).equals("addDay")){
							message = "Server Monitor was requested to add date ";
						}else {
							message = "Server Monitor was requested to add chef on the date ";
						}

						System.out.println(
								 message + tupleData.getElementAt(Integer.class, 2)
										+ "/" + tupleData.getElementAt(Integer.class, 3) + "/"
										+ tupleData.getElementAt(Integer.class, 4) + " to "
										+ tupleData.getElementAt(String.class, 1));

						/*
						 * Since the tuple went in here, this is a great time to
						 * check that the user is a valid source and not
						 * malicious
						 */

						ServerAgent agent = new ServerAgent("AddDayAgent", t);
						exec(agent);

						break;

					case "addDay Feedback":

						tupleData = t.getElementAt(Tuple.class, 1);
						String userName = tupleData.getElementAt(String.class, 0);
						String kitchenName = tupleData.getElementAt(String.class, 1);
						PointToPoint p = new PointToPoint(userName, vp.getAddress());
						put(new Tuple(userName + " addDay Feedback", tupleData), p);
						System.out.println("Server transfers feedback from " + kitchenName + " to " + userName);

						break;

					case "CreateKitchen":
						System.out.println("Create Agent that will create a valid kitchen");
						System.out.println("Send confirmation to the user. Command was handled successfully.");
						break;
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}

	//
	public class ServerAgent extends Agent {

		Tuple t;

		public ServerAgent(String who, Tuple t) {
			super(who);
			this.t = t;
		}

		@Override
		protected void doRun() {
			try {
				Tuple tupleData = t.getElementAt(Tuple.class, 1);
				String kitchenName = tupleData.getElementAt(String.class, 1);
				PointToPoint p = new PointToPoint(kitchenName, vp.getAddress());
				put(t, p);
			} catch (Exception e) {
				e.printStackTrace();

			}

		}
	}

	public User getUser(String userName) {
		for (int i = 0; i < users.size(); i++) {
			if (userName.equals(users.get(i).userName)) {
				System.out.println("Welcome back, " + userName + "!");
				return users.get(i);
			}
		}
		System.out.println("sorry.. who are you?? " + userName);
		return null;
	}

	public void newUser(String userName, String kitchenName) {
		int j = -1;
		User user = new User(userName, kitchenName);
		users.add(user);

		for (int i = 0; i < kitchens.size(); i++) {
			if (kitchens.get(i).kitchenName.equals(kitchenName)) {
				j = i;
			}
		}

		if (j == -1) {
			System.out.println("Creating new kitchen: " + kitchenName);
			Kitchen kitchen = new Kitchen(kitchenName);
			kitchens.add(kitchen);
		}
	}
}

/*
 * 1. Uno!
 * 
 * Create User
 * 
 * Create Kitchen (creates budget automatically)
 * 
 * Create Day Assign user to day (sign up to eating at a day)
 * 
 * 2. Deux
 * 
 * Set cook on day (private)
 * 
 * Get cook on day
 * 
 * 3. Trois
 * 
 * Calculate who's cookin' (automatically distributes staffing)
 * 
 * 4. Quatre
 * 
 * Set price on a day's cooking (in Day.class)
 * 
 * Get price from day's cooking (send to user)
 * 
 * 5. Quins
 * 
 * Get WHO and HOW MANY are signed up to a day.
 * 
 * 6. Six
 * 
 * Make budget.
 * 
 * 7. Seben
 * 
 * Close signups.
 * 
 * 8. Acht
 * 
 * Handling of guests...?
 */
