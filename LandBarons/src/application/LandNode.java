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

	public LandNode(LandBaron owner) {
		initialOwner = owner;
		ownership = owner;
		finished = false;
		connections = new ArrayList<LandNode>();
		bid = 0;
		priority = 0;
		prev = null;
	}

	public LandBaron getOwnership() {
		return ownership;
	}
	
	public void increaseBudget(int value) {
		ownership.increaseBudget(value);
	}
	
	public void decreaseBudget(int value) {
		ownership.decreaseBudget(value);
	}
	
	public boolean isBiddable() {
		return ownership.isBiddable();
	}
	
	public void increaseProfit(int value) {
		ownership.increaseProfit(value);
	}
	
	public boolean isTraversible() {
		return ownership.isTraversible();
	}
	
	public String getName() {
		return ownership.getName();
	}

	public void connectNodes(LandNode neighbor, int direction) {
		connections.add(neighbor);
	}

	public int getBid() {
		return bid;
	}
	
	public boolean isFinished() {
		return finished;
	}
	public void finish() {
		finished = true;
	}

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

	public int getPriority() {
		return priority;
	}

	public void setPriority(int newpriority) {
		priority = newpriority;
	}

	public void setPrev(LandNode newprev) {
		prev = newprev;
	}

	public LandNode getPrevious() {
		return prev;
	}

	public void setOwnership(LandBaron newOwner) {
		ownership = newOwner;
	}

	public void setPrevious(LandNode newPrev) {
		prev = newPrev;
	}
	
	public void reset() {
		resetForDijkstra();
		bid = 0;
		ownership = initialOwner;
	}

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

	public String toString() {
		String s = "";
		//s += ownership + "," + bid;
		s += ownership;
		return s;
	}


}
