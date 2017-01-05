package classes;

import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ts.TupleSpace;

public class Kitchen {
	protected String name;
	
	public Kitchen(String name){
		Node kitchenSpace = new Node("KitchenSpace", new TupleSpace());
		this.name = name;
		
	}

}
