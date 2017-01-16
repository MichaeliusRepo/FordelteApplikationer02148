package classes;

import java.util.ArrayList;
import java.util.Collections;
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

	private int day, month, year;
	private String dayName;

	public Day(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;
		this.dayName = "" + day + month + year;
		Node daySpace = new Node(dayName, new TupleSpace());
		Agent dayAgent = new DayMonitor(dayName);
		daySpace.addPort(Server.vp);
		daySpace.addAgent(dayAgent);
		daySpace.start();
	}

	public String getDate() {
		return day + "/" + month + "/" + year;
	}

	private class DayMonitor extends Agent {

		// The monitor searches for tuples with the info:
		// <COMMAND, USERNAME, KITCHENNAME, FEEDBACK, DATA>
		private Template cmdTemp = new Template(new FormalTemplateField(String.class),
				new FormalTemplateField(String.class), new FormalTemplateField(String.class),
				new ActualTemplateField(false), new FormalTemplateField(Tuple.class));

		private DayMonitor(String name) {
			super(name);
		}

		@Override
		protected void doRun() throws Exception {
			put(new Tuple("dayCreated"), Self.SELF);
			while (true) {
				try {
					Tuple t = getp(cmdTemp);
					if (t != null)
						exec(new DayAgent(t));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class DayAgent extends Agent {

		private String cmd, userName, buyer, kitchenName;
		@SuppressWarnings("unused")
		private int attendees, price, totalAttendees;
		private Tuple dataTuple;
		private ArrayList<String> attendeesList;

		private DayAgent(Tuple data) {
			super(data.getElementAt(String.class, ECommand.USERNAME.getValue()));
			this.dataTuple = data.getElementAt(Tuple.class, ECommand.DATA.getValue());
			this.cmd = data.getElementAt(String.class, ECommand.COMMAND.getValue());
			this.kitchenName = data.getElementAt(String.class, ECommand.KITCHEN.getValue());
			this.userName = data.getElementAt(String.class, ECommand.USERNAME.getValue());
		}

		@Override
		protected void doRun() throws Exception {
			try {

				switch (cmd) {
				case "attendDay":
					this.attendees = dataTuple.getElementAt(Integer.class, 0);
					attendDay(userName, attendees);
					break;

				case "unattendDay":
					unattendDay(userName);
					break;

				case "lockDay":
					lockDay();
					break;

				case "getChef":
					getChef();
					break;

				case "addChef":
					addChef(userName);
					break;

				case "getAttendees":
					getAttendees();
					break;

				case "setPrice":
					this.price = dataTuple.getElementAt(Integer.class, 0);
					setPrice(price);
					break;

				case "getPrice":
					getPrice();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void unattendDay(String userName) {
			String feedback = "unattendDayFeedback";
			try {
				if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
					if (getp(new Template(new ActualTemplateField("attendee"), new ActualTemplateField(userName),
							new FormalTemplateField(Integer.class))) == null)
						feedback(feedback, false, userName + " isn't set to attend that day.", null);
					else {
						feedback(feedback, true,
								userName + " is no longer attending on: " + day + "/" + month + "/" + year, null);
					}
				} else {
					feedback(feedback, false, "Day is locked", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void setPrice(int price) {
			String feedback = "setPriceFeedback";
			try {
				if (queryp(new Template(new ActualTemplateField("locked"))) != null) {
					Tuple t = getp(
							new Template(new ActualTemplateField("price"), new FormalTemplateField(Integer.class)));

					put(new Tuple("price", price), Self.SELF);
					this.buyer = userName;

					if (t == null)
						feedback(feedback, true, "The price was set to " + price, null);
					else
						feedback(feedback, false, "The price was already set to " + t.getElementAt(Integer.class, 1)
								+ ", but has been replaced.", null);
					System.out.println("DayAddBalanceBefore");
					addBalance();
					System.out.println("DayAddBalanceAfter");
				} else {
					feedback(feedback, false, "Day hasn't been locked yet.", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getPrice() {
			String feedback = "getPriceFeedback";
			Tuple t = queryp(new Template(new ActualTemplateField("price"), new FormalTemplateField(Integer.class)));
			int price = t.getElementAt(Integer.class, 1);
			feedback(feedback, true, "Currently the price is at " + price, null);

		}

		private void addChef(String userName) {
			try {
				String feedback = "addChefFeedback";
				LinkedList<Tuple> chefs = queryAll(
						new Template(new ActualTemplateField("chef"), new FormalTemplateField(String.class)));
				if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
					if (chefs.size() <= 2) {
						if (queryp(new Template(new ActualTemplateField("chef"),
								new ActualTemplateField(userName))) == null) {
							put(new Tuple("chef", userName), Self.SELF);
							feedback(feedback, true, userName + " was added as a chef.", null);
						} else {
							feedback(feedback, false, userName + " is already a chef.", null);
						}
					} else {
						feedback(feedback, false, "There are already two chefs.", null);
					}
				} else {
					feedback(feedback, false, "Day was locked", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void getChef() {
			try {
				String feedback = "getChefFeedback";
				String msg;
				LinkedList<Tuple> chefs = queryAll(
						new Template(new ActualTemplateField("chef"), new FormalTemplateField(String.class)));
				switch (chefs.size()) {
				case 0:
					msg = "Nobody as of yet is added as chef to this date.";
					break;
				case 1:
					msg = chefs.get(0).getElementAt(String.class, 1) + " is today's cook.";
					break;
				case 2:
					msg = chefs.get(0).getElementAt(String.class, 1) + " and "
							+ chefs.get(1).getElementAt(String.class, 1) + " are cooks today.";
					break;
				default:
					throw new Exception();
				}
				feedback(feedback, true, msg, null);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void lockDay() {
			String feedback = "lockDayFeedback";
			try {
				if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
					put(new Tuple("locked"), Self.SELF);
					feedback(feedback, true, dayName + " was locked.", null);
				} else {
					feedback(feedback, true, dayName + " was already locked.", null);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void attendDay(String userName, int attendees) {
			String feedback = "attendDayFeedback";
			if (queryp(new Template(new ActualTemplateField("locked"))) == null) {
				try {
					Template temp = new Template(new ActualTemplateField("attendee"), new ActualTemplateField(userName),
							new FormalTemplateField(Integer.class));
					Tuple t = getp(temp);

					if (t == null)
						put(new Tuple("attendee", userName, attendees), Self.SELF);
					else {
						put(new Tuple("attendee", userName, attendees), Self.SELF);
						totalAttendees -= t.getElementAt(Integer.class, 2);
					}
					totalAttendees += attendees;
					feedback(feedback, true, userName + " added with " + attendees + " attendees.", null);

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				feedback(feedback, false, "Day is locked", null);
			}
		}

		private void getAttendees() {
			String feedback = "getAttendeesFeedback";
			try {
				LinkedList<Tuple> attendeesTupleList = queryAll(new Template(new ActualTemplateField("attendee"),
						new FormalTemplateField(String.class), new FormalTemplateField(Integer.class)));
				LinkedList<String> list = new LinkedList<String>();
				String attendees = "Attendees:\n";

				ListIterator<Tuple> iterator = attendeesTupleList.listIterator();
				while (iterator.hasNext()) {
					Tuple t = iterator.next();
					for (int j = 1; j <= t.getElementAt(Integer.class, 2); j++) {
						String attendee = t.getElementAt(String.class, 1);
						list.add(attendee);
						attendees = attendee + "\n";
					}
				}
				Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
				feedback(feedback, true, attendees, list);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void addBalance() {
			try {
				attendeesList = setAttendees();
				put(new Tuple("addBalance", userName, kitchenName, true,
						(new Tuple(dayName, kitchenName, buyer, price, attendeesList))), Self.SELF);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private ArrayList<String> setAttendees() {
			LinkedList<Tuple> attendeesTupleList = queryAll(new Template(new ActualTemplateField("attendee"),
					new FormalTemplateField(String.class), new FormalTemplateField(Integer.class)));
			ArrayList<String> list = new ArrayList<>();

			ListIterator<Tuple> iterator = attendeesTupleList.listIterator();
			while (iterator.hasNext()) {
				Tuple t = iterator.next();
				for (int j = 1; j <= t.getElementAt(Integer.class, 2); j++)
					list.add(t.getElementAt(String.class, 1));
			}
			return list;
		}

		private void feedback(String feedback, boolean result, String message, Object o) {
			try {
				put(new Tuple(feedback, userName, kitchenName, true, new Tuple(result, message, o)), Self.SELF);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
