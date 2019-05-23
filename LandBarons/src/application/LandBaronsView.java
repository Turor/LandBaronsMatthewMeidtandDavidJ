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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.GridPane;

/**
 * View for Land Barons game
 * @author David Jacobson
 * @author Matthew Meidt
 * @version Spring 2019
 *
 */
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
			//ComboBox for the size of the board

			sizeCB = new ComboBox<Integer>();
			sizeCB.setPrefWidth(width/4);
			sizeCB.setPrefHeight(height/12);
			sizeCB.setOnAction(this);
			sizeCB.getItems().setAll(4,5,7,10,14);
			sizeCB.setValue(4);


			model = new LandBaronsModel(sizeCB.getValue());
			model.addPropertyChangeListener(this);

			//TOP BOX
			board = new GridPane();
			//Using placer 'n' variable which will be given by the constructor
			root.getChildren().add(board);
			generateGameBoard();


			lastMove = new TextArea("Game Start");
			lastMove.setPrefHeight(scene.getHeight()/12);
			lastMove.setPrefWidth(scene.getWidth());
			lastMove.setEditable(false);
			lastMove.setWrapText(true);
			lastMove.setId("normal");
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

			//Feedback Box
			feedback = new TextArea(model.toString());
			feedback.setPrefWidth(width/2);
			feedback.setPrefHeight(height/4);
			feedback.setEditable(false);
			feedback.setWrapText(true);
			feedback.setId("normal");


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
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method which resets the game board
	 */
	private void resetNodes() {
		feedback.setText(model.toString());
		sizeCB.setValue(model.getSize());
		generateGameBoard();
	}

	/**
	 * Produces a new game board
	 */
	private void generateGameBoard() {
		board.getChildren().clear();
		buttons = new LandButton[model.getSize()][model.getSize()];
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


	/**
	 * Parses messages received from objects which the view is subscribed to
	 */
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

	/**
	 * Processes a model change
	 * @param gameProgress - Whether or not a game is in progress
	 * @param validity - Whether the action made was valid or not
	 * @param action - What the action was
	 * @param errorType - What kind of error occurred
	 * @param player - Who made the action
	 * @param row - Where was the action performed
	 * @param col
	 */
	private void handleEvent(char gameProgress, char validity, char action, char errorType,
			String player, int row, int col) {
		if(actionWasAReset(action)) {
			resetOccurred();
			updateTurnIndicator();
			updateTextAppearance(lastMove, false);
			playResetSound();
		}else if(actionWasASizeChange(action)) {
			updateTextAppearance(lastMove,false);
			updateTurnIndicator();
			sizeChangeOccurred();
			playResetSound();
		}else if(gameIsFinished(gameProgress)) {
			if(actionWasAMove(action)) {
				lastMove.setText(player +"'s move failed because the game is over.");
				updateTextAppearance(lastMove,true);
				playErrorSound();
			}else if(actionWasAPass(action)) {
				lastMove.setText(player+"'s pass failed because the game is over.");
				updateTextAppearance(lastMove,true);
				playErrorSound();
			}else {
				updateTextAppearance(lastMove,false);
				lastMove.setText(player+ " passed, ending the game");
				feedback.setText(model.toString());
				displayPath();
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
					updateTextAppearance(lastMove,false);
					playMoveSound();
				}else if(actionWasAPass(action)) {
					lastMove.setText(player + " has passed.");
					updateTextAppearance(lastMove,false);
					updateTurnIndicator();
					updateGameState();
					playPassSound();
				}
			}else { //Action was invalid
				if(isErrorUnbiddable(errorType)) {
					lastMove.setText(player + " attempted to bid on " + row + ", " + col+ ". This "
							+ "action failed because the tile is owned by " + model.getOwner(row,col)
							+ ". (" + model.getOwner(row,col) + " can't be bid on)");
					updateTextAppearance(lastMove,true);
					playErrorSound();
				}else if(isErrorTooLittleMoney(errorType)) {
					lastMove.setText(player + " attempted to bid on " + row + ", " + col + ". This "
							+ "action failed because " + player + " doesn't have enough money to buy"
							+ " the tile.");
					updateTextAppearance(lastMove,true);
					playErrorSound();
				}
			}
		}
	}

	private void updateTextAppearance(TextArea textDisplay, boolean wasError) {
		if(wasError)
			textDisplay.setId("error");
		else
			textDisplay.setId("valid");
	}

	private void displayPath() {
		//We don't need a clone of the nodes because they can't be modified once initialized
		LinkedList<Coordinates> path = model.getShortestPath();
		for(Coordinates coordinate : path) {
			int row = coordinate.getRow();
			int col = coordinate.getCol();
			buttons[row][col].setId(model.getTileStyle(row,col)+"f");
		}
	}
	//The following methods are for sound effects for different actions in the game
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
		String resetSound = "../LandBarons/Resources/resetSound.wav";
		playSound(resetSound);
	}

	private void playGameOver() {
		String gameOver = "../LandBarons/Resources/gameOver.wav";
		playSound(gameOver);
	}

	/**
	 * @param path - The path of the audio file to be played
	 */
	private void playSound(String path) {
		Media sound = new Media(new File(path).toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(sound);
		mediaPlayer.play();
	}

	/**Updates the turn indicator display*/
	private void updateTurnIndicator() {
		turnIndicator.setId(model.getTurnID());
	}

	/**Updates the textarea displaying the game state*/
	private void updateGameState() {
		feedback.setText(model.toString());
	}

	/**Performs necessary changes when a reset occurs*/
	private void resetOccurred() {
		lastMove.setText("Game was reset");
		resetNodes();
	}

	/**Performs necessary changes when the board size is changed*/
	private void sizeChangeOccurred() {
		lastMove.setText("Board size was changed");
		generateGameBoard();
		//TODO: Update the GUI based on the new size
	}


	/**
	 * @param actionValidity 
	 * @return Was the action valid?
	 */
	private boolean isValidAction(char actionValidity) {
		return 'V' == actionValidity;
	}

	/**
	 * @param errorType 
	 * @return Was the error due to the tile being unbiddable?
	 */
	private boolean isErrorUnbiddable(char errorType) {
		return 'U' == errorType;
	}

	/**
	 * 
	 * @param errorType 
	 * @return Was the error due to the player having too little money?
	 */
	private boolean isErrorTooLittleMoney(char errorType) {
		return '$' == errorType;
	}

	/**
	 * 
	 * @param action
	 * @return Was the action a move?
	 */
	private boolean actionWasAMove(char action) {
		return 'M' == action;
	}

	/**
	 * 
	 * @param action
	 * @return Was the action a pass?
	 */
	private boolean actionWasAPass(char action) {
		return 'P' == action;
	}

	/**
	 * 
	 * @param action
	 * @return Was the action a reset?
	 */
	private boolean actionWasAReset(char action) {
		return 'R' == action;
	}

	/**
	 * 
	 * @param action
	 * @return Was the action a size change?
	 */
	private boolean actionWasASizeChange(char action) {
		return 'S' == action;
	}

	/**
	 * 
	 * @param gameProgress
	 * @return Was the game finished when the event was received?
	 */
	private boolean gameIsFinished(char gameProgress) {
		return 'F' == gameProgress;
	}
}
