package classes;

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

	public Budget() {
		budgetSpace = new Node("Budget", new TupleSpace());
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

		String cmd;
		String user;
		Tuple data;

		public BudgetAgent(Tuple data, String cmd) {
			super(data.getElementAt(String.class, 0));
			this.data = data;
			this.cmd = cmd;
			this.user = data.getElementAt(String.class, 0);

		}

		@Override
		protected void doRun() {
			try {

				switch (cmd) {

				case "getBalance":
					getBalance();
					break;

				case "addBalance":
					addBalance(data.getElementAt(Integer.class, 1));
					break;

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void addBalance(int balance) {
			Template temp = new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class));

			try {
				Tuple t = get(temp, Self.SELF);
				put(new Tuple(user, (t.getElementAt(Integer.class, 1) + balance)), Self.SELF);
			} catch (Exception e) {

			}
		}

		public void getBalance() {
			String feedback = "getBalance";
			Template temp = new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class));
			int balance;
			try {
				if (queryp(temp) != null) {
					Tuple t = query(temp, Self.SELF);
					balance = t.getElementAt(Integer.class, 1);
					feedback(feedback, true, "Balance for user: " + user);
				} else {
					feedback(feedback, false, user + " could not be found.");
				}
			} catch (Exception e) {

			}
		}
		
		private void feedback(String feedback, boolean result, String message) {
			try {
				put(new Tuple(feedback, user, result, message), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
