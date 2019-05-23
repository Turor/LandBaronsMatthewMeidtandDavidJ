package application;

/**
 * Game entity which can own tiles in the game Land Baron
 * @author Matthew Meidt
 * @version Spring 2019
 *
 */
public class LandBaron implements Comparable<LandBaron> {

	/**What is the name of this entity?*/
	private String name;

	/**Can someone bid on this entities land?*/
	private boolean biddable;

	/**Can the company buy this land at the end of the game?*/
	private boolean traversable;

	/**How much money does this entity have left to bid?*/
	private int budget;

	/**How much money did this entity make?*/
	private int profit;
	
	/**What is this Land Baron's rank in the game?*/
	private int rank;

	/**Did this Land Baron win?*/
	private boolean victor;

	/**
	 * @param name - What is this Land Baron's name?
	 * @param biddability - Can someone bid on this Land Baron's land?
	 * @param traversability - Can the company buy this Land Baron's land?
	 * @param budget - How much money does Land Baron have left to bid?
	 */
	public LandBaron(String name, boolean biddability, boolean traversability, int budget) {
		this.biddable = biddability;
		this.traversable = traversability;
		this.budget = budget;
		this.name = name;
		this.rank = -1;
	}

	/**
	 * @param change How much money did this Land Baron get back?
	 */
	public void increaseBudget(int change) {
		budget += change;
	}

	/**
	 * @param change How much money did this bid cost?
	 */
	public void decreaseBudget(int change) {
		budget -= change;
	}

	/**The game has been reset*/
	public void resetBudget(int budget) {
		this.budget = budget;
	}

	/**How much money does this Land Baron have left to bid?*/
	public int getBudget() {
		return budget;
	}

	/**@return The name of the Land Baron*/
	public String toString() {
		return name;
	}

	/**@return The name of the Land Baron*/
	public String getName() {
		return name;
	}

	/**Change the baron's name*/
	public void changeName(String name) {
		this.name = name;
	}

	/**@return How much money did this baron make?*/
	public int getProfit() {
		return profit;
	}

	/**@param How much did the sale of a land tile increase the baron's profit?*/
	public void increaseProfit(int profit) {
		this.profit += profit;
	}

	/**@param How much profit did this baron lose?*/
	public void decreaseProfit(int cost) {
		this.profit -= cost;
	}

	/**What was this baron's final profit?*/
	public void setProfit(int profit) {
		this.profit = profit;
	}

	/**This Land Baron won*/
	public void setAsVictor() {
		victor = true;
	}
	
	/**
	 * @param This is the rank the Land Baron achieved
	 * */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * @return Did this baron win?
	 * */
	public boolean isVictor() {
		return victor;
	}

	/**
	 * The game has been reset
	 */
	public void resetProfit() {
		profit = 0;
	}

	public void resetVictor() {
		victor = false;
	}
	
	public void resetRank() {
		rank = -1;
	}
	
	/**
	 * @return What place did the baron get?
	 * */
	public int getRank() {
		return rank;
	}
	
	public void newGame(String name, int budget) {
		changeName(name);
		resetGame(budget);
	}

	public void resetGame(int budget) {
		resetProfit();
		resetVictor();
		resetRank();
		resetBudget(budget);
	}
	

	/**
	 * @return Can a player buy their land?
	 */
	public boolean isBiddable() {
		return biddable;
	}

	/**
	 * 
	 * @return Can a path move through their land?
	 */
	public boolean isTraversible() {
		return traversable;
	}

	/**
	 * @return ordering information
	 * -1: This baron made more money than the other baron, thus they are closer to 1st
	 *  0: This baron made the same amount of money as the other baron, thus the order doesn't matter
	 *  1: This baron made less than the other baron, thus they are lower ranked than the other baron
	 */
	@Override
	public int compareTo(LandBaron other) {
		if(this.getProfit() > other.getProfit())
			return -1;
		else if(this.getProfit() == other.getProfit()) 
			return 0;
		else
			return 1;
	}
}
