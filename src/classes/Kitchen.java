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
		kitchenSpace.addPort(Server.vp);
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
			
		}
	}
}
