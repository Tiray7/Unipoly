package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;
import java.util.HashMap;

/**
 * Represents the Unipoly application - handles the main part of the game logic
 * and connects all the pieces together.
 * Has the {@link Component} tag for simple initialization into the spring application context.
 */
@Component
public class UnipolyApp {

	private UnipolyPhase phase;
	private final ArrayList<Player> players;
	private final Bank bank;
	private final Board board;
	private final ArrayList<ChanceCards> cards;
	private final Hashtable<Integer, Question> questions;
	private Player currentPlayer;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;
	private Field currentField;
	private String displayMessage = "";
	private String gameoverString = "";

	private static final Logger unipolyMcLogger = LogManager.getLogger(UnipolyApp.class);

	/**
	 * gamemode enum to find which mode to play , singleplayer or multiPlayer
	 */
	enum Gamemode {
		SINGLE, MULTI
	}

	/***
	 * UnipolyPhase to define which phase in the game is
	 */
	enum UnipolyPhase {
		SHOWANDSWITCH, WAITING, ROLLING, BUY_PROPERTY, DETENTION,
		SHOWCARD, QUIZTIME, JUMP, INDEBT, GAMEOVER, ERROR
	}

	/***
	 * UnipolyApp class constructor
	 */
	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		bank.setownedModuls(new HashMap<>(board.getProperties()));
		players = new ArrayList<>();
		cards = Config.getChanceCards();
		Collections.shuffle(cards);
		questions = Config.getQuestionCards();
		unipolyMcLogger.log(Level.DEBUG, "UnipolyApp initialized.");
	}

	/**
	 * "Unused" getters are needed for the javascript-frontend and or testing-purposes
	 */
	public Field getcurrentField() { return currentField; }
	public Bank getBank() { return bank; }
	public Board getBoard() { return board; }
	public UnipolyPhase getPhase() { return phase; }
	public ArrayList<Player> getPlayers() { return players; }
	public Hashtable<Integer, Question> getQuestions() { return questions; }
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

	/***
	 * Joins a new Player to the Unipoly application-context
	 * 
	 * @param name  name of the {@link Player}
	 * @param token {@link Token.TokenType}
	 * @throws FieldIndexException gets thrown if the field the player gets drawn to is invalid
	 */
	public void join(String name, Token.TokenType token) throws FieldIndexException {
		checkIfPlayerNameAlreadyExists(name, token);
		initializePlayer(name, token);
		unipolyMcLogger.log(Level.DEBUG, "Player: " + name + " joined!");
	}

	/***
	 * Checks if a player name already exists and
	 * @throws IllegalArgumentException if either the name or the token aren't available anymore
	 *  @param name  {@link Player} name
	 * @param token {@link Token.TokenType}
	 */
	private void checkIfPlayerNameAlreadyExists(String name, Token.TokenType token) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				unipolyMcLogger.log(Level.DEBUG, "Player: " + name + " already exists --> IllegalArgumentException thrown");
				throw new IllegalArgumentException("Player name already exists.");
			}
			if (player.getToken().getType() == token) {
				unipolyMcLogger.log(Level.DEBUG, "Token: " + token.name() + " already exists --> IllegalArgumentException thrown");
				throw new IllegalArgumentException("Player token already exists.");
			}
		}
	}

	/***
	 * Initializing a new {@link Player}
	 * 
	 * @param name  {@link Player} name
	 * @param token {@link Token.TokenType}
	 */
	private void initializePlayer(String name, Token.TokenType token) {
		Player player = new Player(players.size(), name, token);
		player.getToken().moveTo(0);
		players.add(player);
		unipolyMcLogger.log(Level.DEBUG, "Player: " + name + " initialized!");
	}

	/***
	 * Starts a new Game - initialize NPC's if needed and sets {@link Player} currentPlayer
	 * 
	 * @param mode   which {@link Gamemode} - single or multi
	 * @param npcnum if singleplayer we have to create "npcnum" x NPC players
	 */
	public void start(Gamemode mode, int npcnum) {
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
		unipolyMcLogger.log(Level.DEBUG, "Game started with current player: " + currentPlayer.getName());
	}

	/***
	 * Sets the  {@link UnipolyPhase}, {@link #firstDice}  and {@link #secondDice} (obtained with {@link Random})
	 * 
	 * @param firstDice parameter from frontend choosen by user
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	public void rollDice(int firstDice) throws FieldIndexException {
		phase = UnipolyPhase.ROLLING;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		// After Rolling the second dice, move the Player accordingly
		movePlayerBy(this.firstDice + secondDice);
		unipolyMcLogger.log(Level.DEBUG, "Dice rolled by: " + currentPlayer.getName());
	}

	/***
	 * Move {@link Player} relative by an amount
	 * 
	 * @param rolledValue amount to move by
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 1 - 36
	 */
	private void movePlayerBy(int rolledValue) throws FieldIndexException {
		currentPlayer.getToken().moveBy(rolledValue);
		currentField = board.getFieldAtIndex(currentPlayer.getToken().getCurrFieldIndex());
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " hat eine " + firstDice + " und " + secondDice
					+ " gewürfelt.";
			checkFieldOptions();
		}
		unipolyMcLogger.log(Level.DEBUG, "Player: " + currentPlayer.getName() + " moved by: " + rolledValue);
	}

	/***
	 * Move Player directly to a chosen field
	 * 
	 * @param fieldIndex has to be an integer between 1 and 36 or else
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	private void movePlayerTo(int fieldIndex) throws FieldIndexException {
		currentPlayer.getToken().moveTo(fieldIndex);
		currentField = board.getFieldAtIndex(fieldIndex);
		unipolyMcLogger.log(Level.DEBUG, "Player: " + currentPlayer.getName() + " moved to: " + fieldIndex);
	}

	/***
	 * Player landed on a jump field and wishes to jump to a certain field
	 * 
	 * @param fieldIndex has to be an integer between 1 and 36 or else
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	public void jumpPlayer(int fieldIndex) throws FieldIndexException {
		currentPlayer.transferMoneyTo(bank, 100);
		movePlayerTo(fieldIndex);
		unipolyMcLogger.log(Level.DEBUG, "Player: " + currentPlayer.getName() + " jumped to: " + fieldIndex);
	}

	/***
	 * Checks field property options according to the {@link #currentPlayer's} position
	 * 
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	public void checkFieldOptions() throws FieldIndexException {
		Config.FieldLabel currentLabel = currentField.getLabel();
		switch (currentLabel) {
			case PROPERTY:
				playerIsOnPropertyField();
				break;
			case CHANCE:
				playerIsOnChanceField();
				break;
			case JUMP:
				playerIsOnJumpField();
				break;
			case GO:
				playerIsOnGoField();
				break;
			case VISIT:
				playerIsOnVisit();
				break;
			case DETENTION:
				playerIsOnGoToDetention();
				break;
			case RECESS:
				playerIsOnGoZnueniPause();
		}
		if ((currentLabel != Config.FieldLabel.JUMP) && (currentLabel != Config.FieldLabel.DETENTION))
			checkIfOverStart();
	}

	/***
	 * Checks if the currentField is the detention field. If so the player gets to choose if he
	 * wants to take his/her getFreeCard (if there is one in his/her posession). After the evaluation the
	 * shown message gets set accordingly.
	 *
	 */
	private void playerIsOnGoToDetention() {
			if (currentPlayer.getFreeCard()) {
				currentPlayer.setFreeCard(false);
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " wurde beim plagieren erwischt und muss deshalb zur Schuldirektorin!<br>Irgendwie konnte "
							+ currentPlayer.getName() + " sich aus der Situation rausreden.";
				} else {
					displayMessage = "Du wurdest beim Plagieren erwischt und musst deshalb zur Schuldirektorin!<br>Du warnst sie, dass wenn sie dich von der Schule wirft, " +
							"du ihr Geheimnis rumerzählst.<br>Sie lässt dich sofort gehen.";
				}
			} else {
				currentPlayer.goDetention();
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " wurde beim Plagieren erwischt und musste deshalb zur Schuldirektorin!";
				} else {
					displayMessage = "Du wurdest beim Plagieren erwischt und musst deshalb zur Schuldirektorin!";
				}
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
	}

	/***
	 * Holds a {@link Player} for a round
	 *
	 */
	private void playerIsOnGoZnueniPause() {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist in die Znüni Pause.";
			} else {
				displayMessage = "Znüni Zeit. Ruh dich etwas aus:<br>Trink einen Kaffee und iss ein Sandwich!";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
	}

	/***
	 * Logic to check if a {@link Field} is available for a player to buy.
	 * 
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	private void playerIsOnPropertyField() throws FieldIndexException {
		FieldProperty currentField = (FieldProperty) this.currentField;
		if (currentField.isOwnerBank()) {
			if (currentPlayer.getMoney() >= currentField.getPropertyCost()) {
				// TODO: NPC Logic
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName()
							+ " gelandet und kauft das Modul.<br>";
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

	/***
	 * Logic to check if a player is on the VISIT field
	 *
	 */
	private void playerIsOnVisit() {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist auf dem 'Nur zu Besuch' Feld gelandet.";
			} else {
				displayMessage = "Zum Glück nur zu Besuch im Rektorat.";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
	}

	/***
	 * Checks if a {@link Player} is on a CHANCE field.
	 * Sets {@link #displayMessage} and {@link UnipolyPhase} accordingly
	 *
	 */
	private void playerIsOnChanceField() {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist auf einem Chance Feld gelandet.";
				readCard();
			} else {
				displayMessage = "Du bist auf einem Chance Feld gelandet! Du musst eine Chance Karte ziehen!";
				phase = UnipolyPhase.SHOWCARD;
			}
	}

	/***
	 * If player is a NPC card gets drawn and shown.
	 * If player is human card gets drawn after clicking in the frontend.
	 * After that sets {@link #displayMessage} and {@link UnipolyPhase} accordingly.
	 */
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
				break;
			case PAYMONEY:
				currentPlayer.transferMoneyTo(bank, cards.get(0).getAmount());
				break;
			case RECEIVEMONEY:
				bank.transferMoneyTo(currentPlayer, cards.get(0).getAmount());
				break;
			case DETENTIONFREECARD:
				currentPlayer.setFreeCard(true);
		}
		cards.add(cards.get(0));
		cards.remove(0);
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	private void playerIsOnJumpField() {
		final int COST_FOR_JUMP = 100;
			if (currentPlayer.getMoney() >= COST_FOR_JUMP) {
				// TODO: NPC Logic
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " ist auf einem Springer Feld gelandet, will aber nicht springen.";
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

	private void playerIsOnGoField() throws FieldIndexException {
			bank.transferMoneyTo(currentPlayer, 400);
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName()
						+ " ist auf Start gelandet und bekommt deshalb das doppelte Honorar!";
			} else {
				displayMessage = "Weil du auf Start gelandet bist, bekommst du das doppelte Honorar!";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
	}

	private void checkIfOverStart() {
		if (currentPlayer.getToken().getPrevFieldIndex() > currentPlayer.getToken().getCurrFieldIndex()) {
			bank.transferMoneyTo(currentPlayer, 200);
		}
	}

	/***
	 * Buys a property for current player via the {@link Player} class.
	 * Sets the {@link #displayMessage} and the {@link UnipolyPhase}
	 */
	public void buyProperty() {
		currentPlayer.buyPropertyFrom(bank, currentPlayer.getToken().getCurrFieldIndex());
		displayMessage += "Das Modul gehört nun <b>" + currentPlayer.getName() + "</b>";
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	private void sellProperty(int FieldIndex, Owner player) {
		player.buyPropertyFrom(currentPlayer, FieldIndex);
		board.getProperties().get(FieldIndex).resetLevel();
	}

	private void landedOnOwnedProperty() {
		FieldProperty currentProperty = ((FieldProperty) currentField);
		if (currentPlayer.payRent(players.get(currentProperty.getOwnerIndex()),
				(FieldProperty) currentField)) {
			if(currentPlayer.getWealth() < currentProperty.getCurrentRent()){
				GameOver();
			} else {
				displayMessage = "Du kannst dir die Miete nicht leisten, Verkaufe deine Module um deine Schulden zurückzahlen zu können.";
				phase = UnipolyPhase.INDEBT;
			}
		} else {
			phase = UnipolyPhase.QUIZTIME;
		}
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
	}

	// TODO: Player landed on his own Modul
	private void landedOnMyProperty() {
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
		displayMessage = "ModulUpgrade!!";
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	public void quizAnswer(boolean questionResult) {
		if (questionResult) {
			currentPlayer.increaseECTS(((FieldProperty) currentField).getCurrentECTSLevel());
			if(currentPlayer.isBachelor()){
				GameOver();
			}
		}
	}

	/***
	 * payOffDebt method refer to the debtor
	 * 
	 * @param FieldIndexes field index number
	 */
	public void payOffDebt(int[] FieldIndexes) {
		Owner buyer = currentPlayer.getDebtor();
		Owner debtor = buyer;
		int totalCost = 0;
		for (int fieldIndex : FieldIndexes) {
			totalCost += board.getProperties().get(fieldIndex).getPropertyCost();
		}
		if (buyer.getMoney() < totalCost) {
			buyer = bank;
		}
		for (int fieldIndex : FieldIndexes) {
			sellProperty(fieldIndex, buyer);
		}
		if (currentPlayer.setandcheckDebt(debtor, currentPlayer.getDebt())) {
			GameOver();
		} else {
			currentPlayer.setDebtor(null);
			currentPlayer.setDebt(0);
			phase = UnipolyPhase.WAITING;
		}
		/*
		 * if(currentPlayer.setandcheckDebt(currentPlayer.getDebtor(),
		 * currentPlayer.getDebt())) { if(currentPlayer.setandgetPropertyOwned() == 0){
		 * loser.setBankrupt(); GameOver(); } else { phase = UnipolyPhase.DEBTFREE; }
		 */
		// switchPlayer();
	}

	private void NPCinDebt() {
		
	}

	/***
	 * switchPlayer methode
	 * 
	 * @throws FieldIndexException
	 */
	public void switchPlayer() throws FieldIndexException {
		displayMessage = "";
		if (currentPlayer.getIndex() == players.size() - 1)
			currentPlayer = players.get(0);
		else
			currentPlayer = players.get(currentPlayer.getIndex() + 1);

		if (currentPlayer.inDetention()) {
			if (currentPlayer.isNPC()) {
				displayMessage = currentPlayer.getName() + " ist noch bei der Schuldirektorin.";
				if (currentPlayer.getleftTimeInDetention() > 0) {
					if (currentPlayer.getMoney() > 200) {
						displayMessage += currentPlayer.getName() + " besticht die Schuldirektorin.";
						payDetentionRansom();
					} else {
						displayMessage += currentPlayer.getName() + " versucht über den Schulverweis zu verhandeln.";
						rollTwoDice();
						if (rolledPash) {
							displayMessage += currentPlayer.getName() + " hat ein Pash gewürfelt und wird somit vom Nachsitzen entlassen.";
							leaveDetention();
							rollDice(new Random().nextInt(6) + 1);
						}
					}
				} else {
					displayMessage += currentPlayer.getName() + " hat schon 3mal versucht Sie zu überzeugen; Ohne Erfolg!";
					displayMessage += currentPlayer.getName() + " muss versuchen die Schuldirektorin zu bestechen.";
					payDetentionRansom();
				}
			}
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
		Player bachelor = null;
		for (Player value : players) {
			if (value.isBachelor())
				bachelor = value;
		}

		gameoverString = "<h1>GAME OVER</h1>";
		ArrayList<Owner> ranking = new ArrayList<>(players);
		Collections.sort(ranking);

		if (bachelor!=null) {
			ranking.remove(bachelor);
			ranking.add(0,bachelor);
		}

		for (int i = 0; i < ranking.size(); i++) {
			Owner player = ranking.get(i);
			gameoverString += "<p>"+ (i + 1) +".Place " + player.getName() + ", " + player.getWealth() + "</p>";
		}
		phase = UnipolyPhase.GAMEOVER;
	}

	/***
	 * Sets the {@link #firstDice} and {@link #secondDice} via the
	 * {@link Random} class. If the player rolls a pash, detention gets decreased.
	 */
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

	/***
	 * Pay off the the detention
	 */
	public void payDetentionRansom() {
		final int RANSOM = 100;
		if (currentPlayer.setandcheckDebt(bank, RANSOM)) {
			if (currentPlayer.isNPC()) { 
				NPCinDebt();
			} else {
				displayMessage = "Du hast nicht genug Geld um Sie zu bestechen, also leihst du dir was von der Bank.";
				phase = UnipolyPhase.INDEBT;
			}
		} else {
			leaveDetention();
		}
	}

	/***
	 * Leaving the Detention "on hold"
	 */
	public void leaveDetention() {
		phase = UnipolyPhase.WAITING;
		currentPlayer.outDetention();
	}
}