package application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.Random;

public class LandBaronsModel {

	/**
	 * Size adds an extra 2 cells to the vertical and horizontal
	 * size to facilitate edge detection without using out of bounds
	 * exceptions
	 */
	private int size;

	private LandBaron[] npcBarons;

	private LandBaron[] players;

	private LinkedList<LandBaron> turn;

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
			if(isAffordableBid(turn.peek(),row,col)) {
				executeMove(turn.peek(),row,col);
			}else {
				fireInvalidMoveDueToMoneyEvent(turn.peek(),row,col);
			}

		else {
			this.fireInvalidMoveDueToUnbiddableTileEvent(turn.peek(), row, col);		
		}
	}

	private void executeMove(LandBaron player, int row,int col) {

		//The previous owner receives a refund
		board[row][col].getOwnership().increaseBudget(board[row][col].getBid());

		board[row][col].makeBid(player);
		player.decreaseBudget(board[row][col].getBid());

		advanceTurn();
		passedLastTurn = false; 
		this.fireValidMove(player, row, col);
	}

	public int getSize() {
		return size-2;
	}

	public void pass() {
		if(passedLastTurn)
			gameFinished();
		else {
			passedLastTurn = true;
			advanceTurn();
		}
	}

	public String getOwner(int row, int col) {
		row++; col++;
		return board[row][col].getOwnership().toString();
	}

	private void advanceTurn() {
		turn.add(turn.poll());
	}

	public void reset() {
		winnable = false;
		passedLastTurn = false;
		gameFinished = false;
		resetPlayers();


		resetNodes();
		addSpecialNodes();



	}

	private void resetPlayers() {
		turn = new LinkedList<LandBaron>();
		for(int player = 0; player < players.length;player++) {
			players[player].resetGame(calculateBudget());
			turn.add(players[player]);
		}
	}

	private void resetForDijkstra() {
		for(int row = 1; row < size-1; row++) {
			for(int col = 1; col < size-1; col++) {
				board[row][col].resetForDijkstra();
			}
		}
	}

	private void initializeModel(int size) {
		pcs = new PropertyChangeSupport(this);
		this.size = size;

		//Initialize the players names and their corresponding identities

		initializeLandBarons();

		initializeBoard();
		initBudget();
		winnable = false;	
		passedLastTurn = false;
		gameFinished = false;
	}

	private void initializeLandBarons() {	
		initializeNPCS();
		initializePlayers();
	}

	private void initializeNPCS() {
		npcBarons = new LandBaron[4];
		npcBarons[0] = new LandBaron("The Company", !BIDDABLE,TRAVERSABLE, 0);
		npcBarons[1] = new LandBaron("The Public", !BIDDABLE,!TRAVERSABLE,0);
		npcBarons[2] = new LandBaron("The Uninformed Faction", BIDDABLE,TRAVERSABLE,0);
		npcBarons[3] = new LandBaron("The Origin", !BIDDABLE, TRAVERSABLE, 0);
	}

	private void initializePlayers() {
		String[] names = {"PlayerOne", "PlayerTwo"};
		players = new LandBaron[names.length];
		turn = new LinkedList<LandBaron>();

		for(int i = 0; i < names.length; i++) 
			players[i] = new LandBaron(names[i],BIDDABLE,TRAVERSABLE, calculateBudget());

		for(int i = 0; i < players.length;i++)
			turn.add(players[i]);		
	}

	private void initializeBoard() {
		board = new LandNode[size][size];
		constructBoard();
		addSpecialNodes();
		connectBoard();
	}

	private int calculateBudget() {
		return (size-2)*(size-2)*2;
	}

	private void constructBoard() {
		for(int row = 1; row < board.length-1; row++) {
			for(int col = 1; col < board[row].length-1;col++) {
				board[row][col] = new LandNode(npcBarons[UNAWARE_LAND_OWNER]);
			}
		}
		board[1][1].setOwnership(npcBarons[ORIGIN]);
		board[size-2][size-2].setOwnership(npcBarons[ORIGIN]);

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
		resetForDijkstra();
		dijkstra();

		LandNode previous = board[size-2][size-2];
		while(previous != null) {
			previous.getOwnership().increaseProfit(previous.getBid()*10);
			previous = previous.getPrevious();
		}


		//TODO: for each node in path, sum player one and player 2
		//TODO: Add unspent budgets to their players sums*10
		//TODO: Inform listeners that the game is done and disallow further moves game moves

		boolean gameFinished = true;
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
							board[row][col].setOwnership(npcBarons[THE_PUBLIC]);
							failedToAddTile = false;
						}else {
							board[row][col].setOwnership(npcBarons[THE_COMPANY]);
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
		return board[row][col].getOwnership().equals(npcBarons[UNAWARE_LAND_OWNER]);
	}

	private void testWinnability() {
		//TODO: Use cheapestPaths result to determine that a path exists
		//TODO: Change winnable accordingly
	}

	private void dijkstra(){

		//TODO: Write Djikstras

	}

	private boolean isBiddableTile(int row, int col) {
		return board[row][col].getOwnership().isBiddable();
	}

	private boolean isAffordableBid(LandBaron player, int row, int col) {
		if(board[row][col].getOwnership().equals(player)) {
			return player.getBudget() > 0;
		}else {
			return player.getBudget() > board[row][col].getBid();
		}
	}

	private void fireInvalidMoveDueToUnbiddableTileEvent(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		String landOwner = board[row][col].getOwnership().getName();
		this.pcs.firePropertyChange("I " + "U " + player.getName() + " " + outwardBoundRow
				+ " " + outwardBoundColumn + " " + landOwner,false, true);
	}

	private void fireInvalidMoveDueToMoneyEvent(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("I " + "$ " + player.getName() + " " + outwardBoundRow 
				+ " " + outwardBoundColumn, player.getBudget(), board[row][col].getBid());

	}

	private void fireValidMove(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("V " + "# " + player.getName() + " " + outwardBoundRow 
				+ " " + outwardBoundColumn,	false, true);
	}



	public String toString() {
		String s = "";
		if(gameFinished) {
			s += "The game has ended!\n";

		}else {
			s+= "It is " + turn.peek().getName() +"'s turn";
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

	private static final boolean TRAVERSABLE = true;
	private static final boolean BIDDABLE = true;

	private static final int THE_COMPANY = 0;
	private static final int THE_PUBLIC = 1;
	private static final int UNAWARE_LAND_OWNER = 2;
	private static final int ORIGIN = 3;

	private static final int PLAYER_ONE = 0;
	private static final int PLAYER_TWO = 1;

	private static final int ABOVE = 0;
	private static final int RIGHT_OF = 1;
	private static final int BELOW = 2;
	private static final int LEFT_OF = 3;

}
