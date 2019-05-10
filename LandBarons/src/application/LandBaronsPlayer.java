package application;

public class LandBaronsPlayer {
	
	private int budget;
	
	private String name;
	
	public LandBaronsPlayer(String name, int budget) {
		this.budget = budget;
		this.name = name;
	}
	
	public String toString() {
		return name + " has " + budget + "$ left to bid.";
	}
	
	public int getBudget() {
		return budget;
	}
	
	public void decreaseBudget(int value) {
		budget -= value;
	}
	
	public void increaseBudget(int value) {
		budget += value;
	}
	
	public void resetBudget(int budget) {
		this.budget = budget;
	}
}
