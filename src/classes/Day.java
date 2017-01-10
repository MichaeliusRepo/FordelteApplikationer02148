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
	
	int day, month, year;

	public Day(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
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
						break;
						
					case "lockDay":
						lockDay();
						break;

					case "addChef":
						addChef(user);
						break;

					}
				} else {
					
				}

				if (queryp(new Template(new ActualTemplateField("locked"))) != null) {
					switch (cmd) {
					case "setPrice":
						this.price = data.getElementAt(Integer.class, 2);
						setPrice(price);
						break;
					}
				} else {
					
				}
				
			} catch (Exception e) {

			}
		}

		private void unattendDay(String user) {
			String feedback = "unattendFeedback";
			try {
				if (getp(new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class))) == null) {
					feedback(feedback, false, user + " isn't set to attend that day.");
				} else {
					feedback(feedback, true, user + " is no longer attending on: " + day + "/" + month + "/" + year);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setPrice(int price) {
			String feedback = "setPriceFeedback";
			try {

				Tuple t = getp(new Template(new ActualTemplateField("price"), new FormalTemplateField(Integer.class)));

				if (t == null) {
					put(new Tuple("price", price), Self.SELF);
					feedback(feedback, true, "The price was set to " + price);
				} else {
					put(new Tuple("price", price), Self.SELF);
					feedback(feedback, false, "The price was already set to " + t.getElementAt(Integer.class, 2)
							+ ", but has been replaced.");
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

					if (queryp(new Template(new ActualTemplateField("chef"), new ActualTemplateField(user))) == null) {

						put(new Tuple("chef", user), Self.SELF);
						feedback(feedback, true, user + " was added as a chef.");

					} else {
						feedback(feedback, false, user + " is already a chef.");
					}

				} else {
					feedback(feedback, false, "There are already two chefs.");
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
				getp(new Template(new ActualTemplateField(user), new FormalTemplateField(Integer.class)));
				put(new Tuple(user, attendees), Self.SELF);
				feedback(feedback, true, "User added.");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void feedback(String feedback, boolean result, String message) {
			try {
				put(new Tuple(feedback, new Tuple(user, result, message)), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
