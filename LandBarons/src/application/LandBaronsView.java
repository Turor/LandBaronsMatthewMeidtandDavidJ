package application;
	
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;


public class Main extends Application implements EventHandler<ActionEvent> {
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
			size
			
			
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
}
