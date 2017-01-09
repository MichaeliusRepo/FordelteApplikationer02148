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
		budgetSpace.addAgent(new BudgetAgent("Budget Agent"));
		budgetSpace.start();

	}

	public static class BudgetAgent extends Agent {

		Template cmdTemp = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public BudgetAgent(String name) {
			super(name);

		}

		@Override
		protected void doRun() throws Exception {

			while (true) {

				try {

					Tuple t = get(cmdTemp, Self.SELF);
					Tuple data = t.getElementAt(Tuple.class, 1);
					String cmd = t.getElementAt(String.class, 0);

					while (true) {
						switch (cmd) {

						case "getBalance":
							exec(new BalanceAgent(data, cmd));
							break;

						case "addBalance":
							exec(new BalanceAgent(data, cmd));
							break;
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static class BalanceAgent extends Agent {

		String cmd;
		String name;
		Tuple data;

		public BalanceAgent(Tuple data, String cmd) {
			super(data.getElementAt(String.class, 0));
			this.data = data;
			this.cmd = cmd;
			this.name = data.getElementAt(String.class, 0);

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
			Template temp = new Template(new ActualTemplateField(name), new FormalTemplateField(Integer.class));

			try {
				Tuple t = get(temp, Self.SELF);
				put(new Tuple(name, (t.getElementAt(Integer.class, 1) + balance)), Self.SELF);
			} catch (Exception e) {

			}
		}

		public void getBalance() {
			Template temp = new Template(new ActualTemplateField(name), new FormalTemplateField(Integer.class));
			int balance;
			try {
				Tuple t = query(temp, Self.SELF);
				balance = t.getElementAt(Integer.class, 1);
			} catch (Exception e) {

			}
		}
	}

}
