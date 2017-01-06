package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ts.TupleSpace;

public class Day {

	public Day(int day, int month, int year) {

		Node daySpace = new Node("day", new TupleSpace());
		Agent dayAgent = new DayAgent("day");
		daySpace.addPort(Server.vp);
		daySpace.addAgent(dayAgent);
		daySpace.start();
	}

	public static class DayAgent extends Agent {

		public DayAgent(String name) {
			super(name);

		}

		@Override
		protected void doRun() throws Exception {

			try {

			} catch (Exception e) {

			}

		}

	}
}
