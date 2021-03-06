package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.LinkedList;
import java.util.Map;

import static ch.zhaw.it.pm3.unipoly.Config.COST_FOR_JUMP;
import static ch.zhaw.it.pm3.unipoly.Config.RANSOM;
import static ch.zhaw.it.pm3.unipoly.Config.GO_MONEY;

import ch.zhaw.it.pm3.unipoly.Config.TokenType;
import ch.zhaw.it.pm3.unipoly.Config.Gamemode;
import ch.zhaw.it.pm3.unipoly.Config.UnipolyPhase;


/**
 * Represents the Unipoly application - handles the main part of the game logic
 * and connects all the pieces together.
 * Has the {@link Component} tag for simple initialization into the spring web application context.
 */
@Component
public class UnipolyApp {

	private UnipolyPhase phase;
	private final ArrayList<Player> players;
	private final Bank bank;
	private final Board board;
	private final List<ChanceCards> cards;
	private final Map<Integer, Question> questions;
	private Player currentPlayer;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;
	private Field currentField;
	private String displayMessage = "";
	private String gameoverString = "";
	private static final Logger unipolyMcLogger = LogManager.getLogger(UnipolyApp.class);

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
	public Field getcurrentField() {
		return currentField;
	}

	public Bank getBank() {
		return bank;
	}

	public Board getBoard() {
		return board;
	}

	public UnipolyPhase getPhase() {
		return phase;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public Map<Integer, Question> getQuestions() {
		return questions;
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

	public String getdisplayMessage() {
		return displayMessage;
	}

	public String getgameoverString() {
		return gameoverString;
	}

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
	 * @param token {@link TokenType}
	 */
	public void join(String name, TokenType token) {
		checkIfPlayerNameAlreadyExists(name, token);
		initializePlayer(name, token);
		unipolyMcLogger.log(Level.DEBUG, "Player: {0} joined!", name);
	}

	/***
	 * Checks if a player name already exists and
	 * @throws IllegalArgumentException if either the name or the token aren't available anymore
	 *  @param name  {@link Player} name
	 * @param token {@link TokenType}
	 */
	private void checkIfPlayerNameAlreadyExists(String name, TokenType token) {
		for (Player player : players) {
			if (player.getName().equals(name)) {
				unipolyMcLogger.log(Level.DEBUG, "Player: {0} already exists --> IllegalArgumentException thrown", name);
				throw new IllegalArgumentException("Player name already exists.");
			}
			if (player.getToken().getType() == token) {
				unipolyMcLogger.log(Level.DEBUG, "Token: {0} already exists --> IllegalArgumentException thrown", token.name());
				throw new IllegalArgumentException("Player token already exists.");
			}
		}
	}

	/***
	 * Initializing a new {@link Player}
	 *
	 * @param name  {@link Player} name
	 * @param token {@link TokenType}
	 */
	private void initializePlayer(String name, TokenType token) {
		Player player = new Player(players.size(), name, token);
		player.getToken().moveTo(0);
		players.add(player);
		unipolyMcLogger.log(Level.DEBUG, "Player: {0} initialized!", name);
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
				initializePlayer("NPC1", TokenType.NPCI);
			if (npcnum >= 2)
				initializePlayer("NPC2", TokenType.NPCII);
			if (npcnum >= 3)
				initializePlayer("NPC3", TokenType.NPCIII);
		}
		// First Player which gets to play
		currentPlayer = players.get(0);
		phase = UnipolyPhase.WAITING;
		unipolyMcLogger.log(Level.DEBUG, "Game started with current player: {0}", currentPlayer.getName());
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
		unipolyMcLogger.log(Level.DEBUG, "Dice rolled by: {}", currentPlayer.getName());
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
		checkIfOverStart();
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " hat eine " + firstDice + " und " + secondDice
					+ " gewürfelt.";
			checkFieldOptions();
		}
		unipolyMcLogger.log(Level.DEBUG, "Player: {0} moved by: {1}", currentPlayer.getName(), rolledValue);
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
		if (currentPlayer.isNPC()) {
			checkFieldOptions();
		}
		unipolyMcLogger.log(Level.DEBUG, "Player: {0} moved to: {1}", currentPlayer.getName(), fieldIndex);
	}

