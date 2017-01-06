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

public class User {
	protected static String userName;
	protected static Node userSpace;
	protected String kitchenName;
	protected UserAgent userAgent;

	/*
	 * public User(String name){ //TODO - Lav user uden navn }
	 */

	public User(String userName, String kitchenName) {

		this.userName = userName;
		userSpace = new Node("UserSpace" + userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		userAgent = new UserAgent(userName);
		userSpace.addAgent(userAgent);
		userSpace.start();
	}

	public static class UserAgent extends Agent {

		protected static PointToPoint p;

		public UserAgent(String UserName) {
			super(UserName);

		}

		@Override
		protected void doRun() {

		}
	}
	
	public static class AddDayAgent extends Agent {

		protected static PointToPoint p;
		protected int day,mounth,year;

		public AddDayAgent(int day, int mounth, int year) {
			super(""+day+mounth+year);
			p = new PointToPoint("Server Monitor", Server.vp.getAddress());
			this.day = day;
			this.mounth = mounth;
			this.year = year;
			

		}

		@Override
		protected void doRun() {
			Tuple newDay = new Tuple("New Day", day, mounth, year, User.userName);
			Tuple t = new Tuple("Server", newDay);
			Template template = new Template(new ActualTemplateField(User.userName), new FormalTemplateField(Tuple.class));
			
			try {
				put(t,p);
				get(template,p);
				
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
			

		}
	}
	
	public void AddDay(int day, int mounth, int year){
		AddDayAgent addDay = new AddDayAgent(day, mounth, year);
		userSpace.addAgent(addDay);
	}
	

}
