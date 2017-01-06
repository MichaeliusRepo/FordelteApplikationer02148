package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ts.TupleSpace;

public class Budget {
	protected Node budgetSpace;

	public Budget() {
		budgetSpace = new Node("Budget", new TupleSpace());
		budgetSpace.addPort(Server.vp);	
		
	}
	
	public static class BudgetAgent extends Agent {

		public BudgetAgent(String name) {
			super(name);
			
		}

		@Override
		protected void doRun() throws Exception {
			
			
		}
		
	}
}
