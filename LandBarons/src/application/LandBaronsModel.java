package application;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * Performs all game logic for Land Barons
 * @author Matthew Meidt
 * @author David Jacobson
 * @version Spring 2019
 *
 */
public class LandBaronsModel {

	/**
	 * Size adds an extra 2 cells to the vertical and horizontal
	 * size to facilitate edge detection without using out of bounds
	 * exceptions
	 */
	private int size;
	
	/**The non-player controlled barons in the game*/
	private LandBaron[] npcBarons;

	/**The players in the game*/
	private LandBaron[] players;
	
	/**Who won the game, and what was their ranking?*/
	private LandBaron[] standings;

	/**Whose turn is it?*/
	private LinkedList<LandBaron> turn;

	/**Is the current configuration of the board winnable?*/
	private boolean winnable;

	/**Contains all the tiles in the game*/
	private LandNode[][] board;

	/**Was the previous move a pass?*/
	private boolean passedLastTurn;

	/**Is the game finished?*/
	private boolean gameFinished;

	/**Enables Listener-Observer pattern*/
	private PropertyChangeSupport pcs;

	/**Instantiates the model to a board with a particular size*/
	public LandBaronsModel(int size) {
		pcs = new PropertyChangeSupport(this);
		initializeModel(size);
	}

	/**Gives the StyleID of the person's whose turn it is*/
	public String getTurnID() {
		return getStylePreference(turn.peek());
	}

	/**
	 * @param size - What is the new board's size?
	 */
	public void changeSize(int size) {
		initializeModel(size);
		this.alertListenersBoardSizeWasChanged();
	}
	
	/**
	 * Gives pricing information of a particular tile
	 * @param row
	 * @param col
	 * @return - Current value of the tile at row and col
	 */
	public int getBid(int row, int col) {
		row++; col++; //No one knows we added a padding row and column
		return board[row][col].getBid();
	}
	
	/**
	 * Displays information useful to the players based on the tile
	 * @param row
	 * @param col
	 * @return The information the user needs
	 */
	public String getInfo(int row, int col) {
		row++;col++;
		if(board[row][col].isBiddable())
			return ""+board[row][col].getBid();
		else
			return board[row][col].toString();
	}
	
	/**
	 * Makes a move at a given tile
	 * @param row
	 * @param col
	 */
	public void move(int row, int col) {
		//TODO: Add move to the move stack to facilitate undos?
		row++; col++; //No one else knows we added a padding row and column
		if(!gameFinished)
			if(isBiddableTile(row, col))
				if(isAffordableBid(turn.peek(),row,col)) {
					executeMove(turn.peek(),row,col);
				}else {
					fireInvalidMoveDueToMoneyEvent(turn.peek(),row,col);
				}

			else {
				this.fireInvalidMoveDueToUnbiddableTileEvent(turn.peek(), row, col);		
			}
		else {
			this.fireInvalidMoveDueToGameBeingFinished(turn.peek(),row,col);
		}
	}

	/**
	 * Executes the move if it has been determined to be valid
	 * @param player - The player who made the move
	 * @param row
	 * @param col
	 */
	private void executeMove(LandBaron player, int row,int col) {

		board[row][col].makeBid(player);
		advanceTurn();
		passedLastTurn = false;
		if(turn.peek().getBudget() == 0)
			pass();
		this.fireValidMove(player, row, col);
	}
	/**
	 * @return How big is the board?
	 */
	public int getSize() {
		return size-2;
	}
	
	/**
	 * Performs a pass
	 */
	public void pass() {
		if(!gameFinished) {

			if(passedLastTurn) {
				gameFinished();
				advanceTurn();
			}else {
				passedLastTurn = true;
				LandBaron playerWhoPassed = turn.peek();
				advanceTurn();
				this.fireValidPass(playerWhoPassed);
			}

		}else {
			this.fireInvalidPassDueToGameBeingFinished(turn.peek());
		}
	}
	
	/**
	 * Who owns this tile?
	 * @param row
	 * @param col
	 * @return The name of the current owner of the targeted tile
	 *  PlayerOne, PlayerTwo, Unowned, Public, or Company
	 */
	public String getOwner(int row, int col) {
		row++; col++;
		return board[row][col].toString();
	}

	private void advanceTurn() {
		turn.add(turn.poll());
	}
	
	/**
	 * Resets the board for a new game
	 */
	public void reset() {
		winnable = false;
		passedLastTurn = false;
		gameFinished = false;
		LandBaron playerWhoReset = turn.peek();
		resetPlayers();
		addSpecialNodes();
		this.alertListenersGameWasReset(playerWhoReset);
	}

	/**
	 * Resets player information
	 */
	private void resetPlayers() {
		turn = new LinkedList<LandBaron>();
		for(int player = 0; player < players.length;player++) {
			players[player].resetGame(calculateBudget());
			turn.add(players[player]);
		}
	}
	
