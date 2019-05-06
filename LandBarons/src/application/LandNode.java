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
	
	private int currentBid;
	
	private int maximumConnections;
	
	/**
	 * 0:	Refers to the node above the current node
	 * 1: 	Refers to the node to the right of the current node
	 * 2:	Refers to the node below the current node
	 * 3: 	Refers to the node to the left of the current node
	 */
	private LandNode[] connectedNodes;
	private LandNode[] disconnectedNodes;
	
	public LandNode() {
		ownership = 0;
		maximumConnections = 4;
		connectedNodes = new LandNode[4];
		disconnectedNodes = new LandNode[4];
		currentBid = 0;
	}
	
	public int getOwnership() {
		return ownership;
	}
	
	public void connectNodes(LandNode neighbor) {
		//TODO: Remember
	}
	
	public int getPrice() {
		//TODO: Do we want to return the current bid or the current price to outbid?
		return 0;
	}


}
