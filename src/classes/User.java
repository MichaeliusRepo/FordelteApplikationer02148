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
	protected static Node userSpace;
	protected String kitchenName;
	// protected UserAgent userAgent;

	/*
	 * public User(String name){ //TODO - Lav user uden navn }
	 */

	public User(String userName, String kitchenName) {
		this.userName = userName;
		this.kitchenName = kitchenName;
		userSpace = new Node(userName, new TupleSpace());
		userSpace.addPort(Server.vp);
		// userAgent = new UserAgent(userName);
		// userSpace.addAgent(userAgent);
		Agent monitor = new Monitor("Monitor");
		userSpace.addAgent(monitor);
		userSpace.start();
	}

	public void addDay(int day, int month, int year) {
		userSpace.put(new Tuple("addDay", new Tuple(day, month, year)));
	}

	// // Adding new day to the Dinnerclub calender
	// public void addDay(int day, int mounth, int year){
	// AddDayAgent addDay = new AddDayAgent(day, mounth, year);
	// userSpace.addAgent(addDay);
	// }
	//
	// // Assigning this user to the given day
	// public void signToDay(int day, int mounth, int year, int amount){
	// SignToDayAgent signToDay = new SignToDayAgent(day, mounth, year, amount);
	// userSpace.addAgent(signToDay);
	// }
	//
	// // Assigning this user as chef to a given day
	// public void signAsChef(int day, int mounth, int year, String menu){
	// SignAsChefAgent signAsChef = new SignAsChefAgent(day, mounth, year,
	// menu);
	// userSpace.addAgent(signAsChef);
	// }

	public class Monitor extends Agent {

		AddDayAgent addDay = new AddDayAgent("AddDayAgent");
		// add remaining agents here

		Tuple t;
		Template what = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public Monitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			try {

				while (true) {

					try {

						t = get(what, Self.SELF);

						switch (t.getElementAt(String.class, 0)) {
						case "addDay":
							Tuple dataTuple = t.getElementAt(Tuple.class, 1);
							addDay(dataTuple.getElementAt(Integer.class, 0),
									dataTuple.getElementAt(Integer.class, 1),
									dataTuple.getElementAt(Integer.class, 2));

							break;

						case "CreateKitchen":
							System.out.println("This is the syntax for switch/case with break.");
							break;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		protected void addDay(int day, int month, int year) {
			try {
				
				addDay.initialize(day,month,year);
				exec(addDay);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/*
	 * public static class UserAgent extends Agent {
	 * 
	 * protected static PointToPoint p;
	 * 
	 * public UserAgent(String UserName) { super(UserName); }
	 * 
	 * @Override protected void doRun() { } }
	 */
	public class AddDayAgent extends Agent {

		protected PointToPoint p;
		protected int day, month, year;

		public AddDayAgent(String who) {
			super("AddDayAgent");
			p = new PointToPoint("Server", Server.vp.getAddress());
		}

		@Override
		protected void doRun() {
			Tuple dataTuple = new Tuple(userName, kitchenName, day, month, year);
			Tuple t = new Tuple("addDay", dataTuple);
			Template feedback = new Template(new ActualTemplateField("addDay Feedback"), new FormalTemplateField(Tuple.class));

			try {
				put(t, p); // AddDay sent to server
				
				t = get(feedback, Self.SELF);
				dataTuple = t.getElementAt(Tuple.class,1);
				System.out.println(dataTuple.getElementAt(String.class, 1));
				System.out.println(userName + " got feedback that method executed successfully!");
				System.out.println("VICTORY \\o/");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void initialize(int day, int month, int year) {
			this.day = day;
			this.month = month;
			this.year = year;
		}
	}

	// public static class SignToDayAgent extends Agent {
	//
	// protected static PointToPoint p;
	// protected int day,mounth,year, amount;
	//
	// public SignToDayAgent(int day, int mounth, int year, int amount) {
	// super("SignDay"+day+mounth+year);
	// p = new PointToPoint("Server Monitor", Server.vp.getAddress());
	// this.day = day;
	// this.mounth = mounth;
	// this.year = year;
	// this.amount = amount;
	// // necessary?
	// /*
	// try {
	// exec(this);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// */
	// }
	//
	// @Override
	// protected void doRun() {
	// Tuple newDay = new Tuple("New Day", day, mounth, year, amount,
	// User.userName);
	// Tuple t = new Tuple("Server", newDay);
	// Template template = new Template(new ActualTemplateField(User.userName),
	// new FormalTemplateField(Tuple.class));
	//
	// try {
	// put(t,p);
	// get(template,p);
	//
	// } catch (InterruptedException | IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// public static class SignAsChefAgent extends Agent {
	//
	// protected static PointToPoint p;
	// protected int day,mounth,year;
	// protected String menu;
	//
	// public SignAsChefAgent(int day, int mounth, int year, String menu) {
	// super(menu+day+mounth+year);
	// p = new PointToPoint("Server Monitor", Server.vp.getAddress());
	// this.day = day;
	// this.mounth = mounth;
	// this.year = year;
	// this.menu = menu;
	// // necessary?
	// /*
	// try {
	// exec(this);
	// } catch (InterruptedException e) {
	// e.printStackTrace();
	// }
	// */
	// }
	//
	// @Override
	// protected void doRun() {
	// Tuple chef = new Tuple("Chef", day, mounth, year, menu, User.userName);
	// Tuple t = new Tuple("Server", chef);
	// Template template = new Template(new ActualTemplateField(User.userName),
	// new FormalTemplateField(Tuple.class));
	//
	// try {
	// put(t,p);
	// get(template,p);
	//
	// } catch (InterruptedException | IOException e) {
	// e.printStackTrace();
	// }
	// }
	// }

}
