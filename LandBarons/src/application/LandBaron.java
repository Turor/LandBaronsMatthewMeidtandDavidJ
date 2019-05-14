package application;

public class LandBaron implements Comparable<LandBaron> {

	private String name;

	private boolean biddable;

	private boolean traversable;

	private int budget;

	private int profit;
	
	private int rank;

	private boolean victor;

	public LandBaron(String name, boolean biddability, boolean traversability, int budget) {
		this.biddable = biddability;
		this.traversable = traversability;
		this.budget = budget;
		this.name = name;
		this.rank = -1;
	}

	public void increaseBudget(int change) {
		budget += change;
	}

	public void decreaseBudget(int change) {
		budget -= change;
	}

	public void resetBudget(int budget) {
		this.budget = budget;
	}

	public int getBudget() {
		return budget;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public void changeName(String name) {
		this.name = name;
	}

	public int getProfit() {
		return profit;
	}

	public void increaseProfit(int profit) {
		this.profit += profit;
	}

	public void decreaseProfit(int cost) {
		this.profit -= cost;
	}

	public void setProfit(int profit) {
		this.profit = profit;
	}

	public void setAsVictor() {
		victor = true;
	}
	
	public void setRank(int rank) {
		this.rank = rank;
	}

	public boolean isVictor() {
		return victor;
	}

	public void resetProfit() {
		profit = 0;
	}

	public void resetVictor() {
		victor = false;
	}
	
	public void resetRank() {
		rank = -1;
	}
	
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
