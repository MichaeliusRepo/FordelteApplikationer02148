package classes;

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
	private String kitchenName;
	private Node kitchenSpace;
	// private Budget budget;

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;

		kitchenSpace = new Node(kitchenName, new TupleSpace());
		// budget = new Budget(kitchenName);
		kitchenSpace.addPort(Server.vp);
		Agent monitor = new KitchenMonitor("kitchenMonitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();
	}

	public String getKitchenName() {
		return kitchenName;
	}

	private class KitchenMonitor extends Agent {

		private Tuple t;
		// <String command, String user, String kitchenName, Boolean feedback,
		// Tuple data>
		private Template kitchenTemplate = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private KitchenMonitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			try {
				put(new Tuple("Budget" + kitchenName, new Budget(kitchenName)), Self.SELF);
				while (true) {
					t = get(kitchenTemplate, Self.SELF);
					exec(new KitchenAgent(t));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	private class KitchenAgent extends Agent {

		private PointToPoint pointer, serverPointer, budgetPointer;
		private Tuple data;
		private String userName, cmd, dayTarget;
		private int day, month, year;

		private KitchenAgent(Tuple t) {
			super(t.getElementAt(String.class, ECommand.COMMAND.getValue()));
			this.cmd = t.getElementAt(String.class, ECommand.COMMAND.getValue());
			this.userName = t.getElementAt(String.class, ECommand.USERNAME.getValue());

			Tuple data = t.getElementAt(Tuple.class, ECommand.DATA.getValue());

			this.day = data.getElementAt(Integer.class, EDayData.DAY.getValue());
			this.month = data.getElementAt(Integer.class, EDayData.MONTH.getValue());
			this.year = data.getElementAt(Integer.class, EDayData.YEAR.getValue());

			dayTarget = "" + day + month + year;
			this.data = new Tuple(data.getElementAt(Integer.class, EDayData.EXTRA.getValue()));

			serverPointer = new PointToPoint("Server", Server.vp.getAddress());
			budgetPointer = new PointToPoint("Budget" + kitchenName, Server.vp.getAddress());
			pointer = new PointToPoint(dayTarget, Server.vp.getAddress());

		}

		@Override
		protected void doRun() {

			switch (cmd) {

			case "addDay":
				setDay(cmd, data);
				System.out.println(cmd);
				break;

			case "removeDay":
				System.out.println(cmd);
				setDay(cmd, data);
				break;

			case "attendDay":
				System.out.println(cmd);
				executeDayCmd(cmd, data);
				// attendDay(data);
				break;

			case "unattendDay":
				System.out.println(cmd);
				executeDayCmd(cmd, data);
				// unattendDay(data);
				break;

			case "addChef":
				System.out.println(cmd);
				executeDayCmd(cmd, data);
				// addChef(data);
				break;
			
			case "getChef":
				executeDayCmd(cmd, data);
				break;

			case "lockDay":
				System.out.println(cmd);
				executeDayCmd(cmd, data);
				// lockDay(data);
				break;

			case "getAttendees":
				System.out.println(cmd);
				executeDayCmd(cmd, data);
				// getAttendees(data);
				break;

			case "setPrice":
				System.out.println(cmd);
				setPrice(data);
				break;

			case "addBalance":
				System.out.println(cmd);
				addBalance(data);
				break;

			case "getBalance":
				System.out.println(cmd);
				getBalance(data);
				break;
			case "resetUserBalance":
				System.out.println(cmd);
				resetUserBalance(data);
				break;

			}
		}

		private void setDay(String cmd, Tuple data) {
			boolean dayExists = checkDayExists(dayTarget);
			try {
				if (cmd.equals("addDay")) {
					if (dayExists) {
						sendFeedback(cmd + "Feedback", new Tuple(false, "Day already exists"));
					} else {
						put(new Tuple(dayTarget, new Day(day, month, year)), Self.SELF);
						get(new Template(new FormalTemplateField(String.class)), pointer);
						sendFeedback(cmd + "Feedback", new Tuple(true, "Day created."));
					}
				} else if (cmd.equals("removeDay")) {
					if (dayExists) {
						Template dayTemp = new Template(new ActualTemplateField(dayTarget),
								new FormalTemplateField(Day.class));
						get(dayTemp, Self.SELF);
						sendFeedback(cmd + "Feedback", new Tuple(true, "Day was deleted."));
					} else {
						sendFeedback(cmd + "Feedback", new Tuple(false, "The chosen day doesn't exist."));
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void executeDayCmd(String cmd, Tuple data) {

			try {
				if (checkDayExists(dayTarget)) {
					Tuple t = new Tuple(cmd, userName, kitchenName, false, data);
					put(t, pointer);
					sendFeedback(cmd + "Feedback", recieveFeedback(dayTarget, cmd + "Feedback"));
				} else {
					sendFeedback(cmd + "Feedback", new Tuple(false, "The chosen day doesn't exist."));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setPrice(Tuple data) {
			int price = data.getElementAt(Integer.class, 0);

			try {
				if (checkDayExists(dayTarget)) {
					put(new Tuple("setPrice", userName, kitchenName, false, new Tuple(price)), pointer);
					sendFeedback("setPriceFeedback", recieveFeedback(dayTarget, "setPriceFeedback"));
					System.out.println("Before addBalance");
					Tuple test = recieveFeedback(dayTarget, "addBalance");
					addBalance(test);
					System.out.println("addBalance");
				} else {
					sendFeedback("setPriceFeedback", new Tuple(false, "Day doesn't exist"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addBalance(Tuple data) {

			try {
				put(new Tuple("addBalance", userName, kitchenName, false,
						data.getElementAt(Tuple.class, ECommand.DATA.getValue())), budgetPointer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getBalance(Tuple data) {

			try {
				put(new Tuple("getBalance", userName, kitchenName, false, data), budgetPointer);
				sendFeedback("getBalanceFeedback", recieveFeedback("Budget" + kitchenName, "getBalanceFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void resetUserBalance(Tuple data) {
			try {
				put(new Tuple("resetUserBalance", data), budgetPointer);
				sendFeedback("resetUserBalanceFeedback", recieveFeedback("Budget" + kitchenName, "resetUserFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private Tuple recieveFeedback(String target, String feedbackCmd) {
			try {
				PointToPoint p = new PointToPoint(target, Server.vp.getAddress());
				Tuple feedbackTuple = get(feedbackTemplate(feedbackCmd), p);
				return feedbackTuple.getElementAt(Tuple.class, ECommand.DATA.getValue());
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void sendFeedback(String cmd, Tuple result) {
			try {
				put(new Tuple(cmd, userName, kitchenName, true, result), serverPointer);
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

		private Template feedbackTemplate(String command) {
			// <String command, String user, String kitchenName, Boolean
			// feedback,
			// Tuple data>
			Template feedbackTemplate = new Template(new ActualTemplateField(command),
					new ActualTemplateField(userName), new ActualTemplateField(kitchenName),
					new ActualTemplateField(true), new FormalTemplateField(Tuple.class));
			return feedbackTemplate;
		}

		@SuppressWarnings("unused") // Use for debugging
		private boolean queryEmpty(Template t) {
			LinkedList<Tuple> getAll = queryAll(t);
			return (getAll.isEmpty()) ? true : false;
		}

	}
}