	/***
	 * Player landed on a jump field and wishes to jump to a certain field
	 *
	 * @param fieldIndex has to be an integer between 1 and 36 or else
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	public void jumpPlayer(int fieldIndex) throws FieldIndexException {
		currentPlayer.transferMoneyTo(bank, COST_FOR_JUMP);
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " ist auf einem Springer Feld gelandet und springt zum Feld mit Index " + fieldIndex + ".";
		}
		movePlayerTo(fieldIndex);
		unipolyMcLogger.log(Level.DEBUG, "Player: {0} jumped to: {1}", currentPlayer.getName(), fieldIndex);
	}

	/***
	 * Checks field property options according to the {@link #currentPlayer's} position
	 *
	 * @throws FieldIndexException gets thrown if any value regarding the field isn't in the range of 0 - 35
	 */
	public void checkFieldOptions() throws FieldIndexException {
		switch (currentField.getLabel()) {
			case PROPERTY:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnPropertyField() gets executed");
				playerIsOnPropertyField();
				break;
			case CHANCE:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnChanceField() gets executed");
				playerIsOnChanceField();
				break;
			case JUMP:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnJumpField() gets executed");
				playerIsOnJumpField();
				break;
			case GO:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoField() gets executed");
				playerIsOnGoField();
				break;
			case VISIT:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnVisit() gets executed");
				playerIsOnVisit();
				break;
			case DETENTION:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoToDetention() gets executed");
				playerIsOnGoToDetention();
				break;
			case RECESS:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoZnueniPause() gets executed");
				playerIsOnGoZnueniPause();
		}
	}

