package application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Random;

public class LandBaronsModel {

	/**
	 * Size adds an extra 2 cells to the vertical and horizontal
	 * size to facilitate edge detection without using out of bounds
	 * exceptions
	 */
	private int size;

	private int playersTurn;

	private boolean winnable;

	private LandNode[][] board;

	private int[] playerBudgets;

	private boolean passedLastTurn;

	private boolean gameFinished;

	private PropertyChangeSupport pcs;



	public LandBaronsModel(int size) {
		initializeModel(size+2);
	}

	public void changeSize(int size) {
		//TODO: Maybe warn players of their actions?	
		initializeModel(size+2);
	}

	public int getBid(int row, int col) {
		row++; col++; //No one knows we added a padding row and column
		return board[row][col].getBid();
	}

	public void move(int row, int col) {
		//TODO: Add move to the move stack to facilitate undos?
		row++; col++; //No one else knows we added a padding row and column
		if(isBiddableTile(row, col))
			if(isAffordableBid(playersTurn,row,col)) {
				executeMove(playersTurn,row,col);
			}else {
				fireInvalidMoveDueToMoneyEvent(playersTurn,row,col);
			}

		else {
			this.fireInvalidMoveDueToUnbiddableTileEvent(playersTurn, row, col);		
		}
	}

	private void executeMove(int player,int row,int col) {
		
		int currentBid = board[row][col].getBid();
		int currentOwner = board[row][col].getOwnership();
		if(currentOwner == PLAYER_ONE_OWNED) {
			//Player One gets a refund of their current bid
			playerBudgets[PLAYER_ONE] +=currentBid;	
		}else if(currentOwner == PLAYER_TWO_OWNED) {
			//Player two gets a refund of their current bid
			playerBudgets[PLAYER_TWO] += currentBid;
		}
		
		//The player executing the move pays for the cost of the land
		playerBudgets[player] -= (currentBid+1);
		
		//If statement for readability
		if(player == PLAYER_ONE)
			board[row][col].makeBid(PLAYER_ONE_OWNED);
		else
			board[row][col].makeBid(PLAYER_TWO_OWNED);
		
		playersTurn++; playersTurn %=2; //Update Player Turn
		passedLastTurn = false; 
		this.fireValidMove(player, row, col, currentBid);
	}

	public int getSize() {
		return size-2;
	}

	public void pass() {
		if(passedLastTurn)
			gameFinished();
		else
			passedLastTurn = true;
	}

	public void reset() {
		winnable = false;
		playersTurn = 0;
		initBudget();

		while(!winnable) {
			resetNodes();
			addSpecialNodes();
			testWinnability();
		}

	}

	private void initializeModel(int size) {
		pcs = new PropertyChangeSupport(this);
		this.size = size;
		playersTurn = 0;
		winnable = false;
		board = new LandNode[size][size];
		initializeBoard();
		initBudget();
		passedLastTurn = false;
		gameFinished = false;
	}

	private void initializeBoard() {
		constructBoard();
		addSpecialNodes();
		connectBoard();
	}

	private void constructBoard() {
		for(int row = 1; row < board.length-1; row++) {
			for(int col = 1; col < board[row].length-1;col++) {
				board[row][col] = new LandNode();
			}
		}
		board[1][1].setOwnership(SOURCE_OR_DESTINATION);
		board[size-2][size-2].setOwnership(SOURCE_OR_DESTINATION);

	}

	private void connectBoard() {
		for(int row = 1; row < board.length -1; row++) {
			for(int col = 1; col < board[row].length-1;col++) {
				if(null != board[row-1][col] ) {
					board[row][col].connectNodes(board[row-1][col],ABOVE);
				}if( null != board[row][col+1]) {
					board[row][col].connectNodes(board[row][col+1],RIGHT_OF);
				}if( null != board[row+1][col]) {
					board[row][col].connectNodes(board[row+1][col],BELOW);
				}if( null != board[row][col-1]) {
					board[row][col].connectNodes(board[row][col-1],LEFT_OF);
				}
			}
		}
	}

	private void initBudget() {
		playerBudgets = new int[2];
		playerBudgets[PLAYER_ONE] = (size-2)*(size-2)*2;
		playerBudgets[PLAYER_TWO] = playerBudgets[PLAYER_ONE];
	}

	private void gameFinished() {
		//TODO: for each node in path, sum player one and player 2
		//TODO: Add unspent budgets to their players sums*10
		//TODO: Inform listeners that the game is done and disallow further moves game moves
	}


