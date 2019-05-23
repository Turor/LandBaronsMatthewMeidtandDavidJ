package application;

/**
 * Packages row and column information into a single object
 * @author Matthew Meidt
 * @version Spring 2019
 */
public class Coordinates {
	
	private final int row;
	
	private final int column;
	
	public Coordinates(int row, int column) {
		this.row = row;
		this.column = column;
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return column;
	}

}
