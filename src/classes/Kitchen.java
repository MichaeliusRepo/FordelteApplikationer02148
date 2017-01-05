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
	
	public Kitchen(String name){
		Node kitchenSpace = new Node("KitchenSpace", new TupleSpace());
		Agent kitchenAgent = new KitchenAgent("1");
		kitchenSpace.addAgent(kitchenAgent);
		this.name = name;
		kitchenSpace.start();
		
	}
	
	public static class KitchenAgent extends Agent {
		
		protected static PointToPoint p;
			
			public KitchenAgent(String id) {
				super(id);
				p = new PointToPoint("UserSpace",DinnerClub.vp.getAddress());

			}

			@Override
			protected void doRun() {
				Template t = new Template(new FormalTemplateField(String.class));

				try {
					System.out.println("kitchen first");
					Tuple tuple = get(t,p);
					System.out.println("after");
					System.out.println("in Kitchen " + tuple.getElementAt(0));
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

}
