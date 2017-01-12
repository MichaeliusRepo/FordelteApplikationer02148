package classes;

public enum ECommand {
	COMMAND(0), USERNAME(1), KITCHEN(2), DATA(3);
	//

	private final int id;

	ECommand(int id) {
		this.id = id;
	}

	public int getValue() {
		return id;
	}
}