	/**Resets nodes in preparation for a cheapest path calculation*/
	private void resetForDijkstra() {
		for(int row = 1; row < size-1; row++) {
			for(int col = 1; col < size-1; col++) {
				board[row][col].resetForDijkstra();
			}
		}
	}

	/**Handles board setup that is common to size changes and initialization*/
	private void initializeModel(int size) {

		this.size = size+2;
		winnable = false;

		initializeLandBarons();
		initializeBoard();

		passedLastTurn = false;
		gameFinished = false;
	}

	/**Initializes both players and non players*/
	private void initializeLandBarons() {	
		initializeNPCS();
		initializePlayers();
	}
	
	/**Creates non-player LandBarons*/
	private void initializeNPCS() {
		npcBarons = new LandBaron[5];
		npcBarons[0] = new LandBaron("The Company", !BIDDABLE,TRAVERSABLE, 0);
		npcBarons[1] = new LandBaron("The Public", !BIDDABLE,!TRAVERSABLE,0);
		npcBarons[2] = new LandBaron("The Uninformed Faction", BIDDABLE,TRAVERSABLE,0);
		npcBarons[3] = new LandBaron("The Origin", !BIDDABLE, TRAVERSABLE, 0);
		npcBarons[4] = new LandBaron("The Destination", !BIDDABLE, TRAVERSABLE,0);
	}

	/**Initializes Player one and Player Two*/
	private void initializePlayers() {
		String[] names = {"PlayerOne", "PlayerTwo"};
		players = new LandBaron[names.length];
		turn = new LinkedList<LandBaron>();

		for(int i = 0; i < names.length; i++) 
			players[i] = new LandBaron(names[i],BIDDABLE,TRAVERSABLE, calculateBudget());

		for(int i = 0; i < players.length;i++)
			turn.add(players[i]);		
	}

	/**Initializes the primary data structure*/
	private void initializeBoard() {

		board = new LandNode[size][size];
		constructBoard();
		makeDefaultConnections();
		addSpecialNodes();
	}

	/**Determines how large a player's budget is*/
	private int calculateBudget() {
		return (size-2)*(size-2)*2;
	}

	/**Initializes Land Nodes with no connections*/
	private void constructBoard() {
		for(int row = 1; row < board.length-1; row++) {
			for(int col = 1; col < board[row].length-1;col++) {
				board[row][col] = new LandNode(npcBarons[UNAWARE_LAND_OWNER], row-1, col-1);
			}
		}
	}

	/**Initializes Land Node connections*/
	private void makeDefaultConnections() {
		for(int row = 1; row < board.length -1; row++) {
			for(int col = 1; col < board[row].length-1;col++) {
				if(null != board[row-1][col] ) {
					board[row][col].connectNodes(board[row-1][col]); //Connect to upper neighbor
				}if( null != board[row][col+1]) {
					board[row][col].connectNodes(board[row][col+1]); //Connect to right neighbor
				}if( null != board[row+1][col]) {
					board[row][col].connectNodes(board[row+1][col]); //Connect to lower neighbor
				}if( null != board[row][col-1]) {
					board[row][col].connectNodes(board[row][col-1]); //Connect to left neighbor
				}
			}
		}
	}
	
	/**Handles end of game calculations*/
	private void gameFinished() {
		dijkstra();

		LandNode previous = board[size-2][size-2];
		while(previous != null) {
			previous.increaseProfit(previous.getBid()*10);
			previous = previous.getPrevious();
		}

		standings = new LandBaron[players.length];
		for(int player = 0; player < players.length; player++) {
			players[player].decreaseProfit(calculateBudget() - players[player].getBudget());
			standings[player] = players[player];
		}
		Arrays.sort(standings);

		//This calculation determines what the players ranks were
		standings[0].setRank(1);
		for(int i = 0; i < standings.length-1; i++) 
			if(standings[i+1].getProfit() == standings[i].getProfit())
				standings[i+1].setRank(standings[i].getRank());
			else
				standings[i+1].setRank(i+2);

		if(playersTied(standings[0],standings[1])) {
			if(playerAvoidedTakingALoss(standings[0])) { //A tie
				standings[0].setAsVictor();
				standings[1].setAsVictor();
			}//Both players lost as neither made a profit
			else if(playerAvoidedTakingALoss(standings[0])) //Someone won
				standings[0].setAsVictor();
		}else {
			if(playerAvoidedTakingALoss(standings[0]))
				standings[0].setAsVictor();
		}

		gameFinished = true;
		alertListenersGameIsFinished();
	}
	
	/**For the End dialogue, if a player's net profit was greater than 0*/
	private boolean playerAvoidedTakingALoss(LandBaron player) {
		return player.getProfit() >= 0;
	}
	
