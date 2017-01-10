package classes;

import java.io.IOException;

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
	protected Budget budget; 

	public Kitchen(String kitchenName) {
		this.kitchenName = kitchenName;

		kitchenSpace = new Node(kitchenName, new TupleSpace());
		budget =  new Budget(kitchenName);
		kitchenSpace.addPort(Server.vp);
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
		String user;
		String cmd;

		public KitchenAgent(String cmd, Tuple data) {
			super(cmd);
			this.data = data;
			this.cmd = cmd;
			this.user = data.getElementAt(String.class, 0);
		}

		@Override
		protected void doRun() {

			switch (cmd) {

			case "addDay":
				addDay(data);
				System.out.println("addDAy");
				break;

			case "removeDay":
				removeDay(data);
				System.out.println("removeDAy");
				break;
			case "attendDay":
				attendDay(data);
				System.out.println("attendDay");
				break;
			case "addChef":
				addChef(data);
				System.out.println("addChef");
				break;

			case "setPrice":
				setPrice(data);
				System.out.println("setPrice");
				break;

			case "addBalance":
				addBalance(data);
				System.out.println("addBalance");
				break;

			case "getBalance":
				getBalance(data);
				System.out.println("getBalance");
				break;
			case "resetUserBalance":
				resetUserBalance(data);
				System.out.println("resetUserBalance");
				break;
			case "lockDay":
				lockDay(data);
				break;
//			case "changeChef":
//				System.out.println("changeChef");
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
					get(new Template(new ActualTemplateField("dayCreated")),p);
					
					sendFeedback("addDay", new Tuple(user, kitchenName, true, "Dagen blev lavet"));
					
				} else
					sendFeedback("addDay", new Tuple(user, kitchenName, false, "Dagen findes allerede"));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void removeDay(Tuple data) {
			String target = "" + data.getElementAt(Integer.class, 1) + "" + data.getElementAt(Integer.class, 2) + ""
					+ data.getElementAt(Integer.class, 3);

			try {
				if (!checkDayExists(target))
					sendFeedback("removeDay", new Tuple(user, kitchenName, false, "Den valgte dag findes ikke"));
				else
					sendFeedback("removeDay", new Tuple(user, kitchenName, true, "Dagen er blevet slettet"));

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
					sendFeedback("attendDay", new Tuple(user, kitchenName, "Den valgte dag findes ikke"));
				else {
					p = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("attendDay", new Tuple(user, kitchenName, attendees)), p);
					sendFeedback("attendDay", recieveFeedback(target, "attendDayFeedback"));
					System.out.println("Jeg lagde det rigtigt");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void addChef(Tuple data){
			int day = data.getElementAt(Integer.class,2);
			int month = data.getElementAt(Integer.class,3);
			int year = data.getElementAt(Integer.class,4);
			String target = "" + day + "" + month + "" + year;
			
			try{
				if(checkDayExists(target)){
					p = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("addChef", data),p);
					sendFeedback("addChef", recieveFeedback(target, "addChefFeedback"));
				}else{
					sendFeedback("addChef", new Tuple(user,kitchenName, false, "Dagen findes ikke"));
				}
			}catch(Exception e){
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
					put(new Tuple("setPrice", new Tuple(user, kitchenName, price)),p);
					sendFeedback("setPrice", recieveFeedback(target, "setPriceFeedback"));
				}else{
					sendFeedback("setPrice", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		private void addBalance(Tuple data){
			
			try {
				p = new PointToPoint("Budget"+kitchenName, Server.vp.getAddress());
				put(new Tuple("addBalance", data),p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void getBalance(Tuple data){
			
			try{
				p = new PointToPoint("Budget"+kitchenName, Server.vp.getAddress());
				put(new Tuple("getBalance",data),p);
				sendFeedback("getBalance", recieveFeedback("Budget"+kitchenName,"getBalanceFeedback"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private void resetUserBalance(Tuple data){
			try{
				p = new PointToPoint("Budget"+kitchenName, Server.vp.getAddress());
				put(new Tuple("resetUserBalance", data),p);
				sendFeedback("resetUserBalance", recieveFeedback("Budget"+kitchenName, "resetUserFeedback"));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private void lockDay(Tuple data){
			int day = data.getElementAt(Integer.class,2);
			int month = data.getElementAt(Integer.class,3);
			int year = data.getElementAt(Integer.class,4);
			String target = "" + day + "" + month + "" + year;
			
			try{
				if(checkDayExists(target)){
					p = new PointToPoint(target, Server.vp.getAddress());
					put(new Tuple("lockDay",data),p);
					sendFeedback("lockDay", recieveFeedback(target,"lockDayFeedback"));
				}else{
					sendFeedback("lockDay", new Tuple(user, kitchenName, false, "Dagen findes ikke"));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private Tuple recieveFeedback(String target, String feedbackCmd) {
			try {
				p = new PointToPoint(target, Server.vp.getAddress());
				Tuple feedbackTuple = get(
						new Template(new ActualTemplateField(feedbackCmd), new FormalTemplateField(Tuple.class)),
						p);
				Tuple feedbackReturnTuple  = feedbackTuple.getElementAt(Tuple.class,1);
				return feedbackReturnTuple;
			} catch (Exception e) {
				e.printStackTrace();
			}

			return null;
		}

		private void sendFeedback(String cmd, Tuple result) {
			try {
				p = new PointToPoint("Server", Server.vp.getAddress());
				put(new Tuple(cmd + " Feedback", result), p);
				System.out.println("Kitchen sent feedback");
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
