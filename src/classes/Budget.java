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


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Budget {
	private static Node budgetSpace;

	public Budget(String kitchenName) {
		budgetSpace = new Node("Budget" + kitchenName, new TupleSpace());
		budgetSpace.addPort(Server.vp);
		budgetSpace.addAgent(new BudgetMonitor("Budget Agent"));
		budgetSpace.start();
	}

	private class BudgetMonitor extends Agent {

		private Template cmdTemp = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private BudgetMonitor(String name) {
			super(name);
		}

		@Override
		protected void doRun() throws Exception {
			try {
				while (true) {
					Tuple t = get(cmdTemp, Self.SELF);
					exec(new BudgetAgent(t));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private class BudgetAgent extends Agent {

		private String cmd, userName, kitchenName;
		private Tuple data;

		private BudgetAgent(Tuple cmdTuple) {
			super(cmdTuple.getElementAt(String.class, ECommand.USERNAME.getValue()));
			this.data = cmdTuple.getElementAt(Tuple.class, ECommand.DATA.getValue());
			this.cmd = cmdTuple.getElementAt(String.class, ECommand.COMMAND.getValue());
			this.kitchenName = cmdTuple.getElementAt(String.class, ECommand.KITCHEN.getValue());
			this.userName = cmdTuple.getElementAt(String.class, ECommand.USERNAME.getValue());
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

				case "addUserBalance":
					addUserBalance(data);
					break;

				case "resetUserBalance":
					resetUserBalance();
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void resetUserBalance() {
			String feedback = "resetUserBalanceFeedback";
			try {
				Template temp = new Template(new ActualTemplateField("user"), new ActualTemplateField(userName),
						new FormalTemplateField(Double.class));
				Tuple t = getp(temp);

				if (t == null)
					feedback(feedback, false, userName + " has no balance set.");
				else {
					put(new Tuple("user", userName, 0.0), Self.SELF);
					feedback(feedback, true, userName + "'s balance has been reset.");
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void addUserBalance(Tuple data) {
			String feedback = "addUserBalanceFeedback";
			int payment = data.getElementAt(Integer.class, 0);

			addBalance(userName, payment);
			feedback(feedback, true, userName + "'s balance has been updated with " + payment + ".");

		}

		public void getBalance() {
			String feedback = "getBalanceFeedback";
			Template temp = new Template(new ActualTemplateField("user"), new ActualTemplateField(userName),
					new FormalTemplateField(Double.class));
			double balance;
			try {
				// This is used to make sure the program doesn't print the
				// entire double, but rounds it down to two decimals.
				if (queryp(temp) != null) {
					Tuple t = query(temp, Self.SELF);
					balance = t.getElementAt(Double.class, 2);
					balance = Math.round(balance * 100);
					balance = balance / 100;
					feedback(feedback, true, "" + balance);
				} else
					feedback(feedback, false, userName + " has no balance.");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void feedback(String feedback, boolean result, String message) {
			try {
				put(new Tuple(feedback, userName, kitchenName, true, new Tuple(result, message)), Self.SELF);
				Template what = new Template(new FormalTemplateField(Tuple.class), new ActualTemplateField(feedback));
				System.out.println(queryEmpty(what));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("unchecked")
		private void dayBudget(Tuple data) {
			try {
				String dayName = data.getElementAt(String.class, 0);
				String kitchenName = data.getElementAt(String.class, 1);

				// <day, kitchen, buyer, price, attendees>
				Template temp = new Template(new ActualTemplateField(dayName), new ActualTemplateField(kitchenName),
						new FormalTemplateField(String.class), new FormalTemplateField(Double.class),
						new FormalTemplateField(ArrayList.class));

				/*
				 * A check must be made to ensure the price hasn't already been
				 * set for the given day. In that case the balance for each user
				 * needs to be reset.
				 */
				Tuple oldData = getp(temp);

				String feedback = "dayBudgetFeedback";

				if (oldData == null) {
					put(data, Self.SELF);
					double price = data.getElementAt(Double.class, 3);
					String buyer = data.getElementAt(String.class, 2);
					ArrayList<String> attendees = data.getElementAt(ArrayList.class, 4);

					double perPrice = price / attendees.size();

					for (String attendee : attendees) {
						addBalance(attendee, perPrice);
						System.out.println(attendee + " was added to Budget");
					}

					/*
					 * Since the buyer has paid for the entire meal, the price
					 * should be subtracted from the buyers balance. The buyer
					 * must however still pay for himself.
					 */
					addBalance(buyer, -price);

					feedback(feedback, true, "Budget has been updated.");

				} else {
					double oldPrice = oldData.getElementAt(Double.class, 3);
					ArrayList<String> oldAttendees = oldData.getElementAt(ArrayList.class, 4);
					String oldBuyer = oldData.getElementAt(String.class, 2);
					double oldPerPrice = oldPrice / oldAttendees.size();

					for (String attendee : oldAttendees)
						addBalance(attendee, -oldPerPrice);

					addBalance(oldBuyer, oldPrice);

					/*
					 * Recursion can now be called with the new Tuple since the
					 * old tuple has been removed and the prices have been reset
					 * to their value before the oldPrice was added.
					 */
					dayBudget(data);
					feedback(feedback, true, "Budget had already been calculated. Has been updated");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void addBalance(String attendee, double perPrice) {
			try {
				Template temp = new Template(new ActualTemplateField("user"), new ActualTemplateField(attendee),
						new FormalTemplateField(Double.class));
				Tuple user = getp(temp);

				// Need to check whether or not the user already has a balance.
				// In that case the new price needs to be added.

				if (user == null)
					put(new Tuple("user", attendee, perPrice), Self.SELF);
				else {
					double newPrice = perPrice + user.getElementAt(Double.class, 2);
					put(new Tuple("user", attendee, newPrice), Self.SELF);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private boolean queryEmpty(Template t) {
			LinkedList<Tuple> getAll = queryAll(t);
			return (getAll.isEmpty()) ? true : false;
		}
	}

}
