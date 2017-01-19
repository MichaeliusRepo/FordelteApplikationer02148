package classes;

/* 02148 Introduction to Coordination in Distributed Applications
*  19. Januar 2017
*  Team 9 - Dinner Club
*	- Alexander Kristian Armstrong, s154302
*	- Michael Atchapero,  s143049
*	- Mathias Ennegaard Asmussen, s154219
*	- Emilie Isabella Dahl, s153762
*	- Jon Ravn Nielsen, s136448
*/

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

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;
		kitchenSpace = new Node(kitchenName, new TupleSpace());
		kitchenSpace.addPort(Server.vp);
		Agent monitor = new KitchenMonitor("kitchenMonitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();
	}

	public String getKitchenName() {
		return kitchenName;
	}
	
	//Class used for continuously fetching and assigning new agents to execute the command tuples
	private class KitchenMonitor extends Agent {

		private Tuple t;
		//Fetching command tuples <command, username, kitchenname, feedback, datatuple>
		private Template kitchenTemplate = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private KitchenMonitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {
			try {
				//Creation of the kitchen budget
				put(new Tuple("Budget" + kitchenName, new Budget(kitchenName)), Self.SELF);
				while (true) {
					t = get(kitchenTemplate, Self.SELF);
					//Creation of new agent to execute command to support multiple users, by multithreading
					exec(new KitchenAgent(t));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	//Agent class used for executing command in the kitchen class
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
			//Used to interpretate the command of a command tuple
			switch (cmd) {

			case "addDay":
			case "removeDay":
				setDay(cmd, data);
				break;

			case "getDays":
				getDays(cmd, data);
				break;

			case "attendDay":
			case "unattendDay":
			case "addChef":
			case "getChef":
			case "lockDay":
			case "getAttendees":
			case "getPrice":
			case "getShopper":
			case "getPricePer":
				executeDayCmd(cmd, data);
				break;

			case "setPrice":
				setPrice(data);
				break;

			case "addBalance":
				addBalance(data);
				break;

			case "getBalance":
				getBalance(data);
				break;
				
			case "addUserBalance":
				addUserBalance(data);
				break;
				
			case "resetUserBalance":
				resetUserBalance(data);
				break;
			}
		}

		private void getDays(String cmd, Tuple data) {
			LinkedList<Tuple> query = queryAll(
					new Template(new FormalTemplateField(String.class), new FormalTemplateField(Day.class)));
			LinkedList<String> list = new LinkedList<String>();
			for (Tuple t : query)
				list.add(t.getElementAt(Day.class, 1).getDate());
			sendFeedback(cmd + "Feedback", new Tuple(true, "Here they are!", list));
		}
		
		private void setDay(String cmd, Tuple data) {
			
			// This method is used to create and remove days.
			
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

		//Used for sending and receiving command/feedback tuples to day tuplespaces
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

		//Used for putting a command tuple including a price into a day's tuplespace and then receiving the feedback
		//and then sending a command tuple to the budget's tuplespace to update the balance
		private void setPrice(Tuple data) {
			double price = (double) data.getElementAt(Integer.class, 0);
			System.out.println(price);
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

		//Used by set price
		private void addBalance(Tuple data) {
			try {
				put(new Tuple("addBalance", userName, kitchenName, false, data), budgetPointer);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//Sends a command tuple to the budget and then recevies a feedback tuple
		//which contains the current balance of a user
		private void getBalance(Tuple data) {
			try {
				put(new Tuple("getBalance", userName, kitchenName, false, data), budgetPointer);
				sendFeedback("getBalanceFeedback", recieveFeedback("Budget" + kitchenName, "getBalanceFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		//Sends a command tuple to the budget and then recevies a feedback tuple
		private void addUserBalance(Tuple data) {
			try {
				put(new Tuple("addUserBalance", userName, kitchenName, false, data), budgetPointer);
				sendFeedback("addUserBalanceFeedback", recieveFeedback("Budget" + kitchenName, "addUserBalanceFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//Sends a command tuple to the budget and then recevies a feedback tuple
		private void resetUserBalance(Tuple data) {
			try {
				put(new Tuple("resetUserBalance", userName, kitchenName, false, data), budgetPointer);
				sendFeedback("resetUserBalanceFeedback", recieveFeedback("Budget" + kitchenName, "resetUserBalanceFeedback"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//Used to retrieve feedback tuples
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

		//Used to send feedback tupels to the server
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

		//Used by the recieveFeedback method to create a feedback template
		//<command, username, kitchenname, feedback, datatuple>
		private Template feedbackTemplate(String command) {
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
