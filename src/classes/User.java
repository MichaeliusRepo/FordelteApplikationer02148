package classes;

// 02148 Introduction to Coordination in Distributed Applications
// 19. Januar 2017
// Team 9 - Dinner Club
//	- Alexander Kristian Armstrong, s154302
//	- Michael Atchapero,  s143049
//	- Mathias Ennegaard Asmussen, s154219
//	- Emilie Isabella Dahl, s153762
//	- Jon Ravn Nielsen, s136448

import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;

public class User {
	private String userName = "";
	private Node userSpace;
	private PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	private String command;
	private String feedbackMsg = null;

	private ArrayList<String> kitchens = new ArrayList<String>();
	private LinkedList<String> returnData;

	// Constructor
	public User() {
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}

	// Returning all days created in a specific kitchen/dinner club
	public LinkedList<String> getDays(String kitchenName) throws Exception {
		feedbackMsg = null;
		command("getDays", kitchenName, 0, 0, 0, 0);
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Waiting for Server to return proper feedback.
		return returnData;
	}

	// Returning all attendees in a specific kitchen/dinner club on a specific
	// day
	public LinkedList<String> getAttendees(String kitchenName, int day, int month, int year)
			throws InterruptedException {
		feedbackMsg = null;
		command("getAttendees", kitchenName, day, month, year, 0);
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		return returnData;
	}

	// The primary method called in GUI and main class
	public void command(String command, String kitchenName, int day, int month, int year, int extra)
			throws InterruptedException {
		// Setting the feedback to null, so in ends when the feedback is
		// returned.
		setFeedbackMsg(null);
		this.command = command;
		Tuple t = new Tuple(command, userName, kitchenName, false, new Tuple(day, month, year, extra));
		userSpace.addAgent(new UserAgent(command, t));
		// Waiting for new feedback message
		while (getFeedbackMsg() == null) {
			Thread.sleep(10);
		}
	}

