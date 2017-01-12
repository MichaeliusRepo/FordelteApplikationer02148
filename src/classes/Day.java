package classes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import org.cmg.jresp.behaviour.Agent;
import org.cmg.jresp.comp.Node;
import org.cmg.jresp.knowledge.ActualTemplateField;
import org.cmg.jresp.knowledge.FormalTemplateField;
import org.cmg.jresp.knowledge.Template;
import org.cmg.jresp.knowledge.Tuple;
import org.cmg.jresp.knowledge.ts.TupleSpace;
import org.cmg.jresp.topology.Self;

public class Day {

	int day, month, year;
	String dayName;

	public Day(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.dayName = "" + day + "" + month + "" + year;
		Node daySpace = new Node(dayName, new TupleSpace());
		Agent dayAgent = new DayMonitor("day");
		daySpace.addPort(Server.vp);
		daySpace.addAgent(dayAgent);
		daySpace.start();
	}

	public class DayMonitor extends Agent {

		// The monitor searches for tuples with the info:
		// <COMMAND, USERNAME, KITCHENNAME, FEEDBACK, DATA>
		Template cmdTemp = new Template(new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new ActualTemplateField(false),
				new FormalTemplateField(Tuple.class));

		public DayMonitor(String name) {
			super(name);

		}

		@Override
		protected void doRun() throws Exception {

			try {
				put(new Tuple("dayCreated"), Self.SELF);
				while (true) {
					Tuple t = getp(cmdTemp);

					if (t != null) {
						Tuple data = t.getElementAt(Tuple.class, ECommand.DATA.getValue());
						String cmd = t.getElementAt(String.class, ECommand.COMMAND.getValue());
						String userName = t.getElementAt(String.class, ECommand.USERNAME.getValue());
						String kitchenName = t.getElementAt(String.class, ECommand.KITCHEN.getValue());
						exec(new DayAgent(cmd, userName, kitchenName, data));
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	public class DayAgent extends Agent {

		String cmd, userName, chef, buyer, kitchenName;
		int attendees, price, totalAttendees;
		Tuple data;
		ArrayList<String> attendeesList;

		public DayAgent(String cmd, String userName, String kitchenName, Tuple data) {
			super(data.getElementAt(String.class, 0));
			this.data = data;
			this.cmd = cmd;
			this.kitchenName = data.getElementAt(String.class, 1);
			this.userName = data.getElementAt(String.class, 0);

		}

		@Override
		protected void doRun() throws Exception {
			try {
				if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
					switch (cmd) {

					case "attendDay":
						this.attendees = data.getElementAt(Integer.class, ECommand.DATA.getValue());
						attendDay(userName, attendees);
						break;

					case "unattendDay":
						unattendDay(userName);
						break;

					case "lockDay":
						lockDay();
						break;

					case "addChef":
						addChef(userName);
						break;

					case "getAttendees":
						getAttendees();
						break;

					}
				} else if (queryp(new Template(new ActualTemplateField("locked"))) != null) {
					switch (cmd) {
					case "setPrice":
						this.price = data.getElementAt(Integer.class, ECommand.DATA.getValue());
						setPrice(price);
						break;

					case "getAttendees":
						getAttendees();
						break;
					}
				} else {

				}

			} catch (Exception e) {

			}
		}

		private void unattendDay(String userName) {
			String feedback = "unattendDayFeedback";
			try {
				if (getp(new Template(new ActualTemplateField("attendee"), new ActualTemplateField(userName),
						new FormalTemplateField(Integer.class))) == null) {
					feedback(feedback, false, userName + " isn't set to attend that day.");
				} else {
					feedback(feedback, true,
							userName + " is no longer attending on: " + day + "/" + month + "/" + year);
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
					this.buyer = userName;
					feedback(feedback, true, "The price was set to " + price);
				} else {
					put(new Tuple("price", price), Self.SELF);
					this.buyer = userName;
					feedback(feedback, false, "The price was already set to " + t.getElementAt(Integer.class, 2)
							+ ", but has been replaced.");
				}
				System.out.println("DayAddBalanceBefore");
				addBalance();
				System.out.println("DayAddBalanceAfter");

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		private void addChef(String userName) {
			try {
				String feedback = "addChefFeedback";
				LinkedList<Tuple> chefs = queryAll(
						new Template(new ActualTemplateField("chef"), new FormalTemplateField(String.class)));

				if (chefs.size() < 2) {

					if (queryp(
							new Template(new ActualTemplateField("chef"), new ActualTemplateField(userName))) == null) {

						put(new Tuple("chef", userName), Self.SELF);
						feedback(feedback, true, userName + " was added as a chef.");

					} else {
						feedback(feedback, false, userName + " is already a chef.");
					}

				} else {
					feedback(feedback, false, "There are already two chefs.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void lockDay() {
			String feedback = "lockDayFeedback";
			try {
				put(new Tuple("locked"), Self.SELF);
				feedback(feedback, true, dayName + " was locked.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void attendDay(String userName, int attendees) {
			String feedback = "attendDayFeedback";
			try {
				Template temp = new Template(new ActualTemplateField("attendee"), new ActualTemplateField(userName),
						new FormalTemplateField(Integer.class));
				Tuple t = getp(temp);

				if (t == null) {
					put(new Tuple("attendee", userName, attendees), Self.SELF);
				} else {
					put(new Tuple("attendee", userName, attendees), Self.SELF);
					totalAttendees -= t.getElementAt(Integer.class, 2);
				}
				totalAttendees += attendees;
				feedback(feedback, true, userName + " added with " + attendees + " attendees.");

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getAttendees() {
			try {

			} catch (Exception e) {

			}
		}

		private void addBalance() {
			try {

				attendeesList = setAttendees();
				put(new Tuple("addBalance", userName, kitchenName, true,
						(new Tuple(dayName, kitchenName, buyer, price, attendeesList)), "addBalance"), Self.SELF);

			} catch (Exception e) {

			}
		}

		private ArrayList<String> setAttendees() {
			LinkedList<Tuple> attendeesTupleList = queryAll(new Template(new ActualTemplateField("attendee"),
					new FormalTemplateField(String.class), new FormalTemplateField(Integer.class)));
			ArrayList<String> list = new ArrayList<>();

			ListIterator<Tuple> iterator = attendeesTupleList.listIterator();
			while (iterator.hasNext()) {
				Tuple t = iterator.next();
				for (int j = 1; j <= t.getElementAt(Integer.class, 2); j++) {
					list.add(t.getElementAt(String.class, 1));
				}
			}
			return list;
		}

		private void feedback(String feedback, boolean result, String message) {
			try {
				put(new Tuple(feedback, userName, kitchenName, true, new Tuple(result, message)), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
