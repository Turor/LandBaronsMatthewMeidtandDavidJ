package application;

import java.util.LinkedList;

public class LandBaronsModel {
	
	private int size;
	
	private int turnCount;
	
	private boolean winnable;
	
	private LandNode[][] board;
	
	private int playerOneBudget;
	
	private int playerTwoBudget;
	
	private boolean passedLastTurn;
	
	private boolean gameFinished;
	

	
	public LandBaronsModel(int size) {
		initializeModel(size);
	}
	
	public void changeSize(int size) {
		//TODO: Maybe warn players of their actions?	
		initializeModel(size);
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
		connectBoard();
	}
	
	private void constructBoard() {
		for(int row = 0; row < board.length; row++) {
			for(int col = 0; col < board[row].length;col++) {
				board[row][col] = new LandNode();
			}
		}
		
	}
	
	private void connectBoard() {
		makeInternalNodeConnections();
		makeFirstRowConnections();
		makeLastRowConnections();
		makeFirstColumnConnections();
		makeLastColumnConnections();
		makeCornerConnections();
	}
	
	private void makeInternalNodeConnections() {
		for(int row = 1; row < board.length-1;row++)
			for(int col = 1; col < board[row].length-1;col++) {
				connectABOVE(row,col);
				connectRIGHT_OF(row,col);
				connectBELOW(row,col);
				connectLEFT_OF(row,col);
			}
	}
	
	private void connectABOVE(int row, int col) { 
		board[row][col].connectNodes(board[row-1][col],ABOVE);
	}
	
	private void connectRIGHT_OF(int row, int col) {
		board[row][col].connectNodes(board[row][col+1],RIGHT_OF);
	}
	
	private void connectBELOW(int row, int col) {
		board[row][col].connectNodes(board[row+1][col],BELOW);
	}
	
	private void connectLEFT_OF(int row, int col) {
		board[row][col].connectNodes(board[row][col-1],LEFT_OF);
	}

	private void makeFirstRowConnections() {
		for(int col = 1,row = 0; col < board.length-1;col++) {
			connectRIGHT_OF(row,col);
			connectBELOW(row,col);
			connectLEFT_OF(row,col);
		}
	}

	private void makeLastRowConnections() {
		for(int col = 1, row = board.length-1; col < board.length-1; col++) {
			connectABOVE(row,col);
			connectRIGHT_OF(row,col);
			connectLEFT_OF(row,col);
		}
	}
	
	private void makeFirstColumnConnections() {
		for(int col = 0, row = 1; row < board.length-1; row++) {
			connectABOVE(row,col);
			connectRIGHT_OF(row,col);
			connectBELOW(row,col);
		}
	}
	
	private void makeLastColumnConnections() {
		for(int col = board.length-1, row = 1; row < board.length-1; row++) {
			connectABOVE(row,col);
			connectLEFT_OF(row,col);
			connectBELOW(row,col);
		}
	}
	
	private void makeCornerConnections() {
		connectBELOW(0,0);
		connectRIGHT_OF(0,0);
		connectLEFT_OF(0,board.length-1);
		connectBELOW(0,board.length-1);
		connectABOVE(board.length-1,0);
		connectRIGHT_OF(board.length-1,0);
		connectABOVE(board.length-1,board.length-1);
		connectLEFT_OF(board.length-1,board.length-1);
	}
	
	private void initBudget() {
		playerOneBudget = size*size*2;
		playerTwoBudget = playerOneBudget;
	}
	
	private void gameFinished() {
		//TODO: for each node in path, sum player one and player 2
		//TODO: Add unspent budgets to their players sums*10
		//TODO: Inform listeners that the game is done and disallow further moves game moves
	}
	

	private void resetNodes() {
		for(int row = 0; row < board.length;row++)
			for(int col = 0; col < board[row].length;col++)
				board[row][col].reset();
	}
	
	private void addSpecialNodes() {
		//TODO: Use random number generator to add the special nodes
		// 			- Ensure proper collision behavior
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
