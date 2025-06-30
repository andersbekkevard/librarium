package bookkeep.models;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.states.FinishedState;
import bookkeep.models.states.InProgressState;
import bookkeep.models.states.NotStartedState;

class OwnedBookTest {

	private OwnedBook book;

	@BeforeEach
	void setUp() {
		// Create a new OwnedBook using the builder.
		book = new BookBuilder()
				.withTitle("JUnit Testing Book")
				.withAuthorName("Author Test")
				.withPublicationYear(2021)
				.withPageCount(100)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.DIGITAL)
				.buildOwnedBook();
	}

	/* =========================== INITIAL STATE TESTS ========================== */
	@Test
	void testInitialState() {
		// Initially, the state should be NotStartedState, pageNumber should be 0,
		// and reading duration should be zero.
		assertTrue(book.getState() instanceof NotStartedState, "Initial state should be NotStartedState");
		assertEquals(0, book.getPageNumber(), "Initial page number should be 0");
		assertEquals(Duration.ZERO, book.getReadingDuration(), "Initial reading duration should be zero");
	}

	/* =========================== GETTER/SETTER TESTS ========================== */
	@Test
	void testSetAndGetPageNumber() {
		book.setPageNumber(50);
		assertEquals(50, book.getPageNumber(), "Page number should update to 50");
	}

	@Test
	void testSetPageNumberInvalidThrowsException() {
		// Given pageCount is 100, setting pageNumber > 100 should throw an exception.
		Exception exception = assertThrows(IllegalArgumentException.class, () -> book.setPageNumber(150));
		assertTrue(exception.getMessage().contains("Page number doesn't exist"),
				"Exception message should indicate invalid page number");
	}

	@Test
	void testGetAndSetFormat() {
		// Verify initial format from builder and update it.
		assertEquals(BookFormat.DIGITAL, book.getFormat(), "Initial format should be DIGITAL");
		book.setFormat(BookFormat.PHYSICAL);
		assertEquals(BookFormat.PHYSICAL, book.getFormat(), "Format should update to PHYSICAL");
	}

	/* ======================= NOT STARTED STATE BEHAVIOR ======================= */
	@Test
	void testNotStartedStateMethodsThrow() {
		// In NotStartedState, methods like addComment and incrementPageNumber are not
		// allowed.
		assertThrows(UnsupportedOperationException.class, () -> book.addComment("Test comment"));
		assertThrows(UnsupportedOperationException.class, () -> book.incrementPageNumber(10));
		// Similarly, addQuote and review are not allowed.
		assertThrows(UnsupportedOperationException.class, () -> book.addQuote("Test quote", 5));
		assertThrows(UnsupportedOperationException.class, () -> book.review("Test review", 4));
	}

	/* ======================= IN PROGRESS STATE BEHAVIOR ======================= */
	@Test
	void testTransitionToInProgressAndIncrementPage() {
		// Transition to InProgressState.
		book.getState().startReading();
		assertTrue(book.getState() instanceof InProgressState, "State should now be InProgressState");

		// Test incrementing page number.
		int initialPage = book.getPageNumber();
		book.incrementPageNumber(20);
		assertEquals(initialPage + 20, book.getPageNumber(), "Page number should increment by 20");
	}

	@Test
	void testInProgressStateAddQuote() {
		// Transition to InProgressState.
		book.getState().startReading();
		int initialEvents = book.getHistory().getListOfEvents().size();
		book.addQuote("Test quote", 30);
		int newEvents = book.getHistory().getListOfEvents().size();
		assertEquals(initialEvents + 1, newEvents, "One quote event should be added in InProgressState");
	}

	@Test
	void testInProgressStateReviewThrows() {
		// In InProgressState, calling review should throw an exception.
		book.getState().startReading();
		assertThrows(UnsupportedOperationException.class, () -> book.review("Review text", 4));
	}

	@Test
	void testReadingDurationInInProgressState() throws InterruptedException {
		book.getState().startReading();
		Duration initialDuration = book.getReadingDuration();
		Thread.sleep(50); // Simulate elapsed time.
		Duration laterDuration = book.getReadingDuration();
		assertTrue(laterDuration.compareTo(initialDuration) > 0, "Reading duration should increase over time");
	}

	/*
	 * ================================= FINISHED STATE BEHAVIOR
	 * =================================
	 */
	@Test
	void testTransitionToFinishedStateAndMethods() {
		// Transition from NotStarted to InProgress, then to Finished.
		book.getState().startReading();
		book.getState().handleIncrementPageNumber(50); // Simulate some progress.
		book.getState().stopReading();
		assertTrue(book.getState() instanceof FinishedState, "State should be FinishedState after stopping reading");

		// In FinishedState, addComment should work (adding an afterthought event).
		int initialEvents = book.getHistory().getListOfEvents().size();
		assertDoesNotThrow(() -> book.addComment("Final thoughts"));
		int newEvents = book.getHistory().getListOfEvents().size();
		assertTrue(newEvents > initialEvents, "Afterthought event should be added in FinishedState");

		// In FinishedState, review should work.
		assertDoesNotThrow(() -> book.review("Excellent!", 5));
		assertNotNull(book.getHistory().getReview(), "Review should be set in FinishedState");

		// In FinishedState, incrementPageNumber should throw.
		assertThrows(UnsupportedOperationException.class, () -> book.incrementPageNumber(10));
	}

	@Test
	void testAddQuoteInFinishedState() {
		// Transition to FinishedState.
		book.getState().startReading();
		book.getState().handleIncrementPageNumber(40);
		book.getState().stopReading();
		int initialEvents = book.getHistory().getListOfEvents().size();
		assertDoesNotThrow(() -> book.addQuote("Final quote", 80));
		int newEvents = book.getHistory().getListOfEvents().size();
		assertTrue(newEvents > initialEvents, "A quote event should be added in FinishedState");
	}
}
