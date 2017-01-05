package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.Self;

public class User {
	protected String name;
	
	public User(String name) {
		Node userSpace = new Node("UserSpace", new TupleSpace());
		this.name = name;
	}
	
public static class UserAgent extends Agent {
		
		public UserAgent(String id) {
			super(id);

		}

		@Override
		protected void doRun() {
			

			try {
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

}
