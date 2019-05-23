package application;

import javafx.scene.control.Button;

/**
 * Extends a button so that the button can store coordinates
 * @author Matthew Meidt
 * @author David Jacobson
 * @version Spring 2019
 *
 */
public class LandButton extends Button {
	
	private int row;
	
	private int col;
	
	public LandButton(String label) {
		super();
		this.setText(label);
	}
	
	public int getRow() {
		return row;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}

}
