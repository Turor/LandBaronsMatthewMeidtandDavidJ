package application;

import java.util.LinkedList;

public class LandBaronsModel {
	
	private int size;
	
	private int turnCount;
	
	private boolean winnable;
	
	private LandNode[][] board;
	
	private int playerOneBudget;
	
	private int playerTwoBudget;
	
	boolean passedLastTurn;
	
	boolean gameFinished;
	

	
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
		//TODO: Cycle through every node in the board calling their constructors
		//TODO: Cycle through the board and create the connections to their neighbors
	}
	
	private void initBudget() {
		playerOneBudget = size*size*2;
		playerTwoBudget = playerOneBudget;
	}
	
	private void gameFinished() {
		LinkedList<LandNode> path = cheapestPath();
		//TODO: for each node in path, sum player one and player 2
		//TODO: Add unspent budgets to their players sums*10
		//TODO: Inform listeners that the game is done and disallow further moves game moves
	}
	

	private void resetNodes() {
		//TODO: Visits all nodes and calls their reset methods
	}
	
	private void addSpecialNodes() {
		//TODO: Use random number generator to add the special nodes
		// 			- Ensure proper collision behavior
	}
	
	private void testWinnability() {
		//TODO: Use cheapestPaths result to determine that a path exists
		//TODO: Change winnable accordingly
	}
	
	private LinkedList<LandNode> cheapestPath(){
		LinkedList<LandNode> path = new LinkedList<LandNode>();
		//TODO: Write Djikstras
		
		return path;
	}
	
	private boolean isValidMove(int row, int col) {
		//TODO: Determine if the move is valid
		return false;
	}
	
	public String toString() {
		//TODO: Inform users what the status of the game is
		return "";
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
