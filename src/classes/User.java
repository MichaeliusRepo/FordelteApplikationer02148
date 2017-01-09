package classes;

import java.io.IOException;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;
import org.cmg.resp.topology.VirtualPort;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;

@SuppressWarnings("unused")
public class User {
	protected String userName;
	protected Node userSpace;
	protected String kitchenName;
	protected PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	protected String command;

	public User(String userName, String kitchenName) {
		this.userName = userName;
		this.kitchenName = kitchenName;
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}

	public void addDay(int day, int month, int year) {
		command = "addDay";
		dayFormat(day, month, year);
	}

	public void addChef(int day, int month, int year) {
		command = "addChef";
		dayFormat(day, month, year);
	}

	public void attendDay(int day, int month, int year) {
		command = "attendDay";
		dayFormat(day, month, year);
	}

	public void unattendDay(int day, int month, int year) {
		command = "unattendDay";
		dayFormat(day, month, year);
	}

	public void lockDay(int day, int month, int year) {
		command = "lockDay";
		dayFormat(day, month, year);
	}

	public void getCook(int day, int month, int year) {
		command = "getCook";
		dayFormat(day, month, year);
	}

	public void setPrice(int day, int month, int year) {
		command = "setPrice";
		dayFormat(day, month, year);
	}

	public void getPrice(int day, int month, int year) {
		command = "getPrice";
		dayFormat(day, month, year);
	}

	public void getAttendees(int day, int month, int year) {
		command = "getAttendees";
		dayFormat(day, month, year);
	}

	public void dayFormat(int day, int month, int year) {
		Tuple t = new Tuple(command, new Tuple(userName, kitchenName, day, month, year));
		Agent addDay = new UserAgent(command, t);
		userSpace.addAgent(addDay);
	}

	public class UserAgent extends Agent {

		Tuple t;

		public UserAgent(String who, Tuple t) {
			super(who);
			this.t = t;
		}

		@Override
		protected void doRun() {
			try {

				switch (command) {
				case "addDay":
				case "addChef":
				case "attendDay":
				case "unattendDay":
				case "lockDay": 
				case "getCook":
				case "setPrice":
				case "getPrice":
				case "getAttendees":
				
				{
					Tuple dataTuple = t.getElementAt(Tuple.class, 1);
					Template feedback = new Template(new ActualTemplateField(userName + " " + command + " Feedback"),
							new FormalTemplateField(Tuple.class));

					try {
						put(t, p); // AddDay sent to server

						t = get(feedback, Self.SELF);

						dataTuple = t.getElementAt(Tuple.class, 1);

						System.out.println(dataTuple.getElementAt(String.class, 2));
						System.out.println(userName + " got SOME feedback.");
						System.out.println("VICTORY \\o/");
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				}

				case "doSomethingElse":
					// do something else
					break;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
