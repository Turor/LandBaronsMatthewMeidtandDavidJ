package application;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class InitializationTests implements PropertyChangeListener {

	@Test
	public void printBoard() {
		LandBaronsModel temp = new LandBaronsModel(5);
		temp.addPropertyChangeListener(this);
		System.out.println(temp.printBoard());
		for(int cycle = 0; cycle < 50; cycle++)
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {
					temp.move(0,1);
					temp.move(0,2);
				}
			}
		System.out.println(temp.printBoard());

	}


	//Controller Stuff//
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		//Reads the information from the event
		Scanner friend = new Scanner(event.getPropertyName());
		char actionValidity = friend.next().charAt(0);
		char typeOfAction = friend.next().charAt(0);
		int player = friend.nextInt();
		int row = friend.nextInt();
		int col = friend.nextInt();
		
		if(isInvalidAction(actionValidity))
			if(isUnbiddable(typeOfAction))
				System.out.println("That move by player " + player + " was invalid because "
						+ row + "," + col + " is owned by the " + friend.next());
			else
				System.out.println("That move was invalid because player " + player + " did"
						+ " not have enough money to bid on " + row + "," + col);
		else
			System.out.println("Player " + player+ " performed a successful move at "
					+ row + "," + col);
		friend.close();
	}
	
	private boolean isInvalidAction(char identifier) {
		return 'I' == identifier;
	}
	
	private boolean isUnbiddable(char identifier) {
		return 'U' == identifier;
	}





}
