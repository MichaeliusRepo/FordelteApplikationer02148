package classes;

import java.util.LinkedList;

import org.cmg.resp.behaviour.Agent;
import org.cmg.resp.comp.Node;
import org.cmg.resp.knowledge.ActualTemplateField;
import org.cmg.resp.knowledge.FormalTemplateField;
import org.cmg.resp.knowledge.Template;
import org.cmg.resp.knowledge.Tuple;
import org.cmg.resp.knowledge.ts.TupleSpace;
import org.cmg.resp.topology.Self;

public class Day {

	public Day(int day, int month, int year) {

		Node daySpace = new Node("" + day + "" + month + "" + year, new TupleSpace());
		Agent dayAgent = new DayMonitor("day");
		daySpace.addPort(Server.vp);
		daySpace.addAgent(dayAgent);
		daySpace.start();
	}

	public class DayMonitor extends Agent {

		Template cmdTemp = new Template(new FormalTemplateField(String.class), new FormalTemplateField(Tuple.class));

		public DayMonitor(String name) {
			super(name);

		}

		@Override
		protected void doRun() throws Exception {

			try {
				put(new Tuple("dayCreated"), Self.SELF);
				while (true) {
					Tuple t = get(cmdTemp, Self.SELF);
					Tuple data = t.getElementAt(Tuple.class, 1);
					String cmd = t.getElementAt(String.class, 0);

					exec(new DayAgent(data, cmd));
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class DayAgent extends Agent {

		String cmd, user, chef;
		int attendees, price;
		Tuple data;

		public DayAgent(Tuple data, String cmd) {
			super(data.getElementAt(String.class, 0));
			this.data = data;
			this.cmd = cmd;
			this.user = data.getElementAt(String.class, 0);

		}

		@Override
		protected void doRun() throws Exception {
			try {
				if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
					switch (cmd) {

					case "attendDay":
						this.attendees = data.getElementAt(Integer.class, 2);
						attendDay(user, attendees);
						break;

					case "unattendDay":
						unattendDay(user);

					case "lockDay":
						lockDay();
						break;

					case "addChef":
						addChef(user);
						break;

					}
				}

				if (queryp(new Template(new ActualTemplateField("locked"))) != null) {
					switch (cmd) {
					case "setPrice":
						this.price = data.getElementAt(Integer.class, 2);
						setPrice(price);
						break;
					}
				}
			} catch (Exception e) {

			}
		}

		private void unattendDay(String user) {
			String feedback = "unattendFeedback";
			try {
				if (getp(new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class))) == null) {
					feedback(feedback, false);
				} else {
					feedback(feedback, true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setPrice(int price) {
			String feedback = "setPriceFeedback";
			try {
				if (getp(new Template(new ActualTemplateField("price"),
						new FormalTemplateField(Integer.class))) == null) {
					put(new Tuple("price", price), Self.SELF);
					feedback(feedback, true);
				} else {
					put(new Tuple("price", price), Self.SELF);
					feedback(feedback, false);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addChef(String user) {
			try {
				String feedback = "addChefFeedback";
				LinkedList<Tuple> chefs = queryAll(
						new Template(new ActualTemplateField("chef"), new FormalTemplateField(String.class)));
				if (chefs.size() < 2) {
					put(new Tuple("chef", user), Self.SELF);
					feedback(feedback, true);
				} else {
					feedback(feedback, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void lockDay() {
			try {
				put(new Tuple("locked"), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void attendDay(String user, int attendees) {
			String feedback = "attendDayFeedback";
			try {
				if (queryp(
						new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class))) != null) {
					put(new Tuple(user, attendees), Self.SELF);
					feedback(feedback, true);
				} else {
					feedback(feedback, false);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void feedback(String feedback, boolean result) {
			try {
				put(new Tuple(feedback, user, result), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