	/**If both players profit was equal*/
	private boolean playersTied(LandBaron firstPlayer, LandBaron secondPlayer) {
		return firstPlayer.getRank() == secondPlayer.getRank();
	}

	/**Calls each node's reset method*/
	private void resetNodes() {
		for(int row = 1; row < board.length-1;row++)
			for(int col = 1; col < board[row].length-1;col++)
				board[row][col].reset();
		board[1][1].setOwnership(npcBarons[ORIGIN]);
		board[size-2][size-2].setOwnership(npcBarons[DESTINATION]);
	}

	/**Called when creating the board, it adds the "special", unbiddable land tiles*/
	private void addSpecialNodes() {

		while(!winnable) {
			resetNodes();
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
			testWinnability();
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
	
	/**Checks for a special tile's existence at the given spot
	 * @return Is there no collision?
	 */
	private boolean noCollision(int row, int col) {
		return board[row][col].getOwnership().equals(npcBarons[UNAWARE_LAND_OWNER]);
	}
	
	/**Used to determine if a board is a valid board*/
	private void testWinnability() {
		dijkstra();

		//Evaluates whether or not a path exists to the destination from the source
		winnable = board[size-2][size-2].getPrevious() != null;
	}

	/**Implements the cheapest path calculation using Dijkstra's algorithm*/
	private void dijkstra(){
		resetForDijkstra();
		PriorityQueue<LandNode> pq = new PriorityQueue<LandNode>();
		pq.add(board[1][1]);
		while(!pq.isEmpty()) {
			LandNode nodeBeingProcessed = pq.poll();
			for(int connection = 0; connection < nodeBeingProcessed.getConnectionCount();
					connection++) {
				LandNode connectedNode = nodeBeingProcessed.connection(connection);
				if(!connectedNode.isFinished())
					if(connectedNode.isTraversible()) //Can a connection be entered?
						if(null != connectedNode.getPrevious()) { //Has this node been reached previously?
							if(connectedNode.getPriority() > nodeBeingProcessed.getPriority() 
									+ connectedNode.getBid()) { //Is this a cheaper path?
								pq.remove(connectedNode);
								updatePreviousNode(pq, nodeBeingProcessed, connectedNode);
							}		
						}else { //No node has connected to this node yet
							updatePreviousNode(pq, nodeBeingProcessed, connectedNode);
						}
			}
			nodeBeingProcessed.finish();
		}
	}

	/**Updates the cheapest path and adds node to queue to be processed*/
	private void updatePreviousNode(PriorityQueue<LandNode> pq, LandNode processingNode, LandNode connectedNode) {
		connectedNode.setPriority(processingNode.getPriority() + connectedNode.getBid());
		connectedNode.setPrevious(processingNode);
		pq.add(connectedNode);
	}
	
	/**
	 * 
	 * @param row
	 * @param col
	 * @return Is the targeted tile biddable?
	 */
	private boolean isBiddableTile(int row, int col) {
		return board[row][col].isBiddable();
	}

	/**
	 * 
	 * @param player 
	 * @param row
	 * @param col
	 * @return Does the player have enough money for this bid?
	 */
	private boolean isAffordableBid(LandBaron player, int row, int col) {
		if(board[row][col].getOwnership().equals(player)) {
			return player.getBudget() > 0;
		}else {
			return player.getBudget() > board[row][col].getBid();
		}
	}
	
	/**The action attempted was invalid because the tile wasn't biddable*/
	private void fireInvalidMoveDueToUnbiddableTileEvent(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		String landOwner = board[row][col].getName();
		this.pcs.firePropertyChange("I I M U " + player.getName() + " " + outwardBoundRow
				+ " " + outwardBoundColumn + " " + landOwner,false, true);
	}
	
	/**The action performed was invalid because the player didn't have enough money*/
	private void fireInvalidMoveDueToMoneyEvent(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("I I M $ " + player.getName() + " " + outwardBoundRow 
				+ " " + outwardBoundColumn, player.getBudget(), board[row][col].getBid()+1);

	}
	
	/**The move attempted by the player was valid*/
	private void fireValidMove(LandBaron player, int row, int col) {
		int outwardBoundRow = row - 1;
		int outwardBoundColumn = col-1;
		this.pcs.firePropertyChange("I V M - " + player.getName() + " " + outwardBoundRow 
				+ " " + outwardBoundColumn,	false, true);
	}
	
	/**The pass performed by the player was valid*/
	private void fireValidPass(LandBaron player) {
		this.pcs.firePropertyChange("I V P - " + player.getName(), false, true);
	}
	
	/**The player's pass failed because the game is already over*/
	private void fireInvalidPassDueToGameBeingFinished(LandBaron player) {
		this.pcs.firePropertyChange("F I P F " + player.getName(),false,true);
	}
	
	/**The player's action failed because the game is over*/
	private void fireInvalidMoveDueToGameBeingFinished(LandBaron player, int row, int col) {
		int outwardBoundRow = row -1;
		int outwardBoundColumn = col -1;
		this.pcs.firePropertyChange("F I M F " + player.getName() + " " + outwardBoundRow 
				+ " " + outwardBoundColumn , false,true);
	}

	/**The game was reset*/
	private void alertListenersGameWasReset(LandBaron player) {
		this.pcs.firePropertyChange("- - R - " + player.getName(),false, true);
	}
	
	/**The game is over*/
	private void alertListenersGameIsFinished() {
		this.pcs.firePropertyChange("F - - - "+ turn.peek().getName(),false,true);
	}

	/**The board's size was changed*/
	private void alertListenersBoardSizeWasChanged() {
		this.pcs.firePropertyChange("- - S - -", false, true);
	}

	/**What is the games current state?*/
	public String toString() {
		String s = "";
		if(gameFinished) {
			s += "The game has ended ";
			if(standings[0].isVictor()) 
				if(standings[1].isVictor()) 
					s+= "in a tie!\n";
				else 
					s+= "with " + standings[0].getName() + " winning!\n";
			else 
				s+= " in a total loss!\n";
			for(int rank = 0; rank < standings.length; rank++) {
				if(tiedWithSomeone(rank))
					s+= "T";
				s+= standings[rank].getRank() + ": ";
				s+= playerStatus(standings[rank]);
			}
		}else {
			s+= "It is " + turn.peek().getName() +"'s turn.\n";
			for(LandBaron player : players)
				s+= playerStatus(player);
		}
		return s;
	}

	/**Tells what user the target tile's style category should be*/
	public String getTileStyle(int row, int col) {
		row++; col++;
		return getStylePreference(board[row][col].getOwnership());
	}
	

	/**Determines the style class of a given entity*/
	private String getStylePreference(LandBaron entity) {
		String s = "id";
		LandBaron[] owners = new LandBaron[npcBarons.length + players.length];
		for(int i = 0; i < owners.length; i++) {
			if(i < npcBarons.length)
				owners[i] = npcBarons[i];
			else
				owners[i] = players[i-npcBarons.length];
		}

		for(int i = 0; i < owners.length; i++) {
			if(entity.equals(owners[i]))
				return s+i;
		}
		return s;
	}

	/**Determines if a tie exists within the standings*/
	private boolean tiedWithSomeone(int index) {
		if(index-1 >=0) {
			if(standings[index].getRank() == standings[index-1].getRank())
				return true;
		}else if(index +1 < standings.length) {
			if(standings[index].getRank() == standings[index+1].getRank())
				return true;
		}
		return false;
	}

	/**Reports on the player status*/
	private String playerStatus(LandBaron player) {
		String s = player.getName() + " has ";
		if(gameFinished) {
			s+= "ended the game with ";
			if(player.getProfit() < 0)
				s+= "a loss of " + player.getProfit() +"$!";
			else if(player.getProfit() == 0)
				s+= "neither a profit nor a loss.";
			else
				s+= "a profit of " + player.getProfit() +"$!";

		}else {
			s+= player.getBudget() + "$ left to bid";		
		}
		s+="\n";
		return s;
	}

	/**
	 * @return - Copy of the game board, useful for debugging
	 */
	public LandNode[][] getBoardClone(){
		return board.clone();
	}
	
	
	/**@return Textual representation of the game board*/
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
	
	/**@return textual representation of the shortest path*/
	public String printShortestPath() {
		String s = "";

		LandNode previous = board[size-2][size-2];
		int counter = 0;
		while(null != previous) {

			s+= previous.getOwnership().getName() + " -> ";
			previous = previous.getPrevious();
			counter++;
			if(counter%5 == 0)
				s+= "\n";
		}

		return s;
	}
	
	/**Data representation of the shortest path*/
	public LinkedList<Coordinates> getShortestPath(){
		LinkedList<Coordinates> path = new LinkedList<Coordinates>();
		LandNode previous = board[size-2][size-2];
		while(null != previous) {
			path.add(previous.getCoordinates());
			previous = previous.getPrevious();
		}
		return path;
	}

	/**Provides a way for an object to subscribe to this object*/
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**Provides a way for an object to unsubscribe to this object*/
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	private static final boolean TRAVERSABLE = true;
	private static final boolean BIDDABLE = true;

	private static final int THE_COMPANY = 0;
	private static final int THE_PUBLIC = 1;
	private static final int UNAWARE_LAND_OWNER = 2;
	private static final int ORIGIN = 3;
	private static final int DESTINATION = 4;
}
