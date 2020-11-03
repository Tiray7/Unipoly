package com.example.Softwareproject3;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Random;

@Component
public class UnipolyApp {

	private UnipolyPhase phase = UnipolyPhase.WAITING;
	private ArrayList<Player> players;
	private int currentPlayerIndex = 0;
	private int firstDice;
	private int secondDice;
	private boolean rolledPash = false;
	private Bank bank;
	private Board board;

	enum UnipolyPhase {
		WAITING,
		ROLLING,
		BUY_PROPERTY,
		TURN,
		JAILED,
		ENDGAME,
		SHOWCARD,
		QUIZTIME,
		JUMP
	}

	public UnipolyApp() {
		board = new Board();
		bank = new Bank();
		players = new ArrayList<>();
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

	// resets Game
	public void resetGame(){
		board = new Board();
		bank = new Bank();
		players = new ArrayList<Player>();
		this.phase = UnipolyPhase.WAITING;
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

	public void rollDice(int firstDice) throws FieldIndexException {
		phase = UnipolyPhase.ROLLING;
		this.firstDice = firstDice;
		secondDice = new Random().nextInt(6) + 1;
		checkFieldOptions(this.firstDice + secondDice);
	}

	public void checkFieldOptions(int rolledValue) throws FieldIndexException {
		Player currentPlayer = players.get(currentPlayerIndex);
		int currentFieldIndex = currentPlayer.getToken().getcurrFieldIndex();
		if (moveAndCheckIfOverStart(currentPlayer, rolledValue, currentFieldIndex)) {
			// Bank gives Player 200CHF;
		} else if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.GO) {
			// Bank gives Player 400CHF;
		}
		phase = UnipolyPhase.WAITING;
		int NO_OWNER = -1;
		if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.PROPERTY &&
				board.getPropertyOwner(currentFieldIndex) == NO_OWNER &&
					currentPlayer.getMoney() > board.getCostFromProperty(currentFieldIndex)) {
			phase = UnipolyPhase.BUY_PROPERTY;
		}
		if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.CHANCE) {
			// draw chance card
			// get money / pay money / whatever
		}
		final int COST_FOR_JUMP = 100;
		if(board.getFieldTypeAtIndex(currentFieldIndex) == Config.FieldLabel.JUMP &&
				currentPlayer.getMoney() > COST_FOR_JUMP) {
			phase = UnipolyPhase.JUMP;
		}
		switchPlayer();
	}

	public void buyProperty(boolean buy, int currentFieldIndex) throws FieldIndexException {
		if(buy) {
			board.getFieldPropertyAtIndex(currentFieldIndex).setOwnerIndex(currentPlayerIndex);
			// geld abziehen und so
		} else {
			switchPlayer();
		}
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
