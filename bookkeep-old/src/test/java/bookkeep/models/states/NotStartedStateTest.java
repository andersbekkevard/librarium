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

class NotStartedStateTest {

	private OwnedBook book;

	@BeforeEach
	void setUp() {
		// Build a sample book. By default, OwnedBook is in NotStartedState.
		book = new BookBuilder()
				.withTitle("Test Book")
				.withAuthorName("Test Author")
				.withPublicationYear(2000)
				.withPageCount(300)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook();
	}

	@Test
	void testGetStateName() {
		assertEquals("NotStartedState", book.getState().getStateName());
	}

	@Test
	void testHandleReadingDurationReturnsZero() {
		Duration duration = book.getState().handleReadingDuration();
		assertEquals(Duration.ZERO, duration, "Duration should be zero in NotStartedState");
	}

	@Test
	void testStartReadingTransitionsState() {
		// When starting reading, the state should change to InProgressState
		book.getState().startReading();
		assertTrue(book.getState() instanceof InProgressState, "State should transition to InProgressState");
		assertNotNull(book.getHistory().getStartedReading(), "Started reading event should be recorded");
	}

	@Test
	void testStopReadingThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().stopReading());
	}

	@Test
	void testHandleCommentThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleComment("Test comment"));
	}

	@Test
	void testHandleQuoteThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleQuote("Test quote", 10));
	}

	@Test
	void testHandleReviewThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleReview("Test review", 5));
	}

	@Test
	void testHandleIncrementPageNumberThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleIncrementPageNumber(10));
	}
}
