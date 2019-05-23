package application;

import java.util.ArrayList;

/**
 * Represents a tile in the game of Land Barons
 * @author Matthew Meidt
 * @author David Jacobson
 * @version Spring 2019
 *
 */
public class LandNode implements Comparable<LandNode> {

	/**Which entity currently owns me?*/
	private LandBaron ownership;

	/**How much am I worth?*/
	private int bid;

	/**How expensive is it to reach me?*/
	private int priority;

	public final static boolean FAILURE = false;

	public final static boolean SUCCESS = true;
	
	/**Who should I revert ownership to when I'm reset?*/
	private LandBaron initialOwner;

	/**What is my cheapest path to the source?*/
	private LandNode prev;

	/**Who am I connected to?*/
	private ArrayList<LandNode> connections;
	
	/**Have I been processed by Dijkstra?*/
	private boolean finished;
	
	/**This is where I am*/
	private final Coordinates coordinates;

	/**
	 * 
	 * @param owner Who is my default owner?
	 * @param row Where am I on the board?
	 * @param column Where am I on the board?
	 */
	public LandNode(LandBaron owner, int row, int column) {
		initialOwner = owner;
		ownership = owner;
		coordinates = new Coordinates(row,column);
		finished = false;
		connections = new ArrayList<LandNode>();
		bid = 0;
		priority = 0;
		prev = null;
	}
	
	/**
	 * Where is the tile located on the game board
	 * @return A set of final coordinates
	 */
	public Coordinates getCoordinates() {
		return coordinates;
	}

	/**
	 * @return The tile's current owner
	 */
	public LandBaron getOwnership() {
		return ownership;
	}
	
	/**
	 * Increases the budget of the tile's current owner
	 * @param value
	 */
	private void increaseBudget(int value) {
		ownership.increaseBudget(value);
	}
	
	/**
	 * Decreases the budget of the tile's current owner
	 * @param value
	 */
	private void decreaseBudget(int value) {
		ownership.decreaseBudget(value);
	}
	
	/**
	 * @return Can this tile be bid on?
	 */
	public boolean isBiddable() {
		return ownership.isBiddable();
	}
	
	/**
	 * This tile is located on the cheapest path, thus the owner gains a profit
	 * @param value
	 */
	public void increaseProfit(int value) {
		ownership.increaseProfit(value);
	}

	/**
	 * @return Can the tile be located on the cheapest path?
	 */
	public boolean isTraversible() {
		return ownership.isTraversible();
	}
	
	/**
	 * @return The name of the owner of the tile
	 */
	public String getName() {
		return ownership.getName();
	}

	/**
	 * @param neighbor Adds a connection to a neighboring node
	 */
	public void connectNodes(LandNode neighbor) {
		connections.add(neighbor);
	}

	/**
	 * @return How much this node currently costs
	 */
	public int getBid() {
		return bid;
	}
	
	/**
	 * @return Has this node been processed in a cheapest path calculation?
	 */
	public boolean isFinished() {
		return finished;
	}
	/**
	 * This node has now been processed in a cheapest path calculation
	 */
	public void finish() {
		finished = true;
	}

	/**
	 * Process a bid on the tile
	 * @param owner - Who made the new bid?
	 */
	public void makeBid(LandBaron owner) {
		if(owner.equals(ownership)) {
			decreaseBudget(1);
		}else {
			increaseBudget(bid);
			ownership = owner;
			decreaseBudget(bid+1);
		}
			bid++;
			priority = bid;
			ownership = owner;
	}
	
	/**
	 * @return How many tiles is this node connected to?
	 */
	public int getConnectionCount() {
		return connections.size();
	}

	/**
	 * Index using  0th indexing scheme
	 * @param desired - Indexed using 0th indexing scheme
	 * @return The nth node that is connected to this node
	 */
	public LandNode connection(int desired) {
		return connections.get(desired);
	}

	/**
	 * Prepare the node for undergoing a cheapest path calculation
	 */
	public void resetForDijkstra() {
		prev = null;
		finished = false;
		priority = bid;
	}

	/**
	 * @return How much does it cost to reach this node?
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * @param newpriority - What is the cost of reaching this node?
	 */
	public void setPriority(int newpriority) {
		priority = newpriority;
	}

	/**
	 * @return What is the cheapest way to reach this node?
	 */
	public LandNode getPrevious() {
		return prev;
	}

	/**
	 * @param newOwner - Sets a new owner of the tile
	 */
	public void setOwnership(LandBaron newOwner) {
		ownership = newOwner;
	}
	
	/**
	 * @param newPrev - Updates the cheapest path to this node
	 */
	public void setPrevious(LandNode newPrev) {
		prev = newPrev;
	}
	
	/**
	 * Resets this node to its initial state
	 */
	public void reset() {
		resetForDijkstra();
		bid = 0;
		ownership = initialOwner;
	}

	/**
	 * @return 1 : This node is more expensive to reach than the other node
	 *  -1 : This node is cheaper to reach than the other node
	 *   0 : This node costs the same to reach as the other node
	 */
	@Override
	public int compareTo(LandNode other) {
		if(priority > other.getPriority())
			return 1;
		else if(priority < other.getPriority())
			return -1;
		else
			return 0;
	}

	/**
	 * Renders the name of the owner of the node
	 */
	public String toString() {
		String s = "";
		//s += ownership + "," + bid;
		s += ownership;
		return s;
	}


}
