package ch.zhaw.it.pm3.unipoly;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

@Component
public class UnipolyApp {

	private UnipolyPhase phase;
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
		WAITING, ROLLING, BUY_PROPERTY, TURN, DETENTION, GO_DETENTION, ENDGAME, SHOWCARD, QUIZTIME, JUMP,
		NOT_ENOUGH_MONEY, GO, VISIT, FREECARD, RECESS, INDEBT, DEBTFREE, STILLDEBT, BANKRUPT
	}

	// UnipolyApp Constructor
	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		players = new ArrayList<>();
		cards = Config.getChanceCards();
		Collections.shuffle(cards);
	}

	/*------ GET functions ------------------------------------------------------------------*/
	public FieldProperty getcurrentFieldProperty() { return currentFieldProperty; }
	public Bank getBank() { return bank; }
	public Board getBoard() { return board; }
	public UnipolyPhase getPhase() { return phase; }
	public ArrayList<Player> getPlayers() { return players;	}
	public Player getCurrentPlayer() { return currentPlayer; }
	public int getFirstDice() { return firstDice; }
	public int getSecondDice() { return secondDice; }
	public boolean isRolledPash() { return rolledPash; }
	public String getCurrentCardText() { return currentCardText; }

	/*------ SET functions -----------------------------------------------------------------*/
	public void setRolledPash(boolean rolledPash) {
		this.rolledPash = rolledPash;
	}

	/*------ Function to configure the Game -------------------------------------------------*/
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

	// Initializing a new Player
	private void initializePlayer(String name, TokenType token) throws FieldIndexException {
		Player player = new Player(name, token);
		player.getToken().moveTo(0);
		player.index = players.size();
		players.add(player);
		player.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(0));
	}

	// Start a new Game
	public void start(Gamemode mode, int npcnum) throws FieldIndexException {
		// If SinglePlayer we have to create "npcnum" NPC Players
		if (Gamemode.SINGLE == mode) {
			if (npcnum >= 1)
				initializePlayer("NPC1", TokenType.NPCI);
			if (npcnum >= 2)
				initializePlayer("NPC2", TokenType.NPCII);
			if (npcnum >= 3)
				initializePlayer("NPC3", TokenType.NPCIII);
		}
		// First Player which gets to play
		currentPlayer = players.get(0);
		phase = UnipolyPhase.WAITING;
	}

	/*------ rolling Dices and moving Players -------------------------------------------------*/
	public void rollDice(int firstDice) throws FieldIndexException {
		phase = UnipolyPhase.ROLLING;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		// After Rolling the second dice, move the Player accordingly
		movePlayerBy(this.firstDice + secondDice);
	}

	// Move Player relative by an amount
	private void movePlayerBy(int rolledValue) throws FieldIndexException {
		currentPlayer.getToken().moveBy(rolledValue);
		currentPlayer.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(currentPlayer.getToken().getCurrFieldIndex()));
	}

	// Move Player direktly to a wished field
	private void movePlayerTo(int FieldIndex) throws FieldIndexException {
		currentPlayer.getToken().moveTo(FieldIndex);
		currentPlayer.getToken().setCurrentFieldLabel(board.getFieldTypeAtIndex(currentPlayer.getToken().getCurrFieldIndex()));
	}

	// Player landed on a jump field and wishes to jump to a certain field
	public void jumpPlayer(int FieldIndex) throws FieldIndexException {
		//currentPlayer.transferMoneyTo(bank, 100);
		movePlayerTo(FieldIndex);
	}

	/*------ Specific Field functions -----------------------------------------------------*/
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
					// TODO: currentPlayer.transferMoneyTo(Owner player, Double amount)
				case RECEIVEMONEY:
					// TODO: player.transferMoneyTo(currentPlayer, Double amount)
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
			// bank.transferMoneyTo(currentPlayer, 400);
			phase = UnipolyPhase.GO;
		}
	}

	private void checkIfOverStart() {
		if (currentPlayer.getToken().getPrevFieldIndex() > currentPlayer.getToken().getCurrFieldIndex()) {
			// bank.transferMoneyTo(currentPlayer, 200);
		}
	}

	/*------ Property Marketplace -------------------------------------------------*/
	// TODO: buyProperty()
	public void buyProperty(int currentFieldIndex) throws FieldIndexException {
		board.getFieldPropertyAtIndex(currentFieldIndex).setOwnerIndex(currentPlayer.index);
		// geld abziehen und so
	}

	// TODO: sellProperty()
	public void sellProperty(int currentFieldIndex) {
	}

	// TODO: landedOnOwnedProperty()
	public void landedOnOwnedProperty(int currentFieldIndex) {
	}

	// TODO: landedOnMyProperty()
	public void landedOnMyProperty(int currentFieldIndex) {
	}

	// TODO: payOfDebt()
	public void payOffDebt(int FieldIndex) {
		sellProperty(FieldIndex);
		/*
		if(currentPlayer.setandcheckDebt(currentPlayer.getDebtor(), currentPlayer.getDebt())) {
			if(currentPlayer.setandgetPropertyOwned() > 0){
				phase = UnipolyPhase.STILLDEBT;
			} else {
				phase = UnipolyPhase.BANKRUPT;
			}
		} else {
			phase = UnipolyPhase.DEBTFREE;
		}
		*/
	}


	/*------ end and start new turn -------------------------------------------------*/
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

	/*------ Detention related funtions ---------------------------------------------------------------------*/
	public void rollTwoDice() {
		phase = UnipolyPhase.ROLLING;
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
			// currentPlayer.transferMoneyTo(bank, RANSOM);
			leaveDetention();
		} else {
			// currentPlayer.setandcheckDebt(bank, RANSOM);
			phase = UnipolyPhase.INDEBT;
			switchPlayer();
		}
	}

	public void leaveDetention() {
		phase = UnipolyPhase.WAITING;
		currentPlayer.outDetention();
	}

	/*------ ****** ---------------------------------------------------------------------*/

}