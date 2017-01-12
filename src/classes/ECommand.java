package classes;

public enum ECommand {
	COMMAND(0), USERNAME(1), KITCHEN(2), FEEDBACK(3), DATA(4);
	//

	private final int id;

	ECommand(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}