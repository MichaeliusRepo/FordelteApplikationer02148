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
	//protected UserAgent userAgent;

	/*
	 * public User(String name){ //TODO - Lav user uden navn }
	 */

	public User(String userName, String kitchenName) {

		this.userName = userName;
		userSpace = new Node("UserSpace" + userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		//userAgent = new UserAgent(userName);
		//userSpace.addAgent(userAgent);
		userSpace.start();
	}
	
	// Adding new day to the Dinnerclub calender
	public void addDay(int day, int mounth, int year){
		AddDayAgent addDay = new AddDayAgent(day, mounth, year);
		userSpace.addAgent(addDay);	
	}
	
	// Assigning this user to the given day
	public void signToDay(int day, int mounth, int year, int amount){
		SignToDayAgent signToDay = new SignToDayAgent(day, mounth, year, amount);
		userSpace.addAgent(signToDay);
	}
	
	// Assigning this user as chef to a given day
	public void signAsChef(int day, int mounth, int year, String menu){
		SignAsChefAgent signAsChef = new SignAsChefAgent(day, mounth, year, menu);
		userSpace.addAgent(signAsChef);
	}
	
/*
	public static class UserAgent extends Agent {

		protected static PointToPoint p;
		
		public UserAgent(String UserName) {
			super(UserName);
		}
		
		@Override
		protected void doRun() {
		}
	}
*/	
	public static class AddDayAgent extends Agent {

		protected static PointToPoint p;
		protected int day,mounth,year;

		public AddDayAgent(int day, int mounth, int year) {
			super("NewDay"+day+mounth+year);
			p = new PointToPoint("Server Monitor", Server.vp.getAddress());
			this.day = day;
			this.mounth = mounth;
			this.year = year;
			// necessary?
			/*
			try {
				exec(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
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
	
	public static class SignToDayAgent extends Agent {

		protected static PointToPoint p;
		protected int day,mounth,year, amount;

		public SignToDayAgent(int day, int mounth, int year, int amount) {
			super("SignDay"+day+mounth+year);
			p = new PointToPoint("Server Monitor", Server.vp.getAddress());
			this.day = day;
			this.mounth = mounth;
			this.year = year;
			this.amount = amount;
			// necessary?
			/*
			try {
				exec(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}

		@Override
		protected void doRun() {
			Tuple newDay = new Tuple("New Day", day, mounth, year, amount, User.userName);
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
	
	public static class SignAsChefAgent extends Agent {

		protected static PointToPoint p;
		protected int day,mounth,year;
		protected String menu;

		public SignAsChefAgent(int day, int mounth, int year, String menu) {
			super(menu+day+mounth+year);
			p = new PointToPoint("Server Monitor", Server.vp.getAddress());
			this.day = day;
			this.mounth = mounth;
			this.year = year;
			this.menu = menu;
			// necessary?
			/*
			try {
				exec(this);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			*/
		}

		@Override
		protected void doRun() {
			Tuple chef = new Tuple("Chef", day, mounth, year, menu, User.userName);
			Tuple t = new Tuple("Server", chef);
			Template template = new Template(new ActualTemplateField(User.userName), new FormalTemplateField(Tuple.class));
			
			try {
				put(t,p);
				get(template,p);
				
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	


}
