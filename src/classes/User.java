package classes;

import java.io.IOException;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;
import org.cmg.resp.topology.VirtualPort;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;

public class User {
	protected String UserName;
	protected Node userSpace;
	protected String kitchenName;
	protected UserAgent userAgent;

	/*
	 * public User(String name){ //TODO - Lav user uden navn }
	 */

	public User(String UserName, String kitchenName) {
		
		this.UserName = UserName;
		userSpace = new Node("UserSpace" + UserName, new TupleSpace());
		userSpace.addPort(DinnerClub.vp);
		userAgent = new UserAgent(UserName);
		userSpace.addAgent(userAgent);
		setKitchen(kitchenName);
		userSpace.start();
	}

	public static class UserAgent extends Agent {

		protected static PointToPoint p;

		public UserAgent(String UserName) {
			super(UserName);

		}

		@Override
		protected void doRun() {
			Tuple t = new Tuple("UserSpace" + name, "Day", "message");

			try {
				addDay(10,1,2017);
				
				t = get(new Template(new FormalTemplateField(String.class),
						new FormalTemplateField(String.class),
						new FormalTemplateField(String.class)),Self.SELF);
				System.out.println("in user again " + t.getElementAt(String.class, 0));

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void setKitchenPointer(String kitchenName) {
			p = new PointToPoint("KitchenSpace" + kitchenName, DinnerClub.vp.getAddress());
		}
		
		public void addDay(int day, int mounth, int year){
			Tuple t = new Tuple("UserSpace" + name, "New Day", day, mounth, year);
			try {
				put(t,p);
				System.out.println("in user message: " + t.getElementAt(String.class, 0) + " sending to "+p.getName());
				t = get(new Template(new FormalTemplateField(String.class), 
						new ActualTemplateField("New Day"),
						new FormalTemplateField(int.class),
						new FormalTemplateField(int.class),
						new FormalTemplateField(int.class)
						),p);
				System.out.println("in user message: " + t.getElementAt(String.class, 0) + " sending to "+p.getName());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}

	public void setKitchen(String kitchenName) {
		this.kitchenName = kitchenName;
		userAgent.setKitchenPointer(kitchenName);

	}

	public String getKitcen() {
		return kitchenName;
	}
	


}
