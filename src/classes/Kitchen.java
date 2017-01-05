package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;

public class Kitchen {
	protected String name;
	protected Node kitchenSpace;

	public Kitchen(String name) {
		kitchenSpace = new Node("KitchenSpace", new TupleSpace());
		kitchenSpace.addPort(DinnerClub.vp);
		this.name = name;
		kitchenSpace.start();

	}

	public static class KitchenAgent extends Agent {

		protected static PointToPoint p;

		public KitchenAgent(String userName) {
			super(userName);
			p = new PointToPoint("UserSpace" + userName, DinnerClub.vp.getAddress());

		}

		@Override
		protected void doRun() {
			Template t = new Template(new ActualTemplateField("Day"), new FormalTemplateField(String.class));

			try {
				while (true) {
					query(t, Self.SELF);
					Tuple tuple = get(t, Self.SELF);
					System.out.println("in Kitchen " + tuple.getElementAt(0));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void start() {
		kitchenSpace.start();

	}

	public void addUser(String userName) {
		// TODO Auto-generated method stub
		Agent kitchenAgent = new KitchenAgent(userName);
		kitchenSpace.addAgent(kitchenAgent);

	}

}
