package classes;

import java.util.ArrayList;
import java.util.List;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;

public class Kitchen {
	protected String kitchenName;
	protected static Node kitchenSpace;
	protected List<Day> days;
	protected PointToPoint p = new PointToPoint("Server", Server.vp.getAddress());

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;
		days = new ArrayList<Day>();

		kitchenSpace = new Node(kitchenName, new TupleSpace());
		kitchenSpace.addPort(Server.vp);
		// Agent kitchenAgent = new KitchenAgent(kitchenName);
		Agent monitor = new Monitor("Monitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();

	}

	public class Monitor extends Agent {

		AddDayAgent addDay = new AddDayAgent("AddDayAgent");
		GetDaysAgent getDays = new GetDaysAgent("GetDaysAgent");
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
							addDay(t.getElementAt(Tuple.class, 1));
							break;

						case "getDays":
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

		protected void addDay(Tuple tupleData) {
			try {
				addDay.initialize(tupleData);
				exec(addDay);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected void getDays(Tuple tupleData) {
			try {
				exec(getDays);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public class AddDayAgent extends Agent {

		Tuple tupleData;

		public AddDayAgent(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			String user = tupleData.getElementAt(String.class, 0);

			int day = tupleData.getElementAt(Integer.class, 2);
			int month = tupleData.getElementAt(Integer.class, 3);
			int year = tupleData.getElementAt(Integer.class, 4);

			Template date = new Template(new ActualTemplateField(day), new ActualTemplateField(month),
					new ActualTemplateField(year));

			try {
				String feedback;

				// check if this date exists in this kitchen already
				if (null == queryp(date)) {
					// add date
					put(new Tuple(day, month, year), Self.SELF);
					days.add(new Day(day, month, year));

					feedback = "Date " + day + "/" + month + "/" + year + " added successfully to " + kitchenName;
				} else {
					feedback = "Date " + day + "/" + month + "/" + year + " already exists in " + kitchenName;
				}

				Tuple feedbackData = new Tuple(user, feedback);
				put(new Tuple("addDay Feedback", feedbackData), p);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		void initialize(Tuple t) {
			tupleData = t;
		}
	}

	// public class AddDayAgent extends Agent {
	//
	// protected PointToPoint p;
	// protected int day, month, year;
	//
	// public AddDayAgent(String who) {
	// super("AddDayAgent");
	// p = new PointToPoint("Server", Server.vp.getAddress());
	// }
	//
	// @Override
	// protected void doRun() {
	// Tuple dataTuple = new Tuple(kitchenName, day, month, year);
	// Tuple t = new Tuple("addDay", dataTuple);
	// Template feedback = new Template(new ActualTemplateField(kitchenName),
	// new FormalTemplateField(Tuple.class));
	//
	// try {
	// put(t, p);
	// System.out.println("AddDay sent to Server.");
	//
	// get(feedback, Self.SELF);
	// System.out.println(name + " got feedback that method executed
	// successfully!");
	// System.out.println("VICTORY \\o/");
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public void initialize(int day, int month, int year) {
	// this.day = day;
	// this.month = month;
	// this.year = year;
	// }
	// }

	public class GetDaysAgent extends Agent {

		public GetDaysAgent(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				put(new Tuple("getDays", days), p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
