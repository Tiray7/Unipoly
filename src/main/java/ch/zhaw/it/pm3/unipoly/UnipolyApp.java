package ch.zhaw.it.pm3.unipoly;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.*;

import static ch.zhaw.it.pm3.unipoly.Config.COST_FOR_JUMP;
import static ch.zhaw.it.pm3.unipoly.Config.RANSOM;

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

	/*------ GET and SET functions ------------------------------------------------------------------*/
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
		checkIfOverStart();
		switch (currentField.getLabel()) {
			case PROPERTY:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnPropertyField() gets executed");
				playerIsOnPropertyField();
			case CHANCE:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnChanceField() gets executed");
				playerIsOnChanceField();
			case JUMP:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnJumpField() gets executed");
				playerIsOnJumpField();
			case GO:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoField() gets executed");
				playerIsOnGoField();
			case VISIT:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnVisit() gets executed");
				playerIsOnVisit();
			case DETENTION:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoToDetention() gets executed");
				playerIsOnGoToDetention();
			case RECESS:
				unipolyMcLogger.log(Level.DEBUG, "Method playerIsOnGoZnueniPause() gets executed");
				playerIsOnGoZnueniPause();
			default:
				break;
		}
	}

	/***
	 * Checks if the currentField is the detention field. If so the player gets to choose if he
	 * wants to take his/her getFreeCard (if there is one in his/her possession). After the evaluation the
	 * shown message gets set accordingly.
	 *
	 */
	private void playerIsOnGoToDetention() {
		if (currentField.getLabel() == Config.FieldLabel.DETENTION) {
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
	}

	/***
	 * Holds a {@link Player} for a round
	 *
	 */
	private void playerIsOnGoZnueniPause() {
		if (currentField.getLabel() == Config.FieldLabel.RECESS) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist in die Znüni Pause.";
			} else {
				displayMessage = "Znüni Zeit. Ruh dich etwas aus:<br>Trink einen Kaffee und iss ein Sandwich!";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
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
				if (currentPlayer.isNPC() && NPCChecksMoney(currentField.getPropertyCost())) {
					displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName()
							+ " gelandet und kauft das Modul.<br>";
					buyProperty();
				} else if (currentPlayer.isNPC()) {
					displayMessage += "<br>" + currentPlayer.getName() + " ist auf " + currentField.getName()
							+ " gelandet, aber will das Modul nicht kaufen.<br>";
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
		if (currentField.getLabel() == Config.FieldLabel.VISIT) {
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " ist auf dem 'Nur zu Besuch' Feld gelandet.";
			} else {
				displayMessage = "Zum Glück nur zu Besuch im Rektorat.";
			}
			phase = UnipolyPhase.SHOWANDSWITCH;
		}
	}

	/***
	 * Checks if a {@link Player} is on a CHANCE field.
	 * Sets {@link #displayMessage} and {@link UnipolyPhase} accordingly
	 *
	 */
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

	/***
	 * redCard method to find what to make if the playe get this card
	 *
	 * @throws FieldIndexException
	 */
	public void readCard() throws FieldIndexException {
		cards.get(0);
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
				if (currentPlayer.setandcheckDebt(bank, cards.get(0).getAmount())) {
					if (currentPlayer.setandgetBankrupt()) {
						if (currentPlayer.isNPC()) {
							displayMessage += "<br>" + currentPlayer.getName()
									+ " kann kann sich das nicht leisten und ist nun Bankrot.";
						} else {
							displayMessage = "Du kannst dir das nicht leisten und bist nun Bankrot.";
						}
						GameOver();
						return;
					} else {
						if (currentPlayer.isNPC()) {
							displayMessage += "<br>" + currentPlayer.getName() + " kann sich dies nicht leisten.";
							NPCinDebt();
						} else {
							displayMessage += "Du kannst dir dies nicht leisten. Verkaufe deine Module um deine Schulden zurückzahlen zu können.";
							phase = UnipolyPhase.INDEBT;
						}
					}
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

	private void playerIsOnJumpField() {
		if (currentField.getLabel() == Config.FieldLabel.JUMP) {
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
		board.checkAndDecreaseRentAndECTS(board.getProperties().get(FieldIndex));
		board.getProperties().get(FieldIndex).resetLevel();
	}

	/***
	 * landedOnOwnedProperty method player landed on owned Land
	 *
	 * @throws FieldIndexException
	 */
	private void landedOnOwnedProperty() throws FieldIndexException {
		FieldProperty currentProperty = ((FieldProperty) currentField);
		if (currentPlayer.setandcheckDebt(players.get(currentProperty.getOwnerIndex()),
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
			board.checkAndRaiseRentAndECTS(currentProperty);
			phase = UnipolyPhase.QUIZTIME;
			if (currentPlayer.isNPC()) {
				displayMessage += "<br>" + currentPlayer.getName() + " muss " + currentProperty.getCurrentRent()
						+ " CHF Miete zahlen.";
				int bool = new Random().nextInt(1);
				quizAnswer(bool == 0);
			}
		}
	}

	// TODO: Player landed on his own Modul
	private void landedOnMyProperty() {
		board.checkAndRaiseRentAndECTS((FieldProperty) currentField);
		displayMessage = "Modul Upgrade!!";
		phase = UnipolyPhase.SHOWANDSWITCH;
	}

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
	}

	/***
	 * payOffDebt method refer to the debtor
	 *
	 * @param fieldIndexes field index number
	 * @throws FieldIndexException
	 */
	public void payOffDebt(Integer[] fieldIndexes) throws FieldIndexException {
		Owner Debtor = currentPlayer.getDebtor();
		int propertyValue = 0;
		for (int i = 0; i < fieldIndexes.length; i++) {
			if (Debtor.isBank()) {
				sellProperty(fieldIndexes[i], Debtor);
			} else {
				propertyValue = board.getProperties().get(fieldIndexes[i]).getPropertyCost();
				currentPlayer.transferPropertyTo(Debtor, fieldIndexes[i]);
				currentPlayer.setDebt(Math.max(0, currentPlayer.getDebt() - propertyValue));
				board.checkAndDecreaseRentAndECTS(board.getProperties().get(fieldIndexes[i]));
				board.getProperties().get(fieldIndexes[i]).resetLevel();
			}
		}
		if (currentPlayer.setandcheckDebt(Debtor, currentPlayer.getDebt())) {
			GameOver();
		}
	}

	private void NPCinDebt() throws FieldIndexException {
		int selling = 0;
		List<Integer> currarr = new ArrayList<Integer>();
		for (Map.Entry<Integer, FieldProperty> modul : currentPlayer.getownedModuls().entrySet()) {
			if (selling < currentPlayer.getDebt()) {
				selling += modul.getValue().getPropertyCost();
				currarr.add(modul.getKey());
			}
		}
		Integer[] FieldIndexes = currarr.toArray(new Integer[currarr.size()]);
		payOffDebt(FieldIndexes);
		displayMessage += "<br>" + currentPlayer.getName()
				+ " musste Module verkaufen um seine Schulden bezahlen zu können.";
		phase = UnipolyPhase.SHOWANDSWITCH;
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
		} else {
			phase = UnipolyPhase.WAITING;
			if (currentPlayer.isNPC()) {
				displayMessage = currentPlayer.getName() + " war an der Reihe.";
				rollDice(new Random().nextInt(6) + 1);
			}
		}
	}

	/***
	 * GameOver() method
	 */
	private void GameOver() {
		Player Bachelor = null;
		for (int i = 0; i < players.size(); i++) {
			if (players.get(i).isBachelor())
				Bachelor = players.get(i);
		}

		gameoverString = "<h1>GAME OVER</h1>";
		ArrayList<Owner> ranking = new ArrayList<>(players);
		Collections.sort(ranking);

		if (Bachelor != null) {
			ranking.remove(Bachelor);
		}

		for (int i = 0; i < ranking.size(); i++) {
			Owner player = ranking.get(i);
			gameoverString += "<p>" + (i + 1) + ".Place " + player.getName() + ", " + player.getWealth() + "</p>";
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
	 * payoff the the Detention
	 *
	 * @throws FieldIndexException
	 */
	public void payDetentionRansom() throws FieldIndexException {
		if (currentPlayer.setandcheckDebt(bank, RANSOM)) {
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
	 * leaving the Detention "on hold"
	 *
	 * @throws FieldIndexException
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
	 * The NPC checks if there is a field free of a modulegroup he partly owns. If not he will jump to the GO field.
	 */
	private int NPCJumps() {
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
		return jumpIndex;
	}
}