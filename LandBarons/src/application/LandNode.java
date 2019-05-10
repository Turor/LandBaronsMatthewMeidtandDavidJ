package application;


public class LandNode implements Comparable<LandNode> {

	/**
	 * 	-2:	Land is public land, and cannot be bought or traversed
	 * 	-1:	Land is already owned by the company and cannot be bid on, and is traversed
	 *   	  free of charge
	 *	 0:	Land is currently
	 *   1: Land is currently owned by player 1
	 *   2: Land is currently owned by player 2
	 */
	private int ownership;

	private int bid;

	private int maximumConnections;

	private int priority;
	
	public final static boolean FAILURE = false;
	
	public final static boolean SUCCESS = true;
	
	

	private LandNode prev;

	
	/**
	 * 0:	Refers to the node above the current node
	 * 1: 	Refers to the node to the right of the current node
	 * 2:	Refers to the node below the current node
	 * 3: 	Refers to the node to the left of the current node
	 */
	private LandNode[] connections;
	
	/**Cheaper reset*/
	private LandNode[] disconnectedNodes;

	public LandNode() {
		ownership = 0;
		maximumConnections = 4;
		connections = new LandNode[maximumConnections];
		disconnectedNodes = new LandNode[maximumConnections];
		bid = 0;
		priority = 0;
		prev = null;
	}

	public int getOwnership() {
		return ownership;
	}

	public void connectNodes(LandNode neighbor, int direction) {
		connections[direction] = neighbor;
	}

	public int getBid() {
		return bid;
	}

	public void makeBid(int owner) {
		bid++;
		priority++;
		ownership = owner;
	}
	
	public int getConnectionCount() {
		int connectionCount = 0;
		for(int i = 0; i < connections.length; i++)
			if(connections[i] != null)
				connectionCount++;
		return connectionCount;
	}

	/**
	 * Index using  0th indexing scheme
	 * @param desired - Indexed using 0th indexing scheme
	 * @return
	 */
	public LandNode connection(int desired) {
		int encounters = 0;
		for(int i = 0; i < connections.length ; i++ ) {
			if(connections[i] != null) {	
				if(encounters == desired)
					return connections[i];
				encounters++;
			}
		}
		return null;

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
	
	public boolean setOwnership(int newOwner) {
		if(newOwner < -3 || newOwner > 2)
			return FAILURE;
		else if(ownership < 0)
			return FAILURE;
		else
			ownership = newOwner;
		return SUCCESS;
	}
	
	public void setPrevious(LandNode newPrev) {
		prev = newPrev;
	}
	
	public void disableMe(LandNode other) {
		for(int i = 0; i < connections.length;i++) {
			if(other.equals(connections[i])) {
				disconnectedNodes[i] = connections[i];
				connections[i] = null;
			}
		}
	}
	
	public void reset() {
		resetConnections();
		bid = 0;
		ownership = 0;
		priority = 0;
		prev = null;
	}
	
	private void resetConnections() {
		for(int i = 0; i < disconnectedNodes.length;i++) {
			if(disconnectedNodes[i] != null) {
				connections[i] = disconnectedNodes[i];
				disconnectedNodes[i] = null;
			}
		}
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
		s += priority + ", " + ownership;
		return s;
	}


}
