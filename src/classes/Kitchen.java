package classes;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.PointToPoint;
import org.cmg.resp.topology.Self;

public class Kitchen {
	protected String kitchenName;
	protected Node kitchenSpace;

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;

		kitchenSpace = new Node(kitchenName, new TupleSpace());
		kitchenSpace.addPort(Server.vp);
		// Agent kitchenAgent = new KitchenAgent(kitchenName);
		Agent monitor = new KitchenMonitor("kitchenMonitor");
		kitchenSpace.addAgent(monitor);
		kitchenSpace.start();
	}

	public class KitchenMonitor extends Agent {

		Tuple t;
		Template kitchenTemplate = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(Tuple.class));

		public KitchenMonitor(String who) {
			super(who);
		}

		@Override
		protected void doRun() {

			while (true) {
				try {
					t = get(kitchenTemplate, Self.SELF);
					Tuple data = t.getElementAt(Tuple.class, 1);
					String cmd = t.getElementAt(String.class, 0);
					exec(new KitchenAgent(cmd, data));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public class KitchenAgent extends Agent {
		protected PointToPoint p;

		Tuple data;
		String user, kitchen;
		String cmd;

		public KitchenAgent(String cmd, Tuple data) {
			super(cmd);
			this.data = data;
			this.cmd = cmd;
			this.user = data.getElementAt(String.class, 0);
			this.kitchen = data.getElementAt(String.class, 1);
		}

		@Override
		protected void doRun() {

			switch (cmd) {

			case "addDay":
				addDay(data);
				break;

			case "removeDay":
				removeDay(data);
				break;
			case "attendDay":
				attendDay(data);
				break;
			case "addChef":

				System.out.println("Adding chef...?");

				break;

			case "setPrice":
				setPrice(data);

				break;

			case "addBalance":

				break;

			case "resetBalance":

				break;
			}
		}

		private void addDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {

				if (!checkDayExists(target)) {

					put(new Tuple("" + day + "" + month + "" + year, new Day(day, month, year)), Self.SELF);
					p = new PointToPoint("" + day + "" + month + "" + year, Server.vp.getAddress());
					sendFeedback("addDay", recieveFeedback(target, "dayCreated"));
					
				} else
					sendFeedback("addDay", new Tuple(user, kitchen, false, "Dagen findes allerede"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void removeDay(Tuple data) {
			String target = "" + data.getElementAt(Integer.class, 1) + "" + data.getElementAt(Integer.class, 2) + ""
					+ data.getElementAt(Integer.class, 3);

			try {
				if (!checkDayExists(target))
					sendFeedback("removeDay", new Tuple(user, kitchen, false, "Den valgte dag findes ikke"));
				else
					sendFeedback("removeDay", new Tuple(user, kitchen, true, "Dagen er blevet slettet"));

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void attendDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			int attendees = data.getElementAt(Integer.class, 5);
			String target = "" + day + "" + month + "" + year;

			try {
				if (!checkDayExists(target))
					sendFeedback("attendDay", new Tuple(user, kitchen, "Den valgte dag findes ikke"));
				else {
					p = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("attendDay", new Tuple(user, kitchen, attendees)), p);
					sendFeedback("attendDay", recieveFeedback(target, "attendDayFeedback"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void setPrice(Tuple data){
			int day = data.getElementAt(Integer.class,2);
			int month = data.getElementAt(Integer.class,3);
			int year = data.getElementAt(Integer.class,4);
			int price = data.getElementAt(Integer.class, 5);
			String target = "" + day + "" + month + "" + year;
			
			try{
				if(checkDayExists(target)){
					p = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("setPrice", new Tuple(user, kitchen, price)),p);
					sendFeedback("setPrice", recieveFeedback(target, "setPriceFeedback"));
				}else{
					sendFeedback("setPrice", new Tuple(user, kitchen, false, "Dagen findes ikke"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

		private Tuple recieveFeedback(String target, String feedbackCmd) {
			try {
				p = new PointToPoint(target, Server.vp.getAddress());
				Tuple feedbackTuple = get(
						new Template(new ActualTemplateField(feedbackCmd), new ActualTemplateField(user),
								new FormalTemplateField(Boolean.class), new FormalTemplateField(String.class)),
						p);
				return feedbackTuple;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void sendFeedback(String cmd, Tuple result) {
			try {
				p = new PointToPoint("Server", Server.vp.getAddress());
				put(new Tuple(cmd + " Feedback", result), p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private boolean checkDayExists(String target) {
			Template checkDayTemplate = new Template(new ActualTemplateField(target),
					new FormalTemplateField(Day.class));
			return (queryp(checkDayTemplate) == null) ? false : true;
		}

	}
}
