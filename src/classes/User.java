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
	private PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	private String command;
	private String feedbackMsg = null;

	// private String[] kitchens = { null, null, null, null };
	private ArrayList<String> kitchens = new ArrayList<String>();

	private int kitchenPointer;

	private LinkedList<String> returnData;

	public User() {
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}

	public User(String userName) throws Exception {
		this.userName = userName;
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
		getUser(userName);
	}

	public LinkedList<String> getDays() {
		return returnData;
	}

	public void command(String command, String kitchenName, int day, int month, int year, int extra) {
		this.command = command;
		Tuple t = new Tuple(command, userName, kitchenName, false, new Tuple(day, month, year, extra));
		userSpace.addAgent(new UserAgent(command, t));
	}

	public boolean addUser(String userName) throws Exception {
		feedbackMsg = null;
		Tuple t = new Tuple("addUser", userName, "", false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));

		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		return (userName != null || userName != "") ? true : false;
	}

	public boolean getUser(String kitchenName) throws Exception {
		feedbackMsg = null;
		Tuple t = new Tuple("getUser", userName, kitchenName, false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		return (userName != null || userName != "") ? true : false;
	}

	public boolean createKitchen(String kitchenName) throws Exception {
		feedbackMsg = null;
		if (kitchens.size() > 4) {
			feedbackMsg = "You may only be a member of up to 4 kitchens.";
			System.out.println(userName + " got feedback: " + feedbackMsg);
			return false;
		}

		Tuple t = new Tuple("createKitchen", userName, kitchenName, false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		
		return (feedbackMsg.contains("was created")) ? true : false;
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
				Template user = new Template(
						new ActualTemplateField(t.getElementAt(String.class, ECommand.USERNAME.getValue())),
						new ActualTemplateField(t.getElementAt(String.class, ECommand.KITCHEN.getValue())));
				boolean exists;
				Tuple dataTuple;

				Template feedback = new Template(new FormalTemplateField(String.class),
						new ActualTemplateField(t.getElementAt(String.class, ECommand.USERNAME.getValue())),
						new FormalTemplateField(String.class), new ActualTemplateField(true),
						new FormalTemplateField(Tuple.class));
				/*
				 * Perhaps we should implement switch cases here, and create
				 * methods for each case, instead of an if statement?
				 */

				switch (cmd) {
				default:
					// Command
					put(t, p);
					t = get(feedback, p);
					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

//					for (int i = 0; i < 100; i++) {
//						System.out.println(dataTuple.getElementAt(i).getClass().getSimpleName());
//					}
					
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
					break;

				case "addUser":
					put(t, p); // put server request
					t = get(feedback, p); // get feedback

					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					exists = dataTuple.getElementAt(Boolean.class, 0);

					if (!exists) {
						userName = t.getElementAt(String.class, ECommand.USERNAME.getValue());
						feedbackMsg = userName + " was retrieved.";
					} else {
						userName = null;
						feedbackMsg = "Username already exists. Pick another.";
					}
					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "getUser":
					put(t, p); // put server request
					t = get(feedback, p); // get feedback

					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					exists = dataTuple.getElementAt(Boolean.class, 0);

					if (exists) { // if this exists, it was successful.
						userName = t.getElementAt(String.class, ECommand.USERNAME.getValue());
						feedbackMsg = userName + " was retrieved.";
						kitchens.clear();
						for (int i = 0; i < 4; i++) 
							kitchens.add(i, dataTuple.getElementAt(String.class, i));
					} else {
						userName = null;
						feedbackMsg = "Username not found.";
					}
					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "createKitchen":
					put(t, p); // put server request
					
					t = get(feedback, p); // get feedback
					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					exists = dataTuple.getElementAt(Boolean.class, 0);
					String kitchenName = t.getElementAt(String.class, ECommand.KITCHEN.getValue());

					if (!exists) { // if doesn't exist, create
						kitchens.add(kitchenName);
						feedbackMsg = kitchenName + " was created.";
					} else
						feedbackMsg = kitchenName + " already exists.";

					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "joinKitchen":

					break;

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

}
