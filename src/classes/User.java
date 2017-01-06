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
		userSpace.start();
	}

	public static class UserAgent extends Agent {

		protected static PointToPoint p;

		public UserAgent(String UserName) {
			super(UserName);

		}

		@Override
		protected void doRun() {

		}
	}

}
