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
	private String userName;
	private Node userSpace;
	private String kitchenName;
	private PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	private String command;
	private String feedbackMsg;
	private final ArrayList<String> kitchens = new ArrayList<String>();
	private LinkedList<String> returnData;

	public User(String userName, String kitchenName) {
		this.userName = userName;
		this.kitchenName = kitchenName;
		kitchens.add(kitchenName);
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}

	public LinkedList<String> getDays() {
		return returnData;
	}

	public void command(String command, int day, int month, int year, int extra) {
		this.command = command;
		Tuple t = new Tuple(command, userName, kitchenName, false, new Tuple(day, month, year, extra));
		Agent userAgent = new UserAgent(command, t);
		userSpace.addAgent(userAgent);
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
				Template feedback = new Template(new FormalTemplateField(String.class),
						new FormalTemplateField(String.class), new FormalTemplateField(String.class),
						new ActualTemplateField(true), new FormalTemplateField(Tuple.class));

				put(t, p); // AddDay sent to server
				
				t = get(feedback, p);
				Tuple dataTuple = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
				
				feedbackMsg = dataTuple.getElementAt(String.class, 1);
				System.out.println(userName + " got feedback: " + feedbackMsg);

				if (t.getElementAt(String.class, ECommand.COMMAND.getValue()).contains("getDays")
						|| t.getElementAt(String.class, ECommand.COMMAND.getValue()).contains("getAttendees")) {
					@SuppressWarnings("unchecked")
					LinkedList<String> list = dataTuple.getElementAt(LinkedList.class, 2);
					returnData = list; // This cannot be done in one line. Thanks for nothing then, Java.
					for (String str : returnData)
						System.out.print(str + ", ");
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

	public boolean addKitchen(String newKitchenName) {
		kitchens.add(newKitchenName);
		return true;
	}

	public void setKitchen(String selectedKitchenName) {
		this.kitchenName = selectedKitchenName;
	}

	public User setInfo(String text) {
		if(text.equals("Emilie")){
			return this;
		}
		return null;
	}

}
