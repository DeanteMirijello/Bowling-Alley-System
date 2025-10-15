package com.bowling.bowlingball;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BowlingballServiceApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void applicationMainRuns() {
		BowlingballServiceApplication.main(new String[]{}); // ✅ Cover the main() method
	}
}
