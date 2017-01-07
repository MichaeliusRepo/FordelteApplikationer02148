package classes;

import classes.User;
import classes.Kitchen;

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

	public static VirtualPort vp = new VirtualPort(1337); // 1337 h4x0r

	public Server() {
		initialize();
	}

	private void initialize() {
		Node server = new Node("Server", new TupleSpace());
		server.addPort(vp);
		Agent monitor = new Monitor("Server Monitor");
		server.addAgent(monitor);
		server.start();

		// MOCK CODE
		String kitchenName = "Den Store Bagedyst";
		User NortiousMaximus = new User("Nortious Maximus", kitchenName);
		Kitchen kitchen = new Kitchen(kitchenName);
		NortiousMaximus.addDay(7, 1, 2017);

	}

	public static class Monitor extends Agent {

		Tuple t;
		Template what = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			while (true) {

				try {
					// t = query(what, Self.SELF);
					// System.out.println(" " + name + " saw " +
					// t.getElementAt(String.class, 1));

					// This is an example of how to use getAll methods from
					// jRESP
					// LinkedList<Tuple> list = new LinkedList<Tuple>();

					// use this to copy all tuples
					// list = queryAll(what);

					// use this to obtain all tuples
					// list = getAll(what);

					t = get(what, Self.SELF);

					switch (t.getElementAt(String.class, 0)) {
					case "addDay":

						Tuple tupleData = t.getElementAt(Tuple.class, 1);
						System.out.println("Server Monitor saw date " + tupleData.getElementAt(Integer.class, 1) + "/"
								+ tupleData.getElementAt(Integer.class, 2) + "/"
								+ tupleData.getElementAt(Integer.class, 3));
						
						System.out.println("Pretend that I validated adding this date.");
						System.out.println("Pretend that I added a day from serverside!");
						
						String userSpaceName = tupleData.getElementAt(String.class, 0);
						PointToPoint p = new PointToPoint(userSpaceName, vp.getAddress());
						put(new Tuple(userSpaceName,tupleData),p);
						System.out.println("Server sent feedback.");

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

	// Copy-pasta this agent whenever you need to make a new one!
	public static class TemplatusMaximus extends Agent {

		public TemplatusMaximus(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				// insert code here
			} catch (Exception e) {
				e.printStackTrace();
			}
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
