package classes;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;
import org.cmg.jresp.topology.VirtualPort;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;

@SuppressWarnings("unused")

public class User {
	private String userName = "";
	private Node userSpace;
	private String kitchenName = "";
	private PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	private String command;
	private String feedbackMsg = null;
	private final ArrayList<String> kitchens = new ArrayList<String>();
	private LinkedList<String> returnData;

	public User() {
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}
	
	public User(String userName) {
		this.userName = userName;
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
		getUser(userName);
	}

	public LinkedList<String> getDays() {
		return returnData;
	}

	public void command(String command, int day, int month, int year, int extra) {
		this.command = command;
		Tuple t = new Tuple(command, userName, kitchenName, false, new Tuple(day, month, year, extra));
		userSpace.addAgent(new UserAgent(command, t));
	}

	public boolean getUser(String userName) {
		this.userName = null;
		Tuple t = new Tuple("getUser", userName, "", false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		return (userName != null || userName != "") ? true : false;
	}
	
	public boolean addUser(String userName) {
		this.userName = null;
		Tuple t = new Tuple("addUser", userName, "", false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		return (userName != null || userName != "") ? true : false;
	}

	private class UserAgent extends Agent {

		private Tuple t;

		private UserAgent(String who, Tuple t) {
			super(who);
			this.t = t;
		}

		@Override
		protected void doRun() {
			try {

				String cmd = t.getElementAt(String.class, ECommand.COMMAND.getValue());
				Template feedback = new Template(new FormalTemplateField(String.class),
						new ActualTemplateField(userName), new FormalTemplateField(String.class),
						new ActualTemplateField(true), new FormalTemplateField(Tuple.class));
				Template user = new Template(
						new ActualTemplateField(t.getElementAt(String.class, ECommand.USERNAME.getValue())),
						new ActualTemplateField(t.getElementAt(String.class, ECommand.KITCHEN.getValue())));

				/*
				 * Perhaps we should implement switch cases here, and create
				 * methods for each case, instead of an if statement?
				 */

				if (cmd.contains("User")) {
					// User request
					if (cmd.equals("addUser"))
						put(t, p);
					t = query(user, p);
					if (t != null) {
						userName = t.getElementAt(String.class, 0);
						feedbackMsg = userName + " belonging to " + kitchenName + " was retrieved.";
						System.out.println(feedbackMsg);
					} else 
						userName = null;
					
				} else {
					// Command
					put(t, p);
					t = get(feedback, p);
					Tuple dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

					feedbackMsg = dataTuple.getElementAt(String.class, 1);
					System.out.println(userName + " got feedback: " + feedbackMsg);

					if (t.getElementAt(String.class, ECommand.COMMAND.getValue()).equals("getDays")
							|| t.getElementAt(String.class, ECommand.COMMAND.getValue()).equals("getAttendees")) {
						@SuppressWarnings("unchecked")
						LinkedList<String> list = dataTuple.getElementAt(LinkedList.class, 2);
						returnData = list;
						for (String str : returnData)
							System.out.print(str + ", ");
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public String getFeedbackMsg() {
		return feedbackMsg;
	}

	public String getUserName() {
		return userName;
	}

	public String getKitchenName(int i) {
		if (kitchens.size() > i)
			return kitchens.get(i);
		return null;
	}

	// TODO
	public boolean addKitchen(String newKitchenName) {
		kitchens.add(newKitchenName);
		return true;
	}

	// TODO
	public boolean setKitchen(String selectedKitchenName) {
		this.kitchenName = selectedKitchenName;
		return true;
	}

}
