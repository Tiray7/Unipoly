package com.example.Softwareproject3;

import java.util.ArrayList;
import java.util.Random;

public class UnipolyApp {

	private UnipolyPhase phase = UnipolyPhase.WAITING;
	private Gamemode gamemode;
	private ArrayList<Player> players;
	private int currentPlayerIndex;
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
		gamemode = mode;

		// Check if we play Singleplayer or Multiplayer
		if (Gamemode.SINGLE == gamemode) {
			if (players.size() != 1) {
				throw new IllegalStateException("To many Players for Single Gameplay");
			} else {
				Player player = new Player("NPC", TokenType.NPC);
				player.getToken().moveTo(0);
				player.index = players.size();
				players.add(player);
			}
		} else if (Gamemode.MULTI == gamemode) {
			if (players.size() < 2) {
				throw new IllegalStateException("Not enough Players for Multiplayer Gameplay");
			} else if (players.size() > 4) {
				throw new IllegalStateException("To many Players for Multiplayer Gameplay");
			}
		}

		currentPlayerIndex = (new Random()).nextInt(players.size());

		// Reset player positions on board
		for (Player player : players) {
			player.getToken().moveTo(0);
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

	public void rollDice(int firstDice) {
		phase = UnipolyPhase.ROLLING;
		secondDice = new Random().nextInt(6) + 1;
		checkFieldOptions(players.get(currentPlayerIndex), firstDice + secondDice);
	}

	private void checkFieldOptions(Player currentPlayer, int rolledValue) {
		int currentFieldIndex = currentPlayer.getToken().getcurrFieldIndex();
		if (moveAndCheckIfOverStart(currentPlayer, rolledValue, currentFieldIndex)) {
		}// Bank gives Player 200CHF;
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

	private boolean moveAndCheckIfOverStart(Player currentPlayer, int rolledValue, int previousField) {
		currentPlayer.getToken().moveBy(rolledValue);
		return previousField > currentPlayer.getToken().getcurrFieldIndex();
	}
}
