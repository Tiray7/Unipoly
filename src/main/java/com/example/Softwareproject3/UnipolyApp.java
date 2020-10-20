package com.example.Softwareproject3;

import java.util.ArrayList;
import java.util.Random;

public class UnipolyApp {

	private UnipolyPhase phase = UnipolyPhase.WAITING;
	private Gamemode gamemode;

	private ArrayList<Player> players;

	private int currentPlayerIndex;

	private int rolledValue1 = 0;
	private int rolledValue2 = 0;
	private int rolledValue = 0;
	private boolean rolledPash = false;

	public UnipolyApp() {
		players = new ArrayList<Player>();
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

	public int getRolledValue1() {
		return rolledValue1;
	}

	public int getRolledValue2() {
		return rolledValue2;
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
		Player currentPlayer = players.get(currentPlayerIndex);

		if (currentPlayer.isJailed()) {
			phase = UnipolyPhase.JAILED;
		}
	}

	public void rollDice(int diceval1) {
		phase = UnipolyPhase.ROLLING;
		Player currentPlayer = players.get(currentPlayerIndex);

		// 4 for Testing
		rolledValue2 = 4;
		rolledValue1 = diceval1;

		rolledValue = rolledValue1 + rolledValue2;
		doField(currentPlayer, rolledValue);
	}

	// Move to Field
	private void doField(Player currentPlayer, int rolledValue) {

		int previousFieldIndex = currentPlayer.getToken().getcurrFieldIndex();
		currentPlayer.getToken().moveBy(rolledValue);
		int currentFieldIndex = currentPlayer.getToken().getcurrFieldIndex();
		
		//Field currentField = get current Field

		// Pass go
		if(previousFieldIndex > currentFieldIndex) {
			// Bank gives Player 200CHF
		}

		//tileOperation(currentField, currentPlayer);

	}

}
