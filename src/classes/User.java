package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;
import org.cmg.resp.topology.VirtualPort;

public class User {
	protected String name;
	protected Node userSpace;

	public User(String name) {
		userSpace = new Node("UserSpace" + name, new TupleSpace());
		userSpace.addPort(DinnerClub.vp);
		this.name = name;
		userSpace.start();
	}

	public static class UserAgent extends Agent {

		protected static PointToPoint p;

		public UserAgent(String id) {
			super(id);
			p = new PointToPoint("KitchenSpace", DinnerClub.vp.getAddress());

		}

		@Override
		protected void doRun() {
			Tuple t = new Tuple("Day", "message");

			try {
				put(t, p);
				System.out.println("in user " + t.getElementAt(String.class, 0));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		userSpace.start();
	}

	public void setKitchen(String kitchenName) {
		// TODO Auto-generated method stub
		Agent userAgent = new UserAgent(kitchenName);
		userSpace.addAgent(userAgent);
		
	}

}
