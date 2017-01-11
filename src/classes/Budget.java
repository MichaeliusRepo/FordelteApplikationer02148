package classes;

import java.util.ArrayList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

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

				if (t == null) {
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
				put(new Tuple(new Tuple(userName, kitchenName, result, message), feedback), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void dayBudget(Tuple data) {
			try {

				String dayName = data.getElementAt(String.class, 0);
				String kitchenName = data.getElementAt(String.class, 1);

				// <day, kitchen, buyer, price, attendees>
				Template temp = new Template(new ActualTemplateField(dayName), new ActualTemplateField(kitchenName),
						new FormalTemplateField(String.class), new FormalTemplateField(Integer.class),
						new FormalTemplateField(ArrayList.class));

				/*
				 * A check must be made to ensure the price hasn't already been
				 * set for the given day. In that case the balance for each user
				 * needs to be reset.
				 */
				Tuple oldData = getp(temp);

				if (oldData == null) {
					put(data, Self.SELF);
					int price = data.getElementAt(Integer.class, 3);
					String buyer = data.getElementAt(String.class, 2);
					ArrayList<String> attendees = data.getElementAt(ArrayList.class, 4);

					double perPrice = price / attendees.size();

					/*
					 * Since the buyer has paid for the entire meal, the price
					 * should be subtracted from the buyers balance. The buyer
					 * must however still pay for himself.
					 */
					addBalance(buyer, -price);

					for (String attendee : attendees) {
						addBalance(attendee, perPrice);
					}

				} else {
					int oldPrice = oldData.getElementAt(Integer.class, 3);
					String oldBuyer = oldData.getElementAt(String.class, 2);
					ArrayList<String> oldAttendees = oldData.getElementAt(ArrayList.class, 4);

					/*
					 * The following calls a recursion using a negative price,
					 * to reset all attendees' balance to the value they were at
					 * before the old price had been set.
					 */
					dayBudget(new Tuple(dayName, kitchenName, oldBuyer, -oldPrice, oldAttendees));

					/*
					 * Recursion can now be called with the new Tuple since the
					 * old tuple has been removed and the prices have been reset
					 * to their value before the oldPrice was added.
					 */
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
