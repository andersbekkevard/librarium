package bookkeep.general;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class JUnitSetupTest {

	@Test
	public void testAddition() {
		// Basic assertion
		int result = 2 + 3;
		assertEquals(5, result, "2 + 3 should equal 5");
	}

	@Test
	public void testStringContains() {
		// String-related assertion
		String phrase = "JUnit 5.7 setup is successful";
		assertTrue(phrase.contains("successful"), "Phrase should contain 'successful'");
	}

	@Test
	public void testExceptionThrown() {
		// Testing exception throwing
		assertThrows(ArithmeticException.class, () -> {
			int result = 10 / 0; // This will throw an exception
		}, "Division by zero should throw ArithmeticException");
	}
}
