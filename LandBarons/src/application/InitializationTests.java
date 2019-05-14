package application;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

public class InitializationTests implements PropertyChangeListener {

	private LandBaronsModel temp;

	@Test
	public void printBoard() {
		temp = new LandBaronsModel(5);
		temp.addPropertyChangeListener(this);
		System.out.println(temp.printBoard());
		for(int cycle = 0; cycle < 7; cycle++)
			for(int i = 0; i < 5; i++) {
				for(int j = 0; j < 5; j++) {
					temp.move(i,j);
				}
			}
		temp.pass();
		temp.pass();
		System.out.println(temp.printShortestPath());
		System.out.println(temp.printBoard());
		System.out.println(temp);
		temp.reset();
		System.out.println(temp.printBoard());
		temp.changeSize(5);

	}


	//Controller Stuff//
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		//Property Name format: gameProgress validity action error actor *row* *column*
		Scanner friend = new Scanner(event.getPropertyName());
		char gameProgress = friend.next().charAt(0);
		char actionValidity = friend.next().charAt(0);
		char typeOfAction = friend.next().charAt(0);
		char errorType = friend.next().charAt(0);
		String player = friend.next();

		int row = -1, col = -1;
		if(actionWasAMove(typeOfAction)) {
			row = friend.nextInt();
			col = friend.nextInt();
		}
		printEventInfo(gameProgress, actionValidity, typeOfAction, errorType, player, row, col);
		friend.close();
	}

	private String printEventInfo(char gameProgress, char validity, char action, char errorType,
			String player, int row, int col) {
		String s = "";

		if(actionWasAReset(action)) {
			resetOccurred();
		}else if(actionWasASizeChange(action)) {
			sizeChangeOccurred();
		}else if(gameIsFinished(gameProgress)) {
			if(actionWasAMove(action)) {
				//TODO: Alert the user that the game is finished, thus a move isn't allowed
			}else if(actionWasAPass(action)) {
				//TODO: Alert the user that the game is finished, thus a pass isn't allowed
			}else {
				//TODO: Update the GUI based on the finished game info
			}
		}else { //Game is still in progress
			if(isValidAction(validity)) {
				if(actionWasAMove(action)) {
					//TODO: Process the player's move
				}else if(actionWasAPass(action)) {
					//TODO: Process the player's pass
				}
			}else { 
				if(isErrorUnbiddable(errorType)) {
					//TODO: Inform the user that their bid failed because the tile was unbiddable
				}else if(isErrorTooLittleMoney(errorType)) {
					//TODO: Inform the user that their bid failed because they had too little money
				}
			}
		}
		return s;
	}

	private void resetOccurred() {
		//TODO: Inform the user a reset occurred
		//TODO: Update the board based on the model
	}

	private void sizeChangeOccurred() {
		//TODO: Inform the user that the game size was updated
		//TODO: Update the GUI based on the new size
	}

	private boolean isValidAction(char actionValidity) {
		return 'V' == actionValidity;
	}

	private boolean isInvalidAction(char actionValidity) {
		return 'I' == actionValidity;
	}

	private boolean isErrorUnbiddable(char errorType) {
		return 'U' == errorType;
	}

	private boolean isErrorTooLittleMoney(char errorType) {
		return '$' == errorType;
	}

	private boolean actionWasAMove(char action) {
		return 'M' == action;
	}

	private boolean actionWasAPass(char action) {
		return 'P' == action;
	}

	private boolean actionWasAReset(char action) {
		return 'R' == action;
	}

	private boolean actionWasASizeChange(char action) {
		return 'S' == action;
	}

	private boolean gameIsFinished(char gameProgress) {
		return 'F' == gameProgress;
	}





}
