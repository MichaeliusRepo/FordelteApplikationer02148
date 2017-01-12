package classes;

public enum EDayData {
	DAY(0), MONTH(1), YEAR(2), EXTRA(3);
//
	private final int id;

	EDayData(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}