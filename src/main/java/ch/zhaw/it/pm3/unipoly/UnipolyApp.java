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

	enum UnipolyPhase {
		WAITING,
		ROLLING,
		BUY_PROPERTY,
		TURN,
		JAILED,
		ENDGAME,
		SHOWCARD,
		QUIZTIME,
		JUMP,
		GO
	}

	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		players = new ArrayList<>();
		cards = Config.getChanceCards();
		Collections.shuffle(cards);
	}

	public Board getBoard() {
		return board;
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
	public String getCurrentCardText(){
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
	public void start(Gamemode mode) throws FieldIndexException {
		if (Gamemode.SINGLE == mode) {
			initializePlayer("NPC", TokenType.NPC);
		}
		currentPlayer = players.get(0);
	}

	public void rollDice() {
		firstDice = new Random().nextInt(6) + 1;
		secondDice = new Random().nextInt(6) + 1;
		if(firstDice == secondDice) rolledPash = true;
	}

	public void rollDice(int firstDice) throws FieldIndexException {
		phase = UnipolyPhase.ROLLING;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		movePlayer(this.firstDice + secondDice);
	}

	public void movePlayer(int rolledValue) throws FieldIndexException {
		currentPlayer.getToken().moveBy(rolledValue);
		currentPlayer.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(currentPlayer.getToken().getCurrFieldIndex()));
	}

	public void checkFieldOptions() throws FieldIndexException {
		switch(currentPlayer.getToken().getCurrentFieldLabel()) {
			case PROPERTY:
				playerIsOnPropertyField();
			case CHANCE:
				playerIsOnChanceField();
			case JUMP:
				playerIsOnJumpField();
			case GO:
				playerIsOnGoField();
			default:
				checkIfOverStart();
		}
	}

	public void playerIsOnPropertyField() {
		int NO_OWNER = -1;
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if(board.getPropertyOwner(currentFieldIndex) == NO_OWNER &&
				currentPlayer.getMoney() > board.getCostFromProperty(currentFieldIndex)) {
			phase = UnipolyPhase.BUY_PROPERTY;
		}
	}

	public void playerIsOnChanceField() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.CHANCE) {
			phase = UnipolyPhase.SHOWCARD;
			cards.get(0);
			currentCardText = cards.get(0).getText();
			switch(cards.get(0).getCardType()){
				case TODETENTION:
					currentPlayer.jail();
				case PAYMONEY:
					//todo geld übertragen
				case RECEIVEMONEY:
					//todo geld übertragen
				case DETENTIONFREECARD:
					currentPlayer.setJailCard(true);
			}
			cards.add(cards.get(0));
			cards.remove(0);
		}
	}

	public void playerIsOnJumpField() throws FieldIndexException {
		int currentFieldIndex = currentPlayer.getToken().getCurrFieldIndex();
		final int COST_FOR_JUMP = 100;
		if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.JUMP &&
				currentPlayer.getMoney() > COST_FOR_JUMP) {
			phase = UnipolyPhase.JUMP;
		}
	}

	public void playerIsOnGoField() {
		// Bank gives Player 400CHF;
	}

	public void buyProperty(boolean buy, int currentFieldIndex) throws FieldIndexException {
		if(buy) {
			board.getFieldPropertyAtIndex(currentFieldIndex).setOwnerIndex(currentPlayer.index);
			// geld abziehen und so
		} else {
			switchPlayer();
		}
	}

	public void switchPlayer() {
		if(currentPlayer.index == players.size() - 1) currentPlayer = players.get(0);
		else currentPlayer = players.get(currentPlayer.index + 1);
		phase = UnipolyPhase.WAITING;
	}

	private void checkIfOverStart() {
		if(currentPlayer.getToken().getPrevFieldIndex() > currentPlayer.getToken().getCurrFieldIndex()) {
			//Bank pay player 200 CHF
		}
	}
}
