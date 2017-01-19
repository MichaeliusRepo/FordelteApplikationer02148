package classes;

/* 02148 Introduction to Coordination in Distributed Applications
*  19. Januar 2017
*  Team 9 - Dinner Club
*	- Alexander Kristian Armstrong, s154302
*	- Michael Atchapero,  s143049
*	- Mathias Ennegaard Asmussen, s154219
*	- Emilie Isabella Dahl, s153762
*	- Jon Ravn Nielsen, s136448
*/

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