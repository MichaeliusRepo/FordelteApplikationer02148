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
		//budget = new Budget(kitchenName);
		kitchenSpace.addPort(Server.vp);
		Agent monitor = new KitchenMonitor("kitchenMonitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();
	}

	public class KitchenMonitor extends Agent {

		Tuple t;
		// <String command, String user, String kitchenName, Boolean feedback, Tuple data>
		Template kitchenTemplate = new Template(new FormalTemplateField(String.class),new FormalTemplateField(String.class),new FormalTemplateField(String.class),new ActualTemplateField(false),
				new FormalTemplateField(Tuple.class));

		public KitchenMonitor(String who) {
			super(who);
			
		}

		@Override
		protected void doRun() {

			
				try {
					try{
						put(new Tuple("Budget"+kitchenName, new Budget(kitchenName)),Self.SELF);
					}catch(Exception e){
						e.printStackTrace();
					}
					while(true){
					t = get(kitchenTemplate, Self.SELF);
					Tuple data = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
					String cmd = t.getElementAt(String.class, ECommand.COMMAND.getValue());
					exec(new KitchenAgent(cmd, data));
					}
				} catch (Exception e) {
					e.printStackTrace();
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
			int day = data.getElementAt(Integer.class, EDayData.DAY.getValue());
			int month = data.getElementAt(Integer.class, EDayData.MONTH.getValue());
			int year = data.getElementAt(Integer.class, EDayData.YEAR.getValue());
			String target = "" + day + month + year;

			try {

				if (!checkDayExists(target)) {

					put(new Tuple(target, new Day(day, month, year)), Self.SELF);
					pointer = new PointToPoint(target, Server.vp.getAddress());
					get(feedbackTemplate("addDay"), pointer);

					sendFeedback("addDay", new Tuple(user, kitchenName, true, "Day created."));

				} else
					sendFeedback("addDay", new Tuple(user, kitchenName, false, "Day already exists"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void removeDay(Tuple data) {
			String target = "" + data.getElementAt(Integer.class, 2) + data.getElementAt(Integer.class, 3)
					+ data.getElementAt(Integer.class, 4);

			try {
				if (!checkDayExists(target)) {
					sendFeedback("removeDay", new Tuple(user, kitchenName, false, "The chosen day doesn't exist."));
				} else {
					get(new Template(new ActualTemplateField(target), new FormalTemplateField(Day.class)), Self.SELF);
					sendFeedback("removeDay", new Tuple(user, kitchenName, true, "Day was deleted."));
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
					sendFeedback("attendDay", new Tuple(user, kitchenName, false, "The chosen day doesn't exist."));
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
					sendFeedback("unattendDay", new Tuple(user, kitchenName, false, "Day doesn't exist"));
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
					sendFeedback("getAttendees", new Tuple(user, kitchenName, false, "Day doesn't exist"));
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
					sendFeedback("addChef", new Tuple(user, kitchenName, false, "Day doesn't exist"));
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
					System.out.println("Before addBalance");
					//Template testtemp = new Template(new FormalTemplateField(Tuple.class), new ActualTemplateField("addBalance"), new ActualTemplateField("test"));
					Tuple testt = recieveFeedback(target, "addBalance");
					addBalance(testt);
					System.out.println("addBalance");
				} else {
					sendFeedback("setPrice", new Tuple(user, kitchenName, false, "Day doesn't exist"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addBalance(Tuple data) {

			try {
				put(new Tuple("addBalance", data), budgetPointer);
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
					sendFeedback("lockDay", new Tuple(user, kitchenName, false, "Day doesn't exist"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Tuple recieveFeedback(String target, String feedbackCmd) {
			try {
				pointer = new PointToPoint(target, Server.vp.getAddress());				
				Tuple feedbackTuple = get(feedbackTemplate(feedbackCmd), pointer);
				return feedbackTuple.getElementAt(Tuple.class, ECommand.DATA.getValue());
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
		
		private Template feedbackTemplate(String command){
			// <String command, String user, String kitchenName, Boolean feedback, Tuple data>
			Template feedbackTemplate = new Template(new ActualTemplateField(command),
					new FormalTemplateField(String.class), new FormalTemplateField(String.class),
					new ActualTemplateField(true), new FormalTemplateField(Tuple.class));
			return feedbackTemplate;
		}
		
		// Use for debugging :-)))))
		private boolean queryEmpty(Template t) {
			LinkedList<Tuple> getAll = queryAll(t);
			return (getAll.isEmpty()) ? true : false;
		}

	}
}
