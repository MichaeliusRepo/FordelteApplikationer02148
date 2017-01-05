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
	protected String kitchenName;
	protected UserAgent userAgent;
	
	/*public User(String name){
		//TODO - Lav user uden navn
	}*/
	
	public User(String name, String kitchenName) {
		
		
		userSpace = new Node("UserSpace" + name, new TupleSpace());
		userSpace.addPort(DinnerClub.vp);
		this.name = name;
		userAgent = new UserAgent(kitchenName);
		userSpace.addAgent(userAgent);
		setKitchen(kitchenName);
		userSpace.start();
	}

	public static class UserAgent extends Agent {

		protected static PointToPoint p;

		public UserAgent(String kitchenName) {
			super(kitchenName);

		}

		@Override
		protected void doRun() {
			Tuple t = new Tuple("UserSpace" + name,"Day", "message");

			try {
				put(t, p);
				System.out.println("in user " + t.getElementAt(String.class, 0));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		public void setKitchenPointer(String kitchenName){
			p = new PointToPoint("KitchenSpace" + kitchenName, DinnerClub.vp.getAddress());
		}
	}

	public void setKitchen(String kitchenName) {
		// TODO Auto-generated method stu;
		this.kitchenName = kitchenName;
		userAgent.setKitchenPointer(kitchenName);
		
	}
	
	public String getKitcen(){
		return kitchenName;
	}

}
