package com.example.Softwareproject3;

import java.util.ArrayList;
import java.util.Random;

public class UnipolyApp {

	private UnipolyPhase phase = UnipolyPhase.WAITING;
	private ArrayList<Player> players;
	private int currentPlayerIndex = 0;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;

	enum UnipolyPhase {
		WAITING,
		ROLLING,
		BUY_PROPERTY,
		TURN,
		JAILED,
		ENDGAME,
		SHOWCARD,
		QUIZTIME
	}

	public UnipolyApp() {
		players = new ArrayList<>();
	}

	public UnipolyPhase getPhase() {
		return phase;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getCurrentPlayerIndex() {
		return currentPlayerIndex;
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

	// Add a new Player to the Game
	public void join(String name, TokenType token) {

		if (phase != UnipolyPhase.WAITING) {
			throw new IllegalStateException("Cannot join unless in the waiting phase.");
		}

		for (Player player : players) {
			if (player.getName().equals(name)) {
				throw new IllegalArgumentException("Player name already exists.");
			}
			if (player.getToken().getType() == token) {
				throw new IllegalArgumentException("Player token already exists.");
			}
		}
		Player player = new Player(name, token);
		player.getToken().moveTo(0);
		player.index = players.size();
		players.add(player);
	}

	// Start a new Game
	public void start(Gamemode mode) {
		if (phase != UnipolyPhase.WAITING) {
			throw new IllegalStateException("Cannot start the unipoly unless in the waiting phase.");
		}

		// Check if we play Singleplayer or Multiplayer
		if (Gamemode.SINGLE == mode) {
			if (players.size() != 1) {
				throw new IllegalStateException("Too many players for singleplayer mode");
			} else {
				Player player = new Player("NPC", TokenType.NPC);
				player.getToken().moveTo(0);
				player.index = players.size();
				players.add(player);
			}
		} else if (Gamemode.MULTI == mode) {
			if (players.size() < 2) {
				throw new IllegalStateException("Not enough players for multiplayer mode");
			} else if (players.size() > 4) {
				throw new IllegalStateException("Too many players for multiplayer mode");
			}
		}
		// Automatically start first Turn
		startTurn();
	}

	// The Current Player starts his Turn
	private void startTurn() {
		if(players.get(currentPlayerIndex).isJailed()) {
			phase = UnipolyPhase.JAILED;
		}
	}

	public void rollDice() {
		firstDice = new Random().nextInt(6) + 1;
		secondDice = new Random().nextInt(6) + 1;
		if(firstDice == secondDice) rolledPash = true;
	}

	public void rollDice(int firstDice) throws InterruptedException {
		phase = UnipolyPhase.ROLLING;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		checkFieldOptions(players.get(currentPlayerIndex), this.firstDice + secondDice);
	}

	private void checkFieldOptions(Player currentPlayer, int rolledValue) throws InterruptedException {
		int currentFieldIndex = currentPlayer.getToken().getcurrFieldIndex();
		if (moveAndCheckIfOverStart(currentPlayer, rolledValue, currentFieldIndex)) {
			// Bank gives Player 200CHF;
			sleep(2000);
		}
		phase = UnipolyPhase.WAITING;

		//tileOperation(currentField, currentPlayer);
		 /*if (fachFeld && no owner && enoughMoney) {
		 	phase = UnipolyPhase.BUY_PROPERTY;
			if(user wants to buy) {
				buyProperty();
				return;
			} else {
				userTurnEnds();
				--> WAITING Phase
				return;
			}
		if (startfeld)
			get double some money
		if (chance)
			draw a chance card
			get money/pay money/whatever
		if (springer)
			do you want to buy?
				if (yes && enough money)
					set ownsCard
				else
					alert: you poor bum
		After everythings checked and done:
			--> update phase and currentPlayerIndex

					*/
	}

	private void buyProperty(int buy) {
		/*if(buy == 1)
				set owner
				- money
		else
			alert: you can't buy
		else {
			if(currentPlayer == owner)
				do you want to build something?
					else
			paySomeMoney, homie*/
	}

	public void switchPlayer() {
		if(currentPlayerIndex == players.size() - 1) currentPlayerIndex = 0;
		else currentPlayerIndex++;
	}

	private boolean moveAndCheckIfOverStart(Player currentPlayer, int rolledValue, int previousField) {
		currentPlayer.getToken().moveBy(rolledValue);
		return previousField > currentPlayer.getToken().getcurrFieldIndex();
	}
}

// Start: 			0
// Fächer: 			1,3,5,6,8,10,12,13,14,15,17,19,21,22,23,24,26,28,30,31,33,35
// Chance: 			2,4,11,20,29,32
// Springer: 		7,16,25,34
// Nachsitzen:		9
// Parkplatz: 		18
// Geh in Knast:	27