	/***
	 * Checks if the currentField is the detention field. If so the player gets to choose if he
	 * wants to take his/her getFreeCard (if there is one in his/her possession). After the evaluation the
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
	 **/
	private void playerIsOnPropertyField() {
		FieldProperty currentField = (FieldProperty) this.currentField;
		if (currentField.isOwnerBank()) {
			if (currentPlayer.getMoney() >= currentField.getPropertyCost()) {
				if (currentPlayer.isNPC()) {
					NPCBuysProperty(currentField);
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
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " zieht die Karte:<br><i>" + cards.get(0).getText()
					+ "</i>";
		} else {
			displayMessage = "<i>" + cards.get(0).getText() + "</i>";
		}
		switch (cards.get(0).getCardType()) {
			case TODETENTION:
				currentPlayer.goDetention();
				phase = UnipolyPhase.SHOWANDSWITCH;
				break;
			case PAYMONEY:
				if (currentPlayer.setAndCheckDebt(bank, cards.get(0).getAmount())) {
					inChanceCardDebt();
				} else {
					phase = UnipolyPhase.SHOWANDSWITCH;
				}
				break;
			case RECEIVEMONEY:
				bank.transferMoneyTo(currentPlayer, cards.get(0).getAmount());
				phase = UnipolyPhase.SHOWANDSWITCH;
				break;
			case DETENTIONFREECARD:
				currentPlayer.setFreeCard(true);
				phase = UnipolyPhase.SHOWANDSWITCH;
		}
		cards.add(cards.get(0));
		cards.remove(0);
	}

	/**
	 * The player doesn't have enough money to pay for the debt. He has to sell his properties and if he still
	 * doesn't have enough money the game will end.
	 */
	private void inChanceCardDebt() {
		if (currentPlayer.setandgetBankrupt()) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName()
						+ " kann sich das nicht leisten und ist nun Bankrot.";
			} else {
				displayMessage = "Du kannst dir das nicht leisten und bist nun Bankrot.";
			}
			GameOver();
		} else {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " kann sich dies nicht leisten.";
				NPCinDebt();
			} else {
				displayMessage += "Du kannst dir dies nicht leisten. Verkaufe deine Module um deine Schulden zurückzahlen zu können.";
				phase = UnipolyPhase.INDEBT;
			}
		}
	}

	/**
	 * The current player lands on a jump field. He can pay to jump or stay at his current place.
	 *
	 * @throws FieldIndexException if the fieldIndex is out of bounds
	 */
	private void playerIsOnJumpField() throws FieldIndexException {
		if (currentPlayer.getMoney() >= COST_FOR_JUMP) {
			if (currentPlayer.isNPC() && NPCChecksMoney(COST_FOR_JUMP)) {
				NPCJumps();
			} else if (currentPlayer.isNPC()) {
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

	private void playerIsOnGoField() {
		bank.transferMoneyTo(currentPlayer, GO_MONEY);
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

	private void sellProperty(int fieldIndex, Owner player) {
		player.buyPropertyFrom(currentPlayer, fieldIndex);
		board.checkAndDecreaseRentAndECTS(board.getProperties().get(fieldIndex));
		board.getProperties().get(fieldIndex).resetLevel();
	}

	/***
	 * landedOnOwnedProperty method player lands on owned Land
	 */
	private void landedOnOwnedProperty() {
		FieldProperty currentProperty = ((FieldProperty) currentField);
		if (currentPlayer.setAndCheckDebt(players.get(currentProperty.getOwnerIndex()),
				currentProperty.getCurrentRent())) {
			if (currentPlayer.setandgetBankrupt()) {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " kann kann sich die Miete nicht leisten und ist nun Bankrot.";
				} else {
					displayMessage = "Du kannst dir die Miete nicht leisten und bist nun Bankrot.";

				}
				GameOver();
			} else {
				board.checkAndRaiseRentAndECTS(currentProperty);
				if (currentPlayer.isNPC()) {
					NPCinDebt();
				} else {
					displayMessage = "Du kannst dir die Miete nicht leisten, Verkaufe deine Module um deine Schulden zurückzahlen zu können.";
					phase = UnipolyPhase.INDEBT;
				}
			}
		} else {
			phase = UnipolyPhase.QUIZTIME;
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " muss " + currentProperty.getCurrentRent()
						+ " CHF Miete zahlen.";
				int bool = new Random().nextInt(1);
				quizAnswer(bool == 0);
			}
		}
	}

	/**
	 * The NPC lands on a property of a player. This method returns a random answer to the quiz question.
	 */
	private void landedOnMyProperty() {
		phase = UnipolyPhase.QUIZTIME;
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " muss " + ((FieldProperty) currentField).getCurrentRent()
					+ " CHF Miete zahlen.";
			int bool = new Random().nextInt(1);
			quizAnswer(bool == 0);
		}
	}

	/**
	 * If the player answers the quiz correctly this method will give the player ECTS-points.
	 *
	 * @param questionResult whether the player answered the question correctly
	 */
	public void quizAnswer(boolean questionResult) {
		if (questionResult) {
			currentPlayer.increaseECTS(((FieldProperty) currentField).getCurrentECTSLevel());
			if (currentPlayer.isBachelor()) {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " hat insgesamt 180ECTS erworben und somit den Bachelor erhalten.";
				} else {
					displayMessage = "Du hast insgesamt 180ECTS erworben und somit den Bachelor erhalten.";
				}
				GameOver();
			} else {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " hat die Frage richitg beantwortet und hat nun insgesamt " + currentPlayer.getECTS()
							+ " ECTS.";
					phase = UnipolyPhase.SHOWANDSWITCH;
				}
			}
		} else {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " hat die Frage falsch beantwortet.";
				phase = UnipolyPhase.SHOWANDSWITCH;
			}
		}
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
	}

	/***
	 * payOffDebt method refers to the debtor
	 * 
	 * @param fieldIndices field index number
	 */
	public void payOffDebt(Integer[] fieldIndices) {
		Owner Debtor = currentPlayer.getDebtor();
		int propertyValue = 0;
		for (Integer fieldIndex : fieldIndices) {
			if (Debtor.isBank()) {
				sellProperty(fieldIndex, Debtor);
			} else {
				propertyValue = board.getProperties().get(fieldIndex).getPropertyCost();
				currentPlayer.transferPropertyTo(Debtor, fieldIndex);
				currentPlayer.setDebt(Math.max(0, currentPlayer.getDebt() - propertyValue));
				board.checkAndDecreaseRentAndECTS(board.getProperties().get(fieldIndex));
				board.getProperties().get(fieldIndex).resetLevel();
			}
		}
		if (currentPlayer.setAndCheckDebt(Debtor, currentPlayer.getDebt())) {
			GameOver();
		}
	}

	/**
	 * If the NPC is in debt he has to sell his properties until he isn's anymore.
	 */
	private void NPCinDebt() {
		int selling = 0;
		List<Integer> currarr = new ArrayList<>();
		for (Map.Entry<Integer, FieldProperty> module : currentPlayer.getownedModuls().entrySet()) {
			if (selling < currentPlayer.getDebt()) {
				selling += module.getValue().getPropertyCost();
				currarr.add(module.getKey());
			}
		}
		Integer[] FieldIndexes = currarr.toArray(new Integer[currarr.size()]);
		payOffDebt(FieldIndexes);
		displayMessage += "<br>" + currentPlayer.getName()
				+ " musste Module verkaufen um seine Schulden bezahlen zu können.";
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

	/***
	 * This method switches the active player.
	 *
	 * @throws FieldIndexException if fieldIndex is out of bounds
	 */
	public void switchPlayer() throws FieldIndexException {
		displayMessage = "";
		if (currentPlayer.getIndex() == players.size() - 1)
			currentPlayer = players.get(0);
		else
			currentPlayer = players.get(currentPlayer.getIndex() + 1);
		if (currentPlayer.inDetention()) {
			playerInDetention();
		} else {
			phase = UnipolyPhase.WAITING;
			if (currentPlayer.isNPC()) {
				displayMessage = currentPlayer.getName() + " war an der Reihe.";
				rollDice(new Random().nextInt(6) + 1);
			}
		}
	}

	/**
	 * This method checks if the currentPlayer is in detention. If yes the player can either pay their way out
	 * or try to roll two same numbers.
	 *
	 * @throws FieldIndexException if the fieldIndex is out of bounds
	 */
	private void playerInDetention() throws FieldIndexException {
		if (currentPlayer.isNPC()) {
			displayMessage = currentPlayer.getName() + " ist noch bei der Schuldirektorin.";
			if (currentPlayer.getleftTimeInDetention() > 0) {
				if (NPCChecksMoney(RANSOM)) {
					displayMessage += "<br>" + currentPlayer.getName() + " besticht die Schuldirektorin.";
					payDetentionRansom();
				} else {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " versucht über den Schulverweis zu verhandeln.";
					rollTwoDice();
					if (rolledPash) {
						displayMessage += "<br>" + currentPlayer.getName() + " hat ein Pash gewürfelt.";
						leaveDetention();
						rollDice(new Random().nextInt(6) + 1);
					} else {
						displayMessage += "<br>" + currentPlayer.getName()
								+ " hat es nicht geschafft die Schuldirektorin zu überzeugen.";
						phase = UnipolyPhase.SHOWANDSWITCH;
					}
				}
			} else {
				displayMessage += "<br>" + currentPlayer.getName()
						+ " hat schon 3mal versucht Sie zu überzeugen; Ohne Erfolg!";
				displayMessage += "<br>" + currentPlayer.getName()
						+ " muss versuchen die Schuldirektorin zu bestechen.";
				payDetentionRansom();
			}
		} else {
			phase = UnipolyPhase.DETENTION;
		}
	}

	/***
	 * GameOver() method
	 */
	private void GameOver() {
		Player Bachelor = null;
		for (Player value : players) {
			if (value.isBachelor())
				Bachelor = value;
		}

		gameoverString = "<h1>GAME OVER</h1>";
		ArrayList<Owner> ranking = new ArrayList<>(players);
		Collections.sort(ranking);
		int j = 0;
		if (Bachelor != null) {
			gameoverString += "<p>" + 1 + ".Place, Bachelor of Science: " + Bachelor.getName() + ", " + Bachelor.getWealth() + "CHF, " + Bachelor.getECTS() + "ECTS</p>";
			ranking.remove(Bachelor);
			j++;
		}

		for (int i = 0; i < ranking.size(); i++) {
			Owner player = ranking.get(i);
			gameoverString += "<p>" + (i + 1 + j) + ".Place " + player.getName() + ", " + player.getWealth() + "CHF, " + ((Player) player).getECTS() + "ECTS</p>";
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
	 * Payoff the detention fee if the player has enough money.
	 *
	 * @throws FieldIndexException if the fieldIndex is out of bounds
	 */
	public void payDetentionRansom() throws FieldIndexException {
		if (currentPlayer.setAndCheckDebt(bank, RANSOM)) {
			if (currentPlayer.setandgetBankrupt()) {
				if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName()
							+ " kann kann sich das Bestechungsgeld nicht leisten und ist nun Bankrot.";
				} else {
					displayMessage = "Du kannst dir das Bestechungsgeld nicht leisten und bist nun Bankrot.";
				}
				GameOver();
			} else {
				if (currentPlayer.isNPC()) {
					NPCinDebt();
				} else {
					displayMessage = "Du hast nicht genug Geld um Sie zu bestechen, also leihst du dir was von der Bank.";
					phase = UnipolyPhase.INDEBT;
				}
			}
		} else {
			leaveDetention();
		}
	}

	/***
	 * The player is allowed to leave detention.
	 *
	 * @throws FieldIndexException if the fieldIndex is out of bounds
	 */
	public void leaveDetention() throws FieldIndexException {
		phase = UnipolyPhase.WAITING;
		currentPlayer.outDetention();
		if (currentPlayer.isNPC()) {
			displayMessage += "<br>" + currentPlayer.getName() + " wird vom Nachsitzen entlassen.";
			rollDice(new Random().nextInt(6) + 1);
		}
	}

	/**
	 * The NPC checks if he owns twice the amount of money which is required to pay.
	 *
	 * @param cost to check
	 * @return if he has twice the money required or not
	 */
	private boolean NPCChecksMoney(int cost) {
		boolean willBuy = false;
		if (currentPlayer.getMoney() > (2 * cost)) {
			willBuy = true;
		}
		return willBuy;
	}

	/**
	 * The NPC checks if there is a field free of a modulegroup he partly owns. If
	 * not he will jump to the GO field.
	 *
	 * @throws FieldIndexException  if the fieldIndex is out of bounds
	 */
	private void NPCJumps() throws FieldIndexException {
		Map<Integer, FieldProperty> mapProperties = board.getProperties();
		Map<Integer, LinkedList<FieldProperty>> mapModuleGroups = board.getModuleGroups();
		int jumpIndex = 0; // 0 is the index of the GO field
		for (Map.Entry<Integer, FieldProperty> entry : mapProperties.entrySet()) {
			if (entry.getValue().getOwnerIndex() == currentPlayer.getIndex() && jumpIndex == 0) {
				LinkedList<FieldProperty> fields = mapModuleGroups.get(entry.getValue().getModuleGroupIndex());
				int num = 0;
				while (fields.size() > num && jumpIndex == 0) {
					if (fields.get(num).getOwnerIndex() == FieldProperty.UNOWNED) {
						jumpIndex = board.getIndexFromField(fields.get(num));
					}
					num++;
				}
			}
		}
		jumpPlayer(jumpIndex);
	}

	/**
	 * This method is called when the NPC has enough money to buy a specific property. If he owns twice the
	 * amount of money that it cost, he will buy it.
	 *
	 * @param currentField the property in question
	 */
	private void NPCBuysProperty(FieldProperty currentField) {
		if (NPCChecksMoney(currentField.getPropertyCost())) {
			displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName()
					+ " gelandet und kauft das Modul.<br>";
			buyProperty();
		} else {
			displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName()
					+ " gelandet, aber will das Modul nicht kaufen.<br>";
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
	}
}