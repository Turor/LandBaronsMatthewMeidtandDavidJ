package application;
	
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;


public class LandBaronsView extends Application implements EventHandler<ActionEvent>, PropertyChangeListener {
	private LandBaronsModel model;
	private Button reset;
	private ComboBox sizeCB;
	private Label gameState;
	private Label lastMove;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();
			Scene scene = new Scene(root,400,400);
			sizeCB = new ComboBox<Integer>();
			sizeCB.getItems().addAll(4,5,7,10,14);
			
			
			
			model = new LandBaronsModel(sizeCB);
			
			//TOP BOX
			GridPane board = new GridPane();
			//Using placer 'n' variable which will be given by the constructor
			for(int i = 0; i < model.getSize(); i++) {
				for(int j = 0; j < model.getSize(); j++) {
					board.add(new Button(), i, j);
				}
			}
			root.getChildren().add(board);
			root.getChildren().add(lastMove);
			
			//BOTTOM BOX
			GridPane botBox = new GridPane();
			//Reset Button
			botBox.add(reset, 1, 0);
			reset.setOnAction(this);
			//Combo Box for Size
			
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handle(ActionEvent e) {
		if(e.getSource() == sizeCB) {
			//attempt to set size
		}
		if(e.getSource() == reset) {
			//reset
		}
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
