package classes;

import classes.User;
import classes.Kitchen;

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;

@SuppressWarnings("unused")
public class Server {

	public final static VirtualPort vp = new VirtualPort(1337);
	private Node server = new Node("Server", new TupleSpace());
	private Tuple userTuple = null;

	public Server() {
		server.addPort(vp);
		Agent monitor = new Monitor("Monitor");
		server.addAgent(monitor);
		server.start();
	}

	private class Monitor extends Agent {

		private Tuple t;
		private Tuple tupleData;
		private Template commandTuples = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			while (true) {
				try {
					t = get(commandTuples, Self.SELF);
					String command = t.getElementAt(String.class, ECommand.COMMAND.getValue());
					String userName = t.getElementAt(String.class, ECommand.USERNAME.getValue());
					String kitchenName = t.getElementAt(String.class, ECommand.KITCHEN.getValue());
					tupleData = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

					Template getObject;
					boolean exists;

					/*
					 * Perhaps we should implement switch cases here, and create
					 * methods for each case, instead of an if statement?
					 */

					// Perhaps you should make your own code work first.
					// Then we'll talk business.

					switch (command) {
					default:
						System.out.println(
								"Server Monitor was requested to " + t.getElementAt(ECommand.COMMAND.getValue()) + ", "
										+ tupleData.getElementAt(Integer.class, EDayData.DAY.getValue()) + "/"
										+ tupleData.getElementAt(Integer.class, EDayData.MONTH.getValue()) + "/"
										+ tupleData.getElementAt(Integer.class, EDayData.YEAR.getValue()) + " to "
										+ t.getElementAt(String.class, ECommand.KITCHEN.getValue()));
						/*
						 * Since the tuple went in here, this is a great time to
						 * check that the user is a valid source and not
						 * malicious
						 */
						exec(new ServerAgent(command, t));
						break;

					case "addUser":
						getObject = new Template(new ActualTemplateField(userName),
								new FormalTemplateField(Tuple.class));
						exists = (queryp(getObject) != null);

						if (!exists) // Put userData into Server if
										// nonexistent
							put(new Tuple(userName, new Tuple("", "", "", "")), Self.SELF);

						// Feedback for user if creation was successful.
						put(new Tuple(command, userName, "", true, new Tuple(exists)), Self.SELF);
						break;

					case "getUser":
						getObject = new Template(new ActualTemplateField(userName),
								new FormalTemplateField(Tuple.class));
						Tuple result = queryp(getObject);

						exists = (result != null);

						// Feedback for user if creation was successful.
						put(new Tuple(command, userName, "", true, new Tuple(exists, result)), Self.SELF);
						break;

					case "createKitchen":
						getObject = new Template(new ActualTemplateField(kitchenName),
								new FormalTemplateField(Kitchen.class));
						exists = (queryp(getObject) != null);

						if (!exists) // put data in
							put(new Tuple(kitchenName, new Kitchen(kitchenName)), Self.SELF);

						// Feedback for user if creation was successful.
						put(new Tuple(command, userName, kitchenName, true, new Tuple(exists)), Self.SELF);
						break;

					case "joinKitchen":
						getObject = new Template(new ActualTemplateField(kitchenName),
								new FormalTemplateField(Tuple.class));
						exists = (queryp(getObject) != null);

						put(new Tuple(command, userName, kitchenName, true, new Tuple(exists)), Self.SELF);
						break;

					case "updateUser":
						getObject = new Template(new ActualTemplateField(userName),
								new FormalTemplateField(Tuple.class));
						get(getObject, Self.SELF); // Remove old data
						put(tupleData, Self.SELF); // Insert new data

					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private class ServerAgent extends Agent {

		private Tuple t;

		private ServerAgent(String who, Tuple t) {
			super(who);
			this.t = t;
		}

		@Override
		protected void doRun() {
			try {
				Tuple tupleData = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
				PointToPoint p = new PointToPoint(t.getElementAt(String.class, ECommand.KITCHEN.getValue()),
						vp.getAddress());
				put(t, p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}