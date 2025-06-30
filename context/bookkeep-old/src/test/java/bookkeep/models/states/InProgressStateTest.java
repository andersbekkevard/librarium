package bookkeep.models.states;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.BookBuilder;
import bookkeep.models.OwnedBook;

class InProgressStateTest {

	private OwnedBook book;

	@BeforeEach
	void setUp() {
		// Build a sample book and transition it to InProgressState.
		book = new BookBuilder()
				.withTitle("Test Book")
				.withAuthorName("Test Author")
				.withPublicationYear(2000)
				.withPageCount(300)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook();
		// Transition from NotStartedState to InProgressState.
		book.getState().startReading();
	}

	@Test
	void testGetStateName() {
		assertEquals("InProgressState", book.getState().getStateName());
	}

	@Test
	void testStartReadingThrowsException() {
		// Once in progress, starting reading again is not allowed.
		assertThrows(UnsupportedOperationException.class, () -> book.getState().startReading());
	}

	@Test
	void testStopReadingTransitionsToFinishedState() {
		book.getState().stopReading();
		assertTrue(book.getState() instanceof FinishedState, "State should transition to FinishedState after stopping");
		assertNotNull(book.getHistory().getFinishedReading(), "Finished reading event should be recorded");
	}

	@Test
	void testHandleCommentAddsEvent() {
		int initialSize = book.getHistory().getListOfEvents().size();
		book.getState().handleComment("Great book!");
		int newSize = book.getHistory().getListOfEvents().size();
		assertTrue(newSize > initialSize, "A comment event should be added in InProgressState");
	}

	@Test
	void testHandleQuoteAddsEvent() {
		int initialSize = book.getHistory().getListOfEvents().size();
		book.getState().handleQuote("An inspiring quote", 50);
		int newSize = book.getHistory().getListOfEvents().size();
		assertTrue(newSize > initialSize, "A quote event should be added in InProgressState");
	}

	@Test
	void testHandleReviewThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleReview("Review text", 4));
	}

	@Test
	void testReadingDurationIncreasesOverTime() {
		Duration d1 = book.getState().handleReadingDuration();
		try {
			Thread.sleep(50); // Simulate elapsed time.
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		Duration d2 = book.getState().handleReadingDuration();
		assertTrue(d2.compareTo(d1) > 0, "Reading duration should increase as time passes");
	}

	@Test
	void testIncrementPageNumberUpdatesPageNumber() {
		int initialPage = book.getPageNumber();
		book.getState().handleIncrementPageNumber(25);
		assertEquals(initialPage + 25, book.getPageNumber(), "Page number should be incremented properly");
	}
}
