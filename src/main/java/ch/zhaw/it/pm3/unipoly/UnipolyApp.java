package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.HashMap;


@Component
public class UnipolyApp {

	private UnipolyPhase phase;
	private final ArrayList<Player> players;
	private final Bank bank;
	private final Board board;
	private final ArrayList<ChanceCards> cards;
	private Player currentPlayer;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;
	private Field currentField;
	private String displayMessage = "";
	private String gameoverString = "";

	private static final Logger unipolyMcLogger = LogManager.getLogger(Controller.class);

	enum Gamemode {
		SINGLE, MULTI
	}

	enum UnipolyPhase {
		SHOWANDSWITCH, WAITING, ROLLING, BUY_PROPERTY, DETENTION, SHOWCARD, QUIZTIME, JUMP,
		INDEBT, GAMEOVER, ERROR
	}

	// UnipolyApp Constructor
	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		bank.setownedModuls(new HashMap<>(board.getProperties()));
		players = new ArrayList<>();
		cards = Config.getChanceCards();
		Collections.shuffle(cards);
	}

	/*------ GET functions ------------------------------------------------------------------*/
	public Field getcurrentField() { return currentField; }
	public Bank getBank() { return bank; }
	public Board getBoard() { return board; }
	public UnipolyPhase getPhase() { return phase; }
	public ArrayList<Player> getPlayers() { return players; }
	public Player getCurrentPlayer() { return currentPlayer; }
	public int getFirstDice() { return firstDice; }
	public int getSecondDice() { return secondDice; }
	public boolean isRolledPash() { return rolledPash; }
	public String getdisplayMessage() { return displayMessage; }
	public String getgameoverString() { return gameoverString; }

	public void setCurrentPlayer(Player player) {
		this.currentPlayer = player;
	}

	public void setCurrentField(Field currentField) {
		this.currentField = currentField;
	}

	/*------ Function to configure the Game -------------------------------------------------*/
	// Add a new Player to the Game
	public void join(String name, Token.TokenType token) throws FieldIndexException {
		checkIfPlayernameAlreadyExists(name, token);
		initializePlayer(name, token);
	}

	private void checkIfPlayernameAlreadyExists(String name, Token.TokenType token) {
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
	private void initializePlayer(String name, Token.TokenType token) throws FieldIndexException {
		Player player = new Player(players.size(), name, token);
		player.getToken().moveTo(0);
		players.add(player);
	}

	// Start a new Game
	public void start(Gamemode mode, int npcnum) throws FieldIndexException {
		// If SinglePlayer we have to create "npcnum" NPC Players
		if (Gamemode.SINGLE == mode) {
			if (npcnum >= 1)
				initializePlayer("NPC1", Token.TokenType.NPCI);
			if (npcnum >= 2)
				initializePlayer("NPC2", Token.TokenType.NPCII);
			if (npcnum >= 3)
				initializePlayer("NPC3", Token.TokenType.NPCIII);
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
		currentField = board.getFieldAtIndex(currentPlayer.getToken().getCurrFieldIndex());
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " hat eine " + firstDice + " und " + secondDice
					+ " gewürfelt.";
			checkFieldOptions();
		}
	}

	// Move Player direktly to a wished field
	private void movePlayerTo(int FieldIndex) throws FieldIndexException {
		currentPlayer.getToken().moveTo(FieldIndex);
		currentField = board.getFieldAtIndex(FieldIndex);
	}

	// Player landed on a jump field and wishes to jump to a certain field
	public void jumpPlayer(int FieldIndex) throws FieldIndexException {
		currentPlayer.transferMoneyTo(bank, 100);
		movePlayerTo(FieldIndex);
	}

	/*------ Specific Field functions -----------------------------------------------------*/
	public void checkFieldOptions() throws FieldIndexException {
		switch (currentField.getLabel()) {
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
		if (currentField.getLabel() == Config.FieldLabel.DETENTION) {
			if (currentPlayer.getFreeCard()) {
				currentPlayer.setFreeCard(false);
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " wurde beim plagieren erwischt und musste deshalb zur Schuldirektorin!<br>Irgendwie konnte "
							+ currentPlayer.getName() + " sich aus der Situation rausreden.";
				} else {
					displayMessage = "Du wurdest beim plagieren erwischt und musst deshalb zur Schuldirektorin!<br>Du warnst sie das wenn sie dich von der Schule schmeist, du ihr Geheimnis rumerzählst.<br>Sie lässt dich sofort gehen.";
				}
				phase = UnipolyPhase.SHOWANDSWITCH;
			} else {
				currentPlayer.goDetention();
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " wurde beim plagieren erwischt und musste deshalb zur Schuldirektorin!";
				} else {
					displayMessage = "Du wurdest beim plagieren erwischt und musst deshalb zur Schuldirektorin!";
				}
				phase = UnipolyPhase.SHOWANDSWITCH;
			}
		}
	}

	private void playerIsOnGoZnueniPause() throws FieldIndexException {
		if (currentField.getLabel() == Config.FieldLabel.RECESS) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist in die Znüni Pause.";
			} else {
				displayMessage = "Znüni Zeit. Ruh dich etwas aus:<br>Trink einen Kaffee und iss ein Sandwich!";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
	}

	private void playerIsOnPropertyField() throws FieldIndexException {
		FieldProperty currentField = (FieldProperty) this.currentField;
		if (currentField.isOwnerBank()) {
			if (currentPlayer.getMoney() >= currentField.getPropertyCost()) {
				// TODO: NPC Logic
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName() + " gelandet und kauft das Modul.<br>";
					buyProperty();
				} else {
					phase = UnipolyPhase.BUY_PROPERTY;
				}
			} else {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " hast leider nicht genug Geld für eine Aktion!<br>Die Runde geht an den nächsten Spieler..";
				} else {
					displayMessage = "Du hast leider nicht genug Geld für eine Aktion!<br>Die Runde geht automatisch an den nächsten Spieler.";
				}
				phase = UnipolyPhase.SHOWANDSWITCH;
			}
		} else if (currentField.getOwnerIndex() == currentPlayer.getIndex()) {
			landedOnMyProperty();
		} else {
			landedOnOwnedProperty();
		}
	}

	private void playerIsOnVisit() throws FieldIndexException {
		if (currentField.getLabel() == Config.FieldLabel.VISIT) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist auf dem 'Nur zu Besuch' Feld gelandet.";
			} else {
				displayMessage = "Zum Glück nur zu Besuch im Rektorat.";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
	}

	private void playerIsOnChanceField() throws FieldIndexException {
		if (currentField.getLabel() == Config.FieldLabel.CHANCE) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist auf einem Chance Feld gelandet.";
				readCard();
			} else {
				displayMessage = "Du bist auf einem Chance Feld gelandet! Du musst eine Chance Karte ziehen!";
				phase = UnipolyPhase.SHOWCARD;
			}
		}
	}

	public void readCard() {
		cards.get(0);
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " zieht die Karte:<br>" + cards.get(0).getText();
		} else {
			displayMessage = cards.get(0).getText();
		}
		switch (cards.get(0).getCardType()) {
			case TODETENTION:
				currentPlayer.goDetention();
			case PAYMONEY:
				currentPlayer.transferMoneyTo(bank, cards.get(0).getAmount());
			case RECEIVEMONEY:
				bank.transferMoneyTo(currentPlayer, cards.get(0).getAmount());
			case DETENTIONFREECARD:
				currentPlayer.setFreeCard(true);
		}
		cards.add(cards.get(0));
		cards.remove(0);
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	private void playerIsOnJumpField() throws FieldIndexException {
		final int COST_FOR_JUMP = 100;
		if (currentField.getLabel() == Config.FieldLabel.JUMP) {
			if (currentPlayer.getMoney() >= COST_FOR_JUMP) {
				// TODO: NPC Logic
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " ist auf einem Jump Feld gelandet, will aber hier bleiben.";
					phase = UnipolyPhase.SHOWANDSWITCH;
				} else {
					phase = UnipolyPhase.JUMP;
				}
			} else {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " hast leider nicht genug Geld für eine Aktion!<br>Die Runde geht an den nächsten Spieler..";
				} else {
					displayMessage = "Du hast leider nicht genug Geld für eine Aktion!<br>Die Runde geht automatisch an den nächsten Spieler.";
				}
				phase = UnipolyPhase.SHOWANDSWITCH;
			}
		}
	}

	private void playerIsOnGoField() throws FieldIndexException {
		if (currentField.getLabel() == Config.FieldLabel.GO) {
			bank.transferMoneyTo(currentPlayer, 400);
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName()
						+ " ist auf Start gelandet und bekommt deshalb das doppelte Honorar!";
			} else {
				displayMessage = "Weil du auf Start gelandet bist, bekommst du das doppelte Honorar!";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
	}

	private void checkIfOverStart() {
		if (currentPlayer.getToken().getPrevFieldIndex() > currentPlayer.getToken().getCurrFieldIndex()) {
			bank.transferMoneyTo(currentPlayer, 200);
		}
	}

	/*------ Property Marketplace -------------------------------------------------*/
	public void buyProperty() {
		currentPlayer.buyPropertyFrom(bank, currentPlayer.getToken().getCurrFieldIndex());
		displayMessage += "Das Modul gehört nun " + currentPlayer.getName();
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	private void sellProperty(int FieldIndex, Owner player) throws FieldIndexException {
		player.buyPropertyFrom(currentPlayer, FieldIndex);
		// TODO: Reset FieldLevel and Group Modul Levels
	}

	// player landed on owned Land
	private void landedOnOwnedProperty() throws FieldIndexException {
		if (currentPlayer.payRent(players.get(((FieldProperty) currentField).getOwnerIndex()),
				(FieldProperty) currentField)) {
			displayMessage = "Du kannst dir die Miete nicht leisten, Verkaufe deine Module um deine Schulden zurückzahlen zu können.";
			phase = UnipolyPhase.INDEBT;
		} else {
			// TODO: Quizfunction
			displayMessage = "";
			phase = UnipolyPhase.QUIZTIME;
		}
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
	}

	// TODO: Player landed on his own Modul
	private void landedOnMyProperty() throws FieldIndexException {
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
		displayMessage = "ModulUpgrade!!";
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	// TODO: payOfDebt(), Input is an array of fieldindexes the player wants to sell
	// to the debtor
	public void payOffDebt(int[] FieldIndexes) throws FieldIndexException {
		if (currentPlayer.getDebtor().isBank()) {
			for (int i = 0; i < FieldIndexes.length; i++) {
				sellProperty(FieldIndexes[i], bank);
			}
		} else {
			for (int i = 0; i < FieldIndexes.length; i++) {
				// TODO: transferFieldTo(Owner owner, int fieldIndex)
				// TODO: Reset FieldLevel and Group Modul Levels
				// Payoff Debt in PropertyValue
			}
		}
		/*
		 * if(currentPlayer.setandcheckDebt(currentPlayer.getDebtor(),
		 * currentPlayer.getDebt())) { if(currentPlayer.setandgetPropertyOwned() == 0){
		 * loser.setBankrupt(); GameOver(); } else { phase = UnipolyPhase.DEBTFREE; }
		 */
		switchPlayer();
	}

	/*------ end and start new turn -------------------------------------------------*/
	public void switchPlayer() throws FieldIndexException {
		displayMessage = "";
		if (currentPlayer.getIndex() == players.size() - 1)
			currentPlayer = players.get(0);
		else
			currentPlayer = players.get(currentPlayer.getIndex() + 1);

		if (currentPlayer.inDetention()) {
			phase = UnipolyPhase.DETENTION;
		} else {
			phase = UnipolyPhase.WAITING;
			if (currentPlayer.isNPC()) {
				displayMessage = currentPlayer.getName() + " war an der Reihe.";
				rollDice(new Random().nextInt(6) + 1);
			}
		}
	}

	// TODO: Calculate Winner and Display it
	private void GameOver() {
		gameoverString = "<h1>GAME OVER</h1>";
		ArrayList<Owner> ranking = new ArrayList<>(players);
		Collections.sort(ranking);
		for(Owner player: ranking){
			gameoverString += "<p>"+ (ranking.indexOf(player) + 1) +".Place " + player.getName() + ", " + player.getWealth() + "</p>";
		}
		phase = UnipolyPhase.GAMEOVER;
	}


	/*------ Detention related funtions ---------------------------------------------------------------------*/
	public void rollTwoDice() {
		phase = UnipolyPhase.ROLLING;
		rolledPash = false;
		firstDice = new Random().nextInt(6) + 1;
		secondDice = new Random().nextInt(6) + 1;
		if (firstDice == secondDice) {
			rolledPash = true;
		} else {
			currentPlayer.decreaseleftTimeInDetention();
		}
	}

	public void payDetentionRansom() {
		final int RANSOM = 100;
		if (currentPlayer.setandcheckDebt(bank, RANSOM)) {
			displayMessage = "Du hast nicht genug Geld um Sie zu bestechen, also leihst du dir was von der Bank.";
			phase = UnipolyPhase.INDEBT;
		} else {
			phase = UnipolyPhase.WAITING;
			leaveDetention();
		}
	}

	public void leaveDetention() {
		phase = UnipolyPhase.WAITING;
		currentPlayer.outDetention();
	}

	/*------ ****** ---------------------------------------------------------------------*/

}