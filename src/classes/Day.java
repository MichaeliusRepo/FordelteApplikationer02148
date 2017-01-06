package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ts.TupleSpace;

public class Day {

	public Day() {
		
		Node daySpace = new Node("day", new TupleSpace());
		Agent dayAgent = new DayAgent("day");
		
		daySpace.addAgent(dayAgent);
	}
	
	public static class DayAgent extends Agent{

		public DayAgent(String name) {
			super(name);
			
		}

		@Override
		protected void doRun() throws Exception {
			
			try{
				
			} catch(Exception e) {
				
			}
			
		}
		
	}
}
