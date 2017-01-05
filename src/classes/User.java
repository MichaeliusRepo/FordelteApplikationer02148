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
			Tuple tq = new Tuple("Q","message","start(P)");
			System.out.println(tq.getElementAt(String.class,2));
			Tuple tr = new Tuple("R","message","start(P)");
			System.out.println(tr.getElementAt(String.class,2));

			try {
				put(tq, Self.SELF);
				put(tr, Self.SELF);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	

}
