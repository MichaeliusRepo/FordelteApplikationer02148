package classes;

import java.io.IOException;

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
	protected String userName;
	protected Node userSpace;
	protected String kitchenName;
	protected PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());
	protected String command;
	private int extra;
	private String feedbackMsg;

	public User(String userName, String kitchenName) {
		this.userName = userName;
		this.kitchenName = kitchenName;
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userSpace.start();
	}

	public void command(String command, int day, int month, int year, int extra) {
		this.command = command;
		this.extra = extra;
		dayFormat(day, month, year);
			
		
	}
/*
	public void command(String command, String targetUser){
		this.command = command;
		Tuple t = new Tuple(command, new Tuple(userName, kitchenName, targetUser));
		Agent userAgent = new UserAgent(command, t);
		userSpace.addAgent(userAgent);
	}
	
	public void command(String command){
		this.command = command;
		Tuple t = new Tuple(command, new Tuple(userName, kitchenName));
		Agent userAgent = new UserAgent(command, t);
		userSpace.addAgent(userAgent);
	}
	*/
	public void dayFormat(int day, int month, int year) {
		Tuple t = new Tuple(command, new Tuple(userName, kitchenName, day, month, year, extra));
		Agent userAgent = new UserAgent(command, t);
		userSpace.addAgent(userAgent);
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
						feedbackMsg = dataTuple.getElementAt(String.class, 3);
						System.out.println(feedbackMsg);
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
