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

	public void command(String command, int day, int month, int year) {
		this.command = command;
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
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
