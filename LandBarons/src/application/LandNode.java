package application;


public class LandNode {

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
		ownership = owner;
	}
	
	public int getConnections() {
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


}
