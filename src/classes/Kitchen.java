package classes;

import java.util.LinkedList;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;

public class Kitchen {
	protected String kitchenName;
	protected static Node kitchenSpace;
	protected LinkedList<Day> days;

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
			Tuple cmd;
			Template cmdTemp = new Template(new FormalTemplateField(String.class),
					new FormalTemplateField(Tuple.class));
			try {
				switch (get(cmdTemp, Self.SELF).getElementAt(String.class, 0)) {
				case "newDayCMD":
					break;

				case "2":
					break;
				}
			} catch (Exception e) {

			}
		}

		protected void addDay(int day, int month, int year) {
			Day newDay = new Day(day, month, year);

		}
	}
}
