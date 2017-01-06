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

@SuppressWarnings("unused") // A.K.A. STFU import warnings
public class Server {

	public static VirtualPort vp = new VirtualPort(1337); // 1337 h4x0r

	public Server() {
		initialize();
	}

	private void initialize() {
		Node server = new Node("Server", new TupleSpace());
		Agent monitor = new Monitor("Server Monitor");
		server.addAgent(monitor);
		server.start();

	}

	public static class Monitor extends Agent {

		public Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			Tuple t;
			Template what = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));
			try {
				// t = query(what, Self.SELF);
				// System.out.println(" " + name + " saw " +
				// t.getElementAt(String.class, 1));

				// This is an example of how to use getAll methods from jRESP
				// LinkedList<Tuple> list = new LinkedList<Tuple>();
				// list = queryAll(what);
				// list = getAll(what);

				t = get(what, Self.SELF);

				switch (t.getElementAt(String.class, 0)) {
				case "AddDay":
					System.out.println("Check info from kitchen and day");
					System.out.println("Send confirmation to the user.");

				case "CreateKitchen":
					System.out.println("Create Agent that will create a valid kitchen");
					System.out.println("Send confirmation to the user. Command was handled successfully.");
				}

			} catch (Exception e) {
				e.printStackTrace();
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
 * Create User
 * Create Kitchen (creates budget automatically)
 * Create Day
 * Assign user to day (sign up to eating at a day)
 * 
 * 2. Deux
 * Set cook on day (private)
 * Get cook on day
 * 
 * 3. Trois
 * Calculate who's cookin' (automatically distributes staffing)
 * 
 * 4. Quatre
 * Set price on a day's cooking (in Day.class)
 * Get price from day's cooking (send to user)
 * 
 * 5. Quins
 * Get WHO and HOW MANY are signed up to a day.
 * 
 * 6. Six
 * Make budget.
 * 
 * 7. Seben
 * Close signups.
 * 
 * 8. Acht
 * Handling of guests...?
 */
