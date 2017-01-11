package classes;

import java.util.ArrayList;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.Self;

public class Budget {
	protected static Node budgetSpace;

	public Budget(String kitchenName) {
		budgetSpace = new Node("Budget" + kitchenName, new TupleSpace());
		budgetSpace.addPort(Server.vp);
		budgetSpace.addAgent(new BudgetMonitor("Budget Agent"));
		budgetSpace.start();

	}

	public static class BudgetMonitor extends Agent {

		Template cmdTemp = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public BudgetMonitor(String name) {
			super(name);

		}

		@Override
		protected void doRun() throws Exception {

			while (true) {

				try {
					while (true) {
						Tuple t = get(cmdTemp, Self.SELF);
						Tuple data = t.getElementAt(Tuple.class, 1);
						String cmd = t.getElementAt(String.class, 0);

						exec(new BudgetAgent(data, cmd));
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static class BudgetAgent extends Agent {

		String cmd, userName, kitchenName;
		Tuple data;

		public BudgetAgent(Tuple data, String cmd) {
			super(data.getElementAt(String.class, 0));
			this.data = data;
			this.cmd = cmd;
			this.kitchenName = data.getElementAt(String.class, 1);
			this.userName = data.getElementAt(String.class, 0);

		}

		@Override
		protected void doRun() {
			try {

				switch (cmd) {

				case "getBalance":
					getBalance();
					break;

				case "addBalance":
					dayBudget(data);
					break;

				case "resetBalance":
					resetBalance(userName);
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void resetBalance(String userName) {
			String feedback = "resetBalanceFeedback";
			try {
				Template temp = new Template(new ActualTemplateField("user"), new ActualTemplateField(userName),
						new FormalTemplateField(Integer.class));
				Tuple t = getp(temp);
				
				if(t == null) {
					feedback(feedback, false, userName + " has no balance set.");
				} else {
					put(new Tuple("user", userName, 0), Self.SELF);
					feedback(feedback, true, userName + "'s balance has been reset.");
				}
				
			} catch (Exception e) {

			}
		}

		public void getBalance() {
			String feedback = "getBalanceFeedback";
			Template temp = new Template(new ActualTemplateField(userName), new FormalTemplateField(Integer.class));
			int balance;
			try {
				if (queryp(temp) != null) {
					Tuple t = query(temp, Self.SELF);
					balance = t.getElementAt(Integer.class, 1);
					feedback(feedback, true, "Balance for user: " + userName + " - " + balance + "kr");
				} else {
					feedback(feedback, false, userName + " could not be found.");
				}
			} catch (Exception e) {

			}
		}

		private void feedback(String feedback, boolean result, String message) {
			try {
				put(new Tuple(feedback, new Tuple(userName, result, message)), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void dayBudget(Tuple data) {
			try {

				Template temp = new Template(new ActualTemplateField(data.getElementAt(String.class, 0)),
						new ActualTemplateField(data.getElementAt(String.class, 1)),
						new FormalTemplateField(String.class), new FormalTemplateField(Integer.class),
						new FormalTemplateField(ArrayList.class));
				Tuple t = getp(temp);

				if (t == null) {
					put(data, Self.SELF);
					int price = data.getElementAt(Integer.class, 3);
					ArrayList<String> attendees = data.getElementAt(ArrayList.class, 4);

					double perPrice = price / attendees.size();

					for (String attendee : attendees) {
						addBalance(attendee, perPrice);
					}
				} else {
					int oldPrice = t.getElementAt(Integer.class, 3);
					ArrayList<String> oldAttendees = t.getElementAt(ArrayList.class, 4);
					double oldPricePer = oldPrice / oldAttendees.size();
					
					for(String attendee : oldAttendees) {
						addBalance(attendee, -oldPricePer);
					}
					dayBudget(data);
				}

			} catch (Exception e) {

			}
		}

		private void addBalance(String attendee, double price) {

			try {
				Template temp = new Template(new ActualTemplateField("user"), new FormalTemplateField(String.class),
						new FormalTemplateField(Double.class));
				Tuple user = getp(temp);
				if (user == null) {
					put(new Tuple("user", attendee, price), Self.SELF);
				} else {
					double newPrice = price + user.getElementAt(Double.class, 2);
					put(new Tuple("user", attendee, newPrice), Self.SELF);
				}

			} catch (Exception e) {

			}
		}

	}

}
