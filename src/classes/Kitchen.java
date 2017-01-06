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
	protected String kitchenName;
	protected static Node kitchenSpace;

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;
		kitchenSpace = new Node("KitchenSpace" + kitchenName, new TupleSpace());
		kitchenSpace.addPort(DinnerClub.vp);
		Agent kitchenAgent = new KitchenAgent(kitchenName);
		kitchenSpace.addAgent(kitchenAgent);
		kitchenSpace.start();

	}

	public static class KitchenAgent extends Agent {

		protected static PointToPoint p;

		public KitchenAgent(String kitchenName) {
			super(kitchenName);

		}

		@Override
		protected void doRun() {
			Template tAddDay = new Template(new FormalTemplateField(String.class), 
					new ActualTemplateField("New Day"),
					new FormalTemplateField(int.class),
					new FormalTemplateField(int.class),
					new FormalTemplateField(int.class)
					);

			try {
				while (true) {
					//System.out.println("kitchen name: "+kitchenSpace.getName());
					//Tuple tuple = get(tAddDay, Self.SELF);
					//System.out.println("in kitchen again");
					//p = new PointToPoint(tuple.getElementAt(String.class, 0), DinnerClub.vp.getAddress());
					//System.out.println("in Kitchen " + tuple.getElementAt(String.class, 0));
					//put(tuple, p);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
