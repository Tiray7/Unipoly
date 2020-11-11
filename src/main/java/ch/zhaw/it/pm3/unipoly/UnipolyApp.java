package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Component;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Component
public class UnipolyApp {

	private UnipolyPhase phase = UnipolyPhase.WAITING;
	private ArrayList<Player> players;
	private Player currentPlayer;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;
	private Bank bank;
	private Board board;
	private ArrayList<ChanceCards> cards;
	private String currentCardText;
	private FieldProperty currentFieldProperty;

	enum UnipolyPhase {
		WAITING, ROLLINGONE, ROLLINGTWO, BUY_PROPERTY, TURN, DETENTION, GO_DETENTION, ENDGAME, SHOWCARD, QUIZTIME, JUMP,
		NOT_ENOUGH_MONEY, GO, VISIT, FREECARD, RECESS
	}

	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		players = new ArrayList<>();
		cards = Config.getChanceCards();
		Collections.shuffle(cards);
	}

	public FieldProperty getcurrentFieldProperty() {
		return currentFieldProperty;
	}

	public Bank getBank() {
		return bank;
	}

	public UnipolyPhase getPhase() {
		return phase;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public Player getCurrentPlayer() {
		return currentPlayer;
	}

	public int getFirstDice() {
		return firstDice;
	}

	public int getSecondDice() {
		return secondDice;
	}

	public boolean isRolledPash() {
		return rolledPash;
	}

	public void setRolledPash(boolean rolledPash) {
		this.rolledPash = rolledPash;
	}

	public String getCurrentCardText() {
		return currentCardText;
	}

	// Add a new Player to the Game
	public void join(String name, TokenType token) throws FieldIndexException {
		checkIfPlayernameAlreadyExists(name, token);
		initializePlayer(name, token);
	}

	private void checkIfPlayernameAlreadyExists(String name, TokenType token) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				throw new IllegalArgumentException("Player name already exists.");
			}
			if (player.getToken().getType() == token) {
				throw new IllegalArgumentException("Player token already exists.");
			}
		}
	}

	private void initializePlayer(String name, TokenType token) throws FieldIndexException {
		Player player = new Player(name, token);
		player.getToken().moveTo(0);
		player.index = players.size();
		players.add(player);
		player.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(0));
	}

	// Start a new Game
	public void start(Gamemode mode, int npcnum) throws FieldIndexException {
		if (Gamemode.SINGLE == mode) {
			if (npcnum >= 1)
				initializePlayer("NPC1", TokenType.NPCI);
			if (npcnum >= 2)
				initializePlayer("NPC2", TokenType.NPCII);
			if (npcnum >= 3)
				initializePlayer("NPC3", TokenType.NPCIII);
		}
		currentPlayer = players.get(0);
	}

	public void rollDice(int firstDice) throws FieldIndexException {
		phase = UnipolyPhase.ROLLINGONE;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		movePlayerBy(this.firstDice + secondDice);
	}

	private void movePlayerBy(int rolledValue) throws FieldIndexException {
		currentPlayer.getToken().moveBy(rolledValue);
		currentPlayer.getToken()
				.setCurrentFieldLabel(board.getFieldTypeAtIndex(currentPlayer.getToken().getCurrFieldIndex()));
	}

	private void movePlayerTo(int FieldIndex) throws FieldIndexException {
		currentPlayer.getToken().moveTo(FieldIndex);
		currentPlayer.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(currentPlayer.getToken().getCurrFieldIndex()));
	}

	public void jumpPlayer(int FieldIndex) throws FieldIndexException {
		// TODO: Player has to pay 100 CHF
		movePlayerTo(FieldIndex);
	}

	public void checkFieldOptions() throws FieldIndexException {
		switch (currentPlayer.getToken().getCurrentFieldLabel()) {
			case PROPERTY:
				playerIsOnPropertyField();
			case CHANCE:
				playerIsOnChanceField();
			case JUMP:
				playerIsOnJumpField();
			case GO:
				playerIsOnGoField();
			case VISIT:
				playerIsOnVisit();
			case DETENTION:
				playerIsOnGoToDetention();
			case RECESS:
				playerIsOnGoZnueniPause();
			default:
				checkIfOverStart();
		}
	}

	private void playerIsOnGoToDetention() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.DETENTION) {
			if (currentPlayer.getFreeCard()) {
				currentPlayer.setFreeCard(false);
				phase = UnipolyPhase.FREECARD;
			} else {
				currentPlayer.goDetention();
				phase = UnipolyPhase.GO_DETENTION;
			}
		}
	}

	private void playerIsOnGoZnueniPause() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.RECESS) {
			phase = UnipolyPhase.RECESS;
		}
	}

	private void playerIsOnPropertyField() throws FieldIndexException {
		int NO_OWNER = -1;
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		currentFieldProperty = board.getFieldPropertyAtIndex(currentFieldIndex);
		if (board.getPropertyOwner(currentFieldIndex) == NO_OWNER) {
			if (currentPlayer.getMoney() >= board.getCostFromProperty(currentFieldIndex)) {
				phase = UnipolyPhase.BUY_PROPERTY;
			} else {
				phase = UnipolyPhase.NOT_ENOUGH_MONEY;
			}
		}
	}

	private void playerIsOnVisit() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.VISIT) {
			phase = UnipolyPhase.VISIT;
		}
	}

	private void playerIsOnChanceField() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.CHANCE) {
			phase = UnipolyPhase.SHOWCARD;
			cards.get(0);
			currentCardText = cards.get(0).getText();
			switch(cards.get(0).getCardType()){
				case TODETENTION:
					currentPlayer.goDetention();
				case PAYMONEY:
					//todo geld übertragen
				case RECEIVEMONEY:
					//todo geld übertragen
				case DETENTIONFREECARD:
					currentPlayer.setFreeCard(true);
			}
			cards.add(cards.get(0));
			cards.remove(0);
		}
	}

	private void playerIsOnJumpField() throws FieldIndexException {
		final int COST_FOR_JUMP = 100;
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.JUMP) {
			if (currentPlayer.getMoney() >= COST_FOR_JUMP) {
				phase = UnipolyPhase.JUMP;
			} else {
				phase = UnipolyPhase.NOT_ENOUGH_MONEY;
			}
		}
	}

	private void playerIsOnGoField() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if (board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.GO) {
			phase = UnipolyPhase.GO;
		}
	}

	public void buyProperty(int currentFieldIndex) throws FieldIndexException {
		board.getFieldPropertyAtIndex(currentFieldIndex).setOwnerIndex(currentPlayer.index);
		// geld abziehen und so
	}

	public void switchPlayer() {
		if (currentPlayer.index == players.size() - 1)
			currentPlayer = players.get(0);
		else
			currentPlayer = players.get(currentPlayer.index + 1);

		if (currentPlayer.inDetention())
			phase = UnipolyPhase.DETENTION;
		else
			phase = UnipolyPhase.WAITING;
	}

	private void checkIfOverStart() {
		if (currentPlayer.getToken().getPrevFieldIndex() > currentPlayer.getToken().getCurrFieldIndex()) {
			// TODO: Bank pay player 200 CHF
		}
	}

	/*------ Detention related funtions ---------------------------------------------------------------------*/
	public void rollTwoDice() {
		phase = UnipolyPhase.ROLLINGTWO;
		setRolledPash(false);
		firstDice = new Random().nextInt(6) + 1;
		secondDice = new Random().nextInt(6) + 1;
		if (firstDice == secondDice) {
			setRolledPash(true);
		} else {
			currentPlayer.decreaseleftTimeInDetention();
		}
	}

	public void payDetentionRansom() {
		final int RANSOM = 100;
		if (currentPlayer.getMoney() >= RANSOM) {
			// TODO: player has to pay 100
			leaveDetention();
		} else {
			// TODO: Player has no money
			switchPlayer();
		}
	}

	public void leaveDetention() {
		phase = UnipolyPhase.WAITING;
		currentPlayer.outDetention();
	}

}
