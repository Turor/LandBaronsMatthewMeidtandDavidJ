package application;
	
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.LinkedList;
import java.util.Scanner;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;


public class LandBaronsView extends Application implements EventHandler<ActionEvent>, PropertyChangeListener {
	private LandBaronsModel model;
	private Button reset;
	private ComboBox<Integer> sizeCB;
	private TextArea lastMove;
	private GridPane board;
	private TextArea feedback;
	private Button pass;
	private final double width = 800;
	private final double height = 600;
	private Rectangle turnIndicator;

	private LandButton[][] buttons;
	
	@Override
	public void start(Stage primaryStage) {
		try {
			VBox root = new VBox();

			Scene scene = new Scene(root,width,height);
			primaryStage.setTitle("The Land Barons Game");
			sizeCB = new ComboBox<Integer>();
			sizeCB.setPrefWidth(width/4);
			sizeCB.setPrefHeight(height/12);
			sizeCB.setOnAction(this);
			sizeCB.getItems().addAll(4,5,7,10,14);
			
			
			
			model = new LandBaronsModel(4); //TODO: Use ComboBox for first value;
			model.addPropertyChangeListener(this);
			
			//TOP BOX
			board = new GridPane();
			//Using placer 'n' variable which will be given by the constructor
			root.getChildren().add(board);
			generateGameBoard();
			
			
			lastMove = new TextArea("Game Start");
			lastMove.setPrefHeight(scene.getHeight()/12);
			lastMove.setPrefWidth(scene.getWidth());
			root.getChildren().add(lastMove);
			
			
			//BOTTOM BOX
			GridPane botBox = new GridPane();
			pass = new Button("Pass");
			pass.setOnAction(this);
			pass.setPrefWidth(width/4);
			pass.setPrefHeight(height/12);
			
			
			turnIndicator = new Rectangle();
			turnIndicator.setWidth(width/4);
			turnIndicator.setHeight(height/4);
			turnIndicator.setId(model.getTurnID());
			
			
			
			
			//Reset Button
			reset = new Button("Reset");
			reset.setPrefWidth(width/4);
			reset.setPrefHeight(height/12);
			
			reset.setOnAction(this);
			//Combo Box for Size
			
			//Feedback Box
			feedback = new TextArea(model.toString());
			feedback.setPrefWidth(width/2);
			feedback.setPrefHeight(height/4);
			
			
			VBox menuHolder = new VBox();
			menuHolder.getChildren().add(pass);
			menuHolder.getChildren().add(sizeCB);
			menuHolder.getChildren().add(reset);
			botBox.add(feedback,0,0);
			botBox.add(turnIndicator,1,0);
			botBox.add(menuHolder, 2,0);
			
			root.getChildren().add(botBox);
			
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void resetNodes() {
		feedback.setText(model.toString());
		sizeCB.setValue(model.getSize());
		generateGameBoard();
	}
	
	private void generateGameBoard() {
		board.getChildren().clear();
		buttons = new LandButton[model.getSize()][model.getSize()];
		//TODO: Delete the previous board more gracefully (No memory leak)
		for(int i = 0; i < model.getSize(); i++) {
			for(int j = 0; j < model.getSize(); j++) {
				LandButton temp = new LandButton(model.getInfo(i,j));
				buttons[i][j] = temp;
				temp.setRow(i);
				temp.setCol(j);
				temp.setOnAction(this);
				temp.setPrefWidth(width/model.getSize());
				temp.setPrefHeight((height-height/3)/model.getSize());
				temp.setId(model.getTileStyle(i,j));
				board.add(temp, j, i);
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void handle(ActionEvent e) {
		if(e.getSource() == sizeCB) {
			model.changeSize(sizeCB.getValue().intValue());
		}else if(e.getSource() == reset) {
			model.reset();
		}else if(e.getSource()== pass) {
			model.pass();
		}else {
			LandButton temp = (LandButton)e.getSource();
			model.move(temp.getRow(),temp.getCol());
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
		handleEvent(gameProgress, actionValidity, typeOfAction, errorType, player, row, col);
		friend.close();
	}

	private void handleEvent(char gameProgress, char validity, char action, char errorType,
			String player, int row, int col) {
		if(actionWasAReset(action)) {
			resetOccurred();
		}else if(actionWasASizeChange(action)) {
			sizeChangeOccurred();
		}else if(gameIsFinished(gameProgress)) {
			if(actionWasAMove(action)) {
				//TODO: Alert the user that the game is finished, thus a move isn't allowed
				lastMove.setText(player +"'s move failed because the game is over.");
				playErrorSound();
			}else if(actionWasAPass(action)) {
				lastMove.setText(player+"'s pass failed because the game is over.");
				playErrorSound();
				//TODO: Alert the user that the game is finished, thus a pass isn't allowed
			}else {
				feedback.setText(model.toString());
				playGameOver();
			}
		}else { //Game is still in progress
			if(isValidAction(validity)) {
				if(actionWasAMove(action)) {
					lastMove.setText(player + " executed a valid move at " + row + ", " + col);
					updateGameState();
					buttons[row][col].setText(model.getInfo(row,col));
					buttons[row][col].setId(model.getTileStyle(row,col));
					updateTurnIndicator();
					playMoveSound();
				}else if(actionWasAPass(action)) {
					lastMove.setText(player + " has passed.");
					updateGameState();
					playPassSound();
				}
			}else { 
				if(isErrorUnbiddable(errorType)) {
					//TODO: Inform the user that their bid failed because the tile was unbiddable
					lastMove.setText(player + " attempted to bid on " + row + ", " + col+ ". This "
							+ "action failed because the tile is owned by " + model.getOwner(row,col)
							+ ". (" + model.getOwner(row,col) + " can't be bid on)");
					playErrorSound();
				}else if(isErrorTooLittleMoney(errorType)) {
					//TODO: Inform the user that their bid failed because they had too little money
					lastMove.setText(player + " attempted to bid on " + row + ", " + col + ". This "
							+ "action failed because " + player + " doesn't have enough money to buy"
									+ " the tile.");
					playErrorSound();
				}
			}
		}
	}
	
	private void playErrorSound() {
		String error = "../LandBarons/Resources/errorSound.wav";
		playSound(error);
	}
	
	private void playMoveSound() {
		String cashRegister = "../LandBarons/Resources/cashRegisterOpening.wav";
		playSound(cashRegister);
	}
	
	
	private void playPassSound() {
		String passSound = "../LandBarons/Resources/bambooSwing.wav";
		playSound(passSound);
	}
	
	private void playResetSound() {
		
	}
	
	private void playGameOver() {
		String gameOver = "../LandBarons/Resources/gameOver.wav";
		playSound(gameOver);
	}
	
	private void playSound(String path) {
		Media sound = new Media(new File(path).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.play();
	}
	
	private void updateTurnIndicator() {
		turnIndicator.setId(model.getTurnID());
	}
	
	private void updateGameState() {
		feedback.setText(model.toString());
	}

	private void resetOccurred() {
		//TODO: Inform the user a reset occurred
		lastMove.setText("Game was reset");
		resetNodes();
		//TODO: Update the board based on the model
	}

	private void sizeChangeOccurred() {
		lastMove.setText("Board size was changed");
		generateGameBoard();
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