	public boolean addUser(String userName) throws Exception {
		// Setting the feedback to null, so in ends when the feedback is
		// returned.
		feedbackMsg = null;
		Tuple t = new Tuple("addUser", userName, "", false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));

		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		return (feedbackMsg.equals(userName + " was retrieved."));
	}

	public boolean getUser(String userName) throws Exception {
		// Setting the feedback to null, so in ends when the feedback is
		// returned.
		feedbackMsg = null;
		Tuple t = new Tuple("getUser", userName, "", false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.
		return (feedbackMsg.equals(userName + " was retrieved."));
	}

	public boolean createKitchen(String kitchenName) throws Exception {
		// Setting the feedback to null, so in ends when the feedback is
		// returned.
		feedbackMsg = null;
		if (kitchens.size() == 4) {
			feedbackMsg = "You may only be a member of up to 4 kitchens.";
			System.out.println(userName + " got feedback: " + feedbackMsg);
			return false;
		}

		Tuple t = new Tuple("createKitchen", userName, kitchenName, false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));

		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.

		boolean result = feedbackMsg.equals(kitchenName + " was created.");
		if (result)
			updateUser(); // WARNING: Method doesn't wait for this to finish!
		return result;
	}

	public boolean joinKitchen(String kitchenName) throws Exception {
		// Setting the feedback to null, so in ends when the feedback is
		// returned.
		feedbackMsg = null;
		if (kitchens.size() == 4) {
			feedbackMsg = "You may only be a member of up to 4 kitchens.";
			System.out.println(userName + " got feedback: " + feedbackMsg);
			return false;
		}

		Tuple t = new Tuple("joinKitchen", userName, kitchenName, false, new Tuple());
		userSpace.addAgent(new UserAgent(command, t));
		while (feedbackMsg == null) {
			Thread.sleep(10);
		} // Wait for Server to return proper feedback.

		boolean result = feedbackMsg.equals("You joined " + kitchenName);
		if (result)
			updateUser(); // WARNING: Method doesn't wait for this to finish!
		return result;

	}

	// Resetting the user information
	private void updateUser() throws Exception {
		ArrayList<String> l = new ArrayList<String>(kitchens);
		while (l.size() != 4)
			l.add("");

		Tuple data = new Tuple(userName, new Tuple(l.get(0), l.get(1), l.get(2), l.get(3)));
		Tuple t = new Tuple("updateUser", userName, "", false, data);
		userSpace.addAgent(new UserAgent(command, t));

	}

	// The agent communicating with the other classes (sending and receiving
	// information)
	private class UserAgent extends Agent {
		private Tuple t;

		private UserAgent(String who, Tuple t) {
			super(who);
			// The tuple which should be sent to the server
			this.t = t;
		}

		@Override
		protected void doRun() {
			try {
				String cmd = t.getElementAt(String.class, ECommand.COMMAND.getValue());
				String kitchenName = t.getElementAt(String.class, ECommand.KITCHEN.getValue());
				boolean exists;
				Tuple dataTuple;

				// Standard command tuple template: < String, username, String,
				// true, Tuple>
				Template feedback = new Template(new FormalTemplateField(String.class),
						new ActualTemplateField(t.getElementAt(String.class, ECommand.USERNAME.getValue())),
						new FormalTemplateField(String.class), new ActualTemplateField(true),
						new FormalTemplateField(Tuple.class));

				switch (cmd) {
				default:
					// the command method called
					put(t, p);
					// waiting for feedback
					t = get(feedback, p);
					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

					// checking is lists if getDays or getAttendees are called
					if (t.getElementAt(String.class, ECommand.COMMAND.getValue()).contains("getDays")
							|| t.getElementAt(String.class, ECommand.COMMAND.getValue()).contains("getAttendees")) {
						@SuppressWarnings("unchecked")
						LinkedList<String> list = dataTuple.getElementAt(LinkedList.class, 2);
						returnData = list;
						for (String str : returnData)
							System.out.print(str + ", ");
						System.out.println();
					}

					feedbackMsg = dataTuple.getElementAt(String.class, 1);
					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "addUser":
					put(t, p); // put server request
					t = get(feedback, p); // get feedback

					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					exists = dataTuple.getElementAt(Boolean.class, 0);

					kitchens.clear(); // Always clear for a new user!

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
						t = dataTuple.getElementAt(Tuple.class, 1).getElementAt(Tuple.class, 1);
						for (int i = 0; i < 4; i++) {
							if (t.getElementAt(String.class, i) == "")
								break;
							kitchens.add(t.getElementAt(String.class, i));
						}

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

					if (!exists) { // if doesn't exist, create
						kitchens.add(kitchenName);
						feedbackMsg = kitchenName + " was created.";
					} else
						feedbackMsg = kitchenName + " already exists.";

					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "joinKitchen":
					put(t, p); // put server request

					t = get(feedback, p); // get feedback
					dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					exists = dataTuple.getElementAt(Boolean.class, 0);

					if (exists)
						if (kitchens.contains(kitchenName))
							feedbackMsg = "You are already a member.";
						else {
							kitchens.add(kitchenName);
							feedbackMsg = "You joined " + kitchenName;
						}
					else
						feedbackMsg = kitchenName + " does not exist.";

					System.out.println(userName + " got feedback: " + feedbackMsg);
					break;

				case "updateUser":
					put(t, p);

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

	public void setFeedbackMsg(String feedback) {
		feedbackMsg = feedback;
	}

	public String getUserName() {
		return userName;
	}

	// returning kitchens one at the time
	public String getKitchenName(int i) {
		if (i < kitchens.size()) {
			return kitchens.get(i);
		}
		return "";
	}

	public LinkedList<String> getReturnData() {
		return returnData;
	}

	public void setReturnData(LinkedList<String> list) {
		this.returnData = list;
	}

}
