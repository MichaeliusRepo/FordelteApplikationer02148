package classes;

import java.io.IOException;
import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.PointToPoint;
import org.cmg.jresp.topology.Self;

public class Kitchen {
	protected String kitchenName;
	protected Node kitchenSpace;
	protected Budget budget;

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;

		kitchenSpace = new Node(kitchenName, new TupleSpace());
		budget = new Budget(kitchenName);
		kitchenSpace.addPort(Server.vp);
		Agent monitor = new KitchenMonitor("kitchenMonitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();
	}

	public class KitchenMonitor extends Agent {

		Tuple t;
		Template kitchenTemplate = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(Tuple.class));

		public KitchenMonitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			while (true) {
				try {
					t = get(kitchenTemplate, Self.SELF);
					Tuple data = t.getElementAt(Tuple.class, 1);
					String cmd = t.getElementAt(String.class, 0);
					exec(new KitchenAgent(cmd, data));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class KitchenAgent extends Agent {
		protected PointToPoint pointer, serverPointer, budgetPointer;

		Tuple data;
		String user;
		String cmd;

		public KitchenAgent(String cmd, Tuple data) {
			super(cmd);
			this.data = data;
			this.cmd = cmd;
			this.user = data.getElementAt(String.class, 0);
			serverPointer = new PointToPoint("Server", Server.vp.getAddress());
			budgetPointer = new PointToPoint("Budget" + kitchenName, Server.vp.getAddress());
		}

		@Override
		protected void doRun() {

			switch (cmd) {

			case "addDay":
				addDay(data);
				System.out.println("addDAy");
				break;

			case "removeDay":
				System.out.println("removeDay");
				removeDay(data);
				break;
			case "attendDay":
				System.out.println("attendDay");
				attendDay(data);
				break;
			case "unattendDay":
				unattendDay(data);
				break;
			case "addChef":
				System.out.println("addChef");
				addChef(data);
				break;

			case "setPrice":
				System.out.println("setPrice");
				setPrice(data);
				break;

			case "addBalance":
				System.out.println("addBalance");
				addBalance(data);
				break;

			case "getBalance":
				System.out.println("getBalance");
				getBalance(data);
				break;
			case "resetUserBalance":
				System.out.println("resetUserBalance");
				resetUserBalance(data);
				break;
			case "lockDay":
				System.out.println("lockDay");
				lockDay(data);
				break;
			// case "changeChef":
			// System.out.println("changeChef");
			// break;
			case "getAttendees":
				getAttendees(data);
				System.out.println("getAttendees");
			}
		}

		private void addDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {

				if (!checkDayExists(target)) {

					put(new Tuple(target, new Day(day, month, year)), Self.SELF);
					pointer = new PointToPoint(target, Server.vp.getAddress());
					get(new Template(new ActualTemplateField("dayCreated")), pointer);

					sendFeedback("addDay", new Tuple(user, kitchenName, true, "Dagen blev lavet"));

				} else
					sendFeedback("addDay", new Tuple(user, kitchenName, false, "Dagen findes allerede"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void removeDay(Tuple data) {
			String target = "" + data.getElementAt(Integer.class, 2) + "" + data.getElementAt(Integer.class, 3) + ""
					+ data.getElementAt(Integer.class, 4);

			try {
				if (!checkDayExists(target)) {
					sendFeedback("removeDay", new Tuple(user, kitchenName, false, "Den valgte dag findes ikke"));
				} else {
					get(new Template(new ActualTemplateField(target), new FormalTemplateField(Day.class)), Self.SELF);
					sendFeedback("removeDay", new Tuple(user, kitchenName, true, "Dagen er blevet slettet"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void attendDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			int attendees = data.getElementAt(Integer.class, 5);
			String target = "" + day + "" + month + "" + year;

			try {
				if (!checkDayExists(target))
					sendFeedback("attendDay", new Tuple(user, kitchenName, false, "Den valgte dag findes ikke"));
				else {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("attendDay", new Tuple(user, kitchenName, attendees)), pointer);
					sendFeedback("attendDay", recieveFeedback(target, "attendDayFeedback"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void unattendDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {
				if (checkDayExists(target)) {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("unattendDay", new Tuple(user, kitchenName)), pointer);
					sendFeedback("unattendDay", recieveFeedback(target, "unattendDayFeedback"));
				} else {
					sendFeedback("unattendDay", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getAttendees(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {
				if (checkDayExists(target)) {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("getAttendees", new Tuple(user, kitchenName)), pointer);
					sendFeedback("getAttendees", recieveFeedback(target, "getAttendeesFeedback"));
				} else {
					sendFeedback("getAttendees", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void addChef(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {
				if (checkDayExists(target)) {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("addChef", new Tuple(user, kitchenName)), pointer);
					sendFeedback("addChef", recieveFeedback(target, "addChefFeedback"));
				} else {
					sendFeedback("addChef", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setPrice(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			int price = data.getElementAt(Integer.class, 5);
			String target = "" + day + "" + month + "" + year;

			try {
				if (checkDayExists(target)) {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("setPrice", new Tuple(user, kitchenName, price)), pointer);
					sendFeedback("setPrice", recieveFeedback(target, "setPriceFeedback"));
					addBalance(get(new Template(new ActualTemplateField("addBalance"), new FormalTemplateField(Tuple.class)), pointer));
				} else {
					sendFeedback("setPrice", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addBalance(Tuple data) {

			try {
				put(new Tuple("addBalance", data.getElementAt(Tuple.class, 1)), budgetPointer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getBalance(Tuple data) {

			try {
				put(new Tuple("getBalance", data), budgetPointer);
				sendFeedback("getBalance", recieveFeedback("Budget" + kitchenName, "getBalanceFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void resetUserBalance(Tuple data) {
			try {
				put(new Tuple("resetUserBalance", data), budgetPointer);
				sendFeedback("resetUserBalance", recieveFeedback("Budget" + kitchenName, "resetUserFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void lockDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {
				if (checkDayExists(target)) {
					pointer = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("lockDay", data), pointer);
					sendFeedback("lockDay", recieveFeedback(target, "lockDayFeedback"));
				} else {
					sendFeedback("lockDay", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Tuple recieveFeedback(String target, String feedbackCmd) {
			try {
				pointer = new PointToPoint(target, Server.vp.getAddress());
				Template what = new Template(new FormalTemplateField(Tuple.class),
						new ActualTemplateField(feedbackCmd));
				//System.out.println(queryEmpty(what));
				
				Tuple feedbackTuple = get(what, pointer);
				Tuple feedbackReturnTuple = feedbackTuple.getElementAt(Tuple.class, 0);
				return feedbackReturnTuple;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void sendFeedback(String cmd, Tuple result) {
			try {
				put(new Tuple(cmd + " Feedback", result), serverPointer);
				System.out.println("Kitchen sent feedback");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private boolean checkDayExists(String target) {
			Template checkDayTemplate = new Template(new ActualTemplateField(target),
					new FormalTemplateField(Day.class));
			return (queryp(checkDayTemplate) == null) ? false : true;
		}

		// Use for debugging :-)))))
		private boolean queryEmpty(Template t) {
			LinkedList<Tuple> getAll = queryAll(t);
			return (getAll.isEmpty()) ? true : false;
		}

	}
}
