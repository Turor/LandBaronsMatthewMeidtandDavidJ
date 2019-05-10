package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InitializationTests {

	@Test
	void test() {
		LandBaronsModel temp = new LandBaronsModel(5);
		System.out.println(temp.toString());
	}
	
	@Test
	void printBoard() {
		LandBaronsModel temp = new LandBaronsModel(5);
		LandNode[][] board =  temp.getBoardClone();
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[i].length; j++) {
				System.out.print(board[i][j] + "\t");
			}
			System.out.println();
		}
	}

}
