package classes;

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

		String cmd;
		String user;
		String chef;
		int attendees;
		int price;
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
						addUser(user, attendees);
						break;

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

		private void setPrice(int price) {
			try {
				if (queryp(new Template(new ActualTemplateField("price"),
						new FormalTemplateField(Integer.class))) == null) {
					
					put(new Tuple("price", price), Self.SELF);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addChef(String user) {
			try {
				put(new Tuple("chef", user), Self.SELF);
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

		private void addUser(String user, int attendees) {
			try {
				put(new Tuple(user, attendees), Self.SELF);
				put(new Tuple("userAttended"), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}
}
