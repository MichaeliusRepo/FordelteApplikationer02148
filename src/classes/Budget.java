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
					
					Tuple cmd = get(cmdTemp, Self.SELF);
					String name = cmd.getElementAt(Tuple.class, 1).getElementAt(String.class, 0);
					
					switch (cmd.getElementAt(String.class, 0)) {
					
					case "getBalance":
						exec(new BalanceAgent(name, "getBalance"));
						break;

					case "setBalance":
						exec(new BalanceAgent(name, "setBalance"));
						break;
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

		public BalanceAgent(String who, String cmd) {
			super(who);
			this.cmd = cmd;
			this.name = who;
		}

		@Override
		protected void doRun() {
			try {
				switch (cmd) {

				case "getBalance":
					getBalance(name);
					break;

				case "setBalance":
					setBalance();
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setBalance() {

		}

		public void getBalance(String name) {
			Template temp = new Template(new ActualTemplateField(name), new FormalTemplateField(Integer.class));
			int balance;
			try{
				Tuple t = get(temp, Self.SELF);
				balance = t.getElementAt(Integer.class, 1);
			} catch(Exception e) {
				
			}
		}
	}

}
