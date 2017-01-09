package classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
				//TODO - lav en metode til at tilfï¿½je en chef(kan evt virke som change chef)
				break;
				
			case "setPrice":
				// TODO - lav metode til at fortælle hvor meget maden kostede på
				// en dag
				break;
				
			case "addBalance":
				//TODO - tager prisen for maden og lï¿½gger det over i budget
				break;
				
			case "resetBalance":
				//TODO - Metode der bruges til at nulstille balance pï¿½ alle brugere nï¿½r der skal betales
				break;
			}
		}

		// Metode til at tilføje ny dag
		private void addDay(Tuple data) {
			int day = data.getElementAt(Integer.class, 2);
			int month = data.getElementAt(Integer.class, 3);
			int year = data.getElementAt(Integer.class, 4);
			String target = "" + day + "" + month + "" + year;

			try {

				if (!checkDayExits(target)) {

					put(new Tuple("" + day + "" + month + "" + year, new Day(day, month, year)), Self.SELF);
					p = new PointToPoint("" + day + "" + month + "" + year, Server.vp.getAddress());
					
					sendFeedback("addDay", recieveFeedback(target, "dayCreated"));
					

				} else {
					sendFeedback("addDay", false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void removeDay(Tuple data) {
			String target = "" + data.getElementAt(Integer.class, 1) + "" + data.getElementAt(Integer.class, 2) + ""
					+ data.getElementAt(Integer.class, 3);

			try {
				if (!checkDayExits(target)) {
					sendFeedback("removeDay",false);
				} else {
					sendFeedback("removeDay",true);
				}
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

				if (checkDayExits(target)) {
					sendFeedback("attendDay", false);
				} else {
					p = new PointToPoint(target,Server.vp.getAddress());
					put(new Tuple("attendDay", new Tuple(user,kitchen,attendees)),p);
					sendFeedback("attendDay", recieveFeedback(target, "attendDayFeedback"));	
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private boolean recieveFeedback(String target, String feedback){
			try {
				p = new PointToPoint(target, Server.vp.getAddress());
				Tuple feedbackTuple = get(new Template(new ActualTemplateField(feedback), new ActualTemplateField(user),
						new FormalTemplateField(Boolean.class)), p);
				return feedbackTuple.getElementAt(Boolean.class,2);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}
		
		private void sendFeedback(String cmd, boolean result){
			try{
				p = new PointToPoint("Server", Server.vp.getAddress());
				Tuple feedbackData = new Tuple(user, kitchen, result);
				put(new Tuple(cmd + " Feedback", feedbackData), p);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		private boolean checkDayExits(String target){
			Template checkDayTemplate = new Template(new ActualTemplateField(target),
					new FormalTemplateField(Day.class));
			if(queryp(checkDayTemplate) == null){
				return false;
			}else{
				return true;
			}
		}

	}
}
