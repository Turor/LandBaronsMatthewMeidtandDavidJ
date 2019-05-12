package application;

public class LandBaron {
	
	private String name;
	
	private boolean biddable;
	
	private boolean traversable;
	
	private int budget;
	
	private int profit;
	
	private boolean victor;
	
	public LandBaron(String name, boolean biddability, boolean traversability, int budget) {
		this.biddable = biddability;
		this.traversable = traversability;
		this.budget = budget;
		this.name = name;
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
	
	public void setProfit(int profit) {
		this.profit = profit;
	}
	
	public void setAsVictor() {
		victor = true;
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
	
	public void newGame(String name, int budget) {
		changeName(name);
		resetGame(budget);
	}
	
	public void resetGame(int budget) {
		resetProfit();
		resetVictor();
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

}