	private void resetNodes() {
		for(int row = 1; row < board.length-1;row++)
			for(int col = 1; col < board[row].length-1;col++)
				board[row][col].reset();
	}

	//1 through size - 2
	private void addSpecialNodes() {
		Random yolo = new Random();
		for(int neededSpecialTiles = 0; neededSpecialTiles < size-2;neededSpecialTiles++) {
			boolean failedToAddTile = true;
			while(failedToAddTile) {
				int row = yolo.nextInt(size-2) + 1;
				int col = yolo.nextInt(size-2) + 1;
				if(isValidTileLocation(row, col)) 
					if(noCollision(row,col)) 
						if(yolo.nextInt(2) == 0) {
							board[row][col].setOwnership(PUBLIC_LAND);
							failedToAddTile = false;
						}else {
							board[row][col].setOwnership(COMPANY);
							failedToAddTile = false;
						}

			}
		}
	}

	/**
	 * A random tile can initially be placed at any tile besides the start and ending tile
	 * for the companies.
	 * @param horizontal
	 * @param vertical
	 * @return A potentially valid location was found
	 */
	private boolean isValidTileLocation(int horizontal, int vertical) {
		if(horizontal == 1 && vertical == 1)
			return false;
		else if(horizontal == size - 2 && vertical == size - 2)
			return false;
		else
			return true;
	}

	private boolean noCollision(int row, int col) {
		if(board[row][col].getOwnership() < 0)
			return false;
		else
			return true;
	}

	private void testWinnability() {
		//TODO: Use cheapestPaths result to determine that a path exists
		//TODO: Change winnable accordingly
	}

	private void cheapestPath(){

		//TODO: Write Djikstras

	}

	private boolean isBiddableTile(int row, int col) {
		return board[row][col].getOwnership() >-1;
	}

	private boolean isAffordableBid(int player, int row, int col) {
		int currentOwner = board[row][col].getOwnership();
		//If a player already owns a tile, they need only add one dollar to
		//raise a bid.
		if(currentOwner == player+1)
			return playerBudgets[player] > 0;
		return playerBudgets[player] > board[row][col].getBid();
	}

	private void fireInvalidMoveDueToUnbiddableTileEvent(int player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		String landOwner = "";
		if(board[row][col].getOwnership() == -3) 
			landOwner = "Company";		
		else if(board[row][col].getOwnership() == -2) 
			landOwner = "Public";		
		else
			landOwner = "Company";
		this.pcs.firePropertyChange("I " + "U " + (player+1) + " " + outwardBoundRow
				+ " " + outwardBoundColumn + " " + landOwner,false, true);
	}

	private void fireInvalidMoveDueToMoneyEvent(int player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("I " + "$ " + (player+1) + " " + outwardBoundRow 
				+ " " + outwardBoundColumn, playerBudgets[player], 
				board[row][col].getBid());
				
	}

	private void fireValidMove(int player, int row, int col, int oldValue) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("V " + "# " + (player+1) + " " + outwardBoundRow 
				+ " " + outwardBoundColumn,	oldValue, true);
	}



	public String toString() {
		String s = "";
		if(gameFinished) {
			s += "The game has ended!\n";
		}else {
			if(playersTurn == 0)
				s+= "It is Player 1's turn!\n";
			else
				s+= "It is Player 2's turn!\n";
			s+= "Player 1 has " + playerBudgets[PLAYER_ONE] + "$ left to bid\n";
			s+= "Player 2 has " + playerBudgets[PLAYER_TWO] + "$ left to bid\n";
		}
		return s;
	}

	/**
	 * @return - Copy of the game board, useful for debugging
	 */
	public LandNode[][] getBoardClone(){
		return board.clone();
	}

	public String printBoard() {
		String s = "";
		for(int row = 1; row < board.length-1;row++) {
			for(int col = 1; col < board.length-1;col++) {
				s+=board[row][col] + "\t";
			}
			s +="\n";
		}
		return s;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	private static final int SOURCE_OR_DESTINATION = -3;
	private static final int PUBLIC_LAND = -2;
	private static final int COMPANY = -1;
	private static final int UNOWNED = 0;
	private static final int PLAYER_ONE_OWNED = 1;
	private static final int PLAYER_TWO_OWNED = 2;

	private static final int PLAYER_ONE = 0;
	private static final int PLAYER_TWO = 1;

	private static final int ABOVE = 0;
	private static final int RIGHT_OF = 1;
	private static final int BELOW = 2;
	private static final int LEFT_OF = 3;

}
