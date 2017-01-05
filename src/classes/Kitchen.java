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
		this.name = name;
		kitchenSpace = new Node("KitchenSpace" + name, new TupleSpace());
		kitchenSpace.addPort(DinnerClub.vp);
		Agent kitchenAgent = new KitchenAgent(name);
		kitchenSpace.addAgent(kitchenAgent);
		kitchenSpace.start();

	}

	public static class KitchenAgent extends Agent {

		protected static PointToPoint p;

		public KitchenAgent(String userName) {
			super(userName);

		}

		@Override
		protected void doRun() {
			Template t = new Template(new FormalTemplateField(String.class), new ActualTemplateField("Day"),
					new FormalTemplateField(String.class));

			try {
				while (true) {
					Tuple tuple = get(t, Self.SELF);
					p = new PointToPoint(tuple.getElementAt(String.class, 0), DinnerClub.vp.getAddress());
					// query(t, Self.SELF);
					System.out.println("in Kitchen " + tuple.getElementAt(String.class, 0));
					put(tuple, p);

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
