package application;

import java.util.ArrayList;

public class LandNode implements Comparable<LandNode> {

	/**
	 * 	-2:	Land is public land, and cannot be bought or traversed
	 * 	-1:	Land is already owned by the company and cannot be bid on, and is traversed
	 *   	  free of charge
	 *	 0:	Land is currently
	 *   1: Land is currently owned by player 1
	 *   2: Land is currently owned by player 2
	 */
	private LandBaron ownership;

	private int bid;

	private int priority;

	public final static boolean FAILURE = false;

	public final static boolean SUCCESS = true;
	
	private LandBaron initialOwner;

	private LandNode prev;

	private ArrayList<LandNode> connections;
	
	private boolean finished;
	
	private Coordinates coordinates;

	//Landnode constructor
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
	
	//Returns coordinates
	public Coordinates getCoordinates() {
		return coordinates;
	}

	//Returns the ownership of the Land Node
	public LandBaron getOwnership() {
		return ownership;
	}
	
	//Increases the budget by a certain amount
	public void increaseBudget(int value) {
		ownership.increaseBudget(value);
	}
	//Decreases the budget by a certain amount
	public void decreaseBudget(int value) {
		ownership.decreaseBudget(value);
	}
	//Determines if the land node is biddable
	public boolean isBiddable() {
		return ownership.isBiddable();
	}
	
	//Increases the prophet by a certain amount
	public void increaseProfit(int value) {
		ownership.increaseProfit(value);
	}
	//Determines if the node is traversible
	public boolean isTraversible() {
		return ownership.isTraversible();
	}
	
	//Returns the name of the land node
	public String getName() {
		return ownership.getName();
	}

	//Connects this node with a neighboring node in a certain direction
	public void connectNodes(LandNode neighbor, int direction) {
		connections.add(neighbor);
	}

	//Returns the current bid of the land node
	public int getBid() {
		return bid;
	}
	
	
	public boolean isFinished() {
		return finished;
	}
	public void finish() {
		finished = true;
	}

	//Makes a bid on the land, depending on who owns the land
	public int makeBid(LandBaron owner) {
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
			return bid;
	}
	//Returns the number of connections to the land node
	public int getConnectionCount() {
		return connections.size();
	}

	/**
	 * Index using  0th indexing scheme
	 * @param desired - Indexed using 0th indexing scheme
	 * @return
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

	//Returns the priority of the Land Node
	public int getPriority() {
		return priority;
	}

	//Sets the priority of the land node
	public void setPriority(int newpriority) {
		priority = newpriority;
	}

	//Returns the previous node to the current land node (Used for Dijkstra)
	public LandNode getPrevious() {
		return prev;
	}

	//Sets the ownership of the land node
	public void setOwnership(LandBaron newOwner) {
		ownership = newOwner;
	}
	
	//Sets the previous node to the current land node
	public void setPrevious(LandNode newPrev) {
		prev = newPrev;
	}
	
	//Resets the data of the land node
	public void reset() {
		resetForDijkstra();
		bid = 0;
		ownership = initialOwner;
	}

	//The compare to method for comparing nodes to each other
	@Override
	public int compareTo(LandNode other) {
		// TODO Auto-generated method stub
		if(priority > other.getPriority())
			return 1;
		else if(priority < other.getPriority())
			return -1;
		else
			return 0;
	}

	//The to string method for information about the land node
	public String toString() {
		String s = "";
		//s += ownership + "," + bid;
		s += ownership;
		return s;
	}


}
