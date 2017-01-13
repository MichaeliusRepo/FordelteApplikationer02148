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
	
	public Tuple accessUser(String userName, String kitchenName, String cmd) {
		server.addAgent(new UserManagingAgent(userName, kitchenName, cmd));
		while (userTuple == null) {
		}
		Tuple t = userTuple;
		userTuple = null;
		return t;
	}

	private class Monitor extends Agent {

		private Tuple t;
		private Tuple tupleData;
		private Template what = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			while (true) {
				try {
					t = get(what, Self.SELF);
					String command = t.getElementAt(String.class, ECommand.COMMAND.getValue());
					tupleData = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

					if (!command.contains("Feedback")) {
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
						Agent agent = new ServerAgent(command, t);
						exec(agent);
					} else {
						// This section has been moved to user.
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

	private class UserManagingAgent extends Agent {

		String userName, kitchenName, cmd;

		private UserManagingAgent(String userName, String kitchenName, String cmd) {
			super(userName);
			this.userName = userName;
			this.kitchenName = kitchenName;
			this.cmd = cmd;
		}

		@Override
		protected void doRun() {
			try {
				if (cmd.contains("add"))
					put(new Tuple(userName, kitchenName), Self.SELF);
				userTuple = queryp(
						new Template(new ActualTemplateField(userName), new ActualTemplateField(kitchenName)));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}