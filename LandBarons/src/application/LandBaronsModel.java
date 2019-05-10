package application;

import java.util.Objects;
import java.util.Random;

public class LandBaronsModel {
	
	/**
	 * Size adds an extra 2 cells to the vertical and horizontal
	 * size to facilitate edge detection without generation of a
	 * stack trace
	 */
	private int size;

	private int turnCount;

	private boolean winnable;

	private LandNode[][] board;

	private int playerOneBudget;

	private int playerTwoBudget;

	private boolean passedLastTurn;

	private boolean gameFinished;



	public LandBaronsModel(int size) {
		initializeModel(size+2);
	}

	public void changeSize(int size) {
		//TODO: Maybe warn players of their actions?	
		initializeModel(size+2);
	}

	public void makeMove(int row, int col) {
		if(isValidMove(row, col))
			//TODO: do the move
			//TODO: Change the state of the boolean referring to passes
			//TODO: Add move to the move stack to facilitate undos?
			;
		else
			//TODO: Inform the view why the move was illegal
			;
	}

	public void pass() {
		if(passedLastTurn)
			gameFinished();
		else
			passedLastTurn = true;
	}

	public void reset() {
		winnable = false;
		turnCount = 0;
		initBudget();

		while(!winnable) {
			resetNodes();
			addSpecialNodes();
			testWinnability();
		}

	}

	private void initializeModel(int size) {
		this.size = size;
		turnCount = 0;
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
		playerOneBudget = (size-2)*(size-2)*2;
		playerTwoBudget = playerOneBudget;
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
							board[row][col].setOwnership(COMPANY_OWNED);
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

	private boolean isValidMove(int row, int col) {
		//TODO: Determine if the move is valid
		return false;
	}

	public String toString() {
		//TODO: Inform users what the status of the game is
		String s = "";
		if(gameFinished) {
			s += "The game has ended!\n";
		}else {
			if(turnCount%2 == 0)
				s+= "It is Player 1's turn!\n";
			else
				s+= "It is Player 2's turn!\n";
			s+= "Player 1 has " + playerOneBudget + "$ left to bid\n";
			s+= "Player 2 has " + playerTwoBudget + "$ left to bid\n";
		}
		return s;
	}
	
	/**
	 * @return - Copy of the game board, useful for debugging
	 */
	public LandNode[][] getBoardClone(){
		return board.clone();
	}

	private static final int SOURCE_OR_DESTINATION = -3;
	private static final int PUBLIC_LAND = -2;
	private static final int COMPANY_OWNED = -1;
	private static final int UNOWNED = 0;
	private static final int PLAYER_ONE_OWNED = 1;
	private static final int PLAYER_TWO_OWNED = 2;

	private static final int ABOVE = 0;
	private static final int RIGHT_OF = 1;
	private static final int BELOW = 2;
	private static final int LEFT_OF = 3;

}
