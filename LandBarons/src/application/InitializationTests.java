package application;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InitializationTests {

	@Test
	void test() {
		LandBaronsModel temp = new LandBaronsModel(5);
		System.out.println(temp.toString());
	}

}
