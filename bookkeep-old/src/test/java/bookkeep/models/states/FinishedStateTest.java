package bookkeep.models.states;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.BookBuilder;
import bookkeep.models.OwnedBook;
import bookkeep.models.history.BookEvent;

class FinishedStateTest {

	private OwnedBook book;

	@BeforeEach
	void setUp() {
		// Build a sample book and transition it to FinishedState.
		book = new BookBuilder()
				.withTitle("Test Book")
				.withAuthorName("Test Author")
				.withPublicationYear(2000)
				.withPageCount(300)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook();
		// Start reading and then finish reading.
		book.getState().startReading();
		book.getState().handleIncrementPageNumber(100); // simulate progress
		book.getState().stopReading();
	}

	@Test
	void testGetStateName() {
		assertEquals("FinishedState", book.getState().getStateName());
	}

	@Test
	void testStartReadingThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().startReading());
	}

	@Test
	void testStopReadingThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().stopReading());
	}

	@Test
	void testHandleCommentAddsAfterThoughtEvent() {
		int initialSize = book.getHistory().getListOfEvents().size();
		book.getState().handleComment("Afterthought comment");
		int newSize = book.getHistory().getListOfEvents().size();
		assertTrue(newSize > initialSize, "An afterthought event should be added in FinishedState");
		// Also verify that the afterthought subset is not empty.
		assertFalse(book.getHistory().getAfterThoughts().isEmpty(), "Afterthought events should exist");
	}

	@Test
	void testHandleQuoteAddsEvent() {
		int initialSize = book.getHistory().getListOfEvents().size();
		book.getState().handleQuote("Final quote", 250);
		int newSize = book.getHistory().getListOfEvents().size();
		assertTrue(newSize > initialSize, "A quote event should be added in FinishedState");
	}

	@Test
	void testHandleReviewAddsAndOverridesReview() {
		// First, add an initial review.
		book.getState().handleReview("Initial review", 4);
		BookEvent reviewEvent1 = book.getHistory().getReview();
		assertNotNull(reviewEvent1, "Review event should be set after first review");
		String content1 = reviewEvent1.getText();
		assertEquals("Initial review", content1, "Initial review text should match");

		// Second review should override the previous one.
		book.getState().handleReview("Updated review", 5);
		BookEvent reviewEvent2 = book.getHistory().getReview();
		assertNotNull(reviewEvent2, "Review event should be updated");
		String content2 = reviewEvent2.getText();
		assertNotEquals("Initial review", content2, "Review text should be overridden");
		assertTrue(content2.contains("_OVERRIDE_"), "Overridden review should be marked with _OVERRIDE_");
	}

	@Test
	void testHandleReadingDurationIsPositive() {
		Duration duration = book.getState().handleReadingDuration();
		assertTrue(duration.compareTo(Duration.ZERO) >= 0, "Reading duration should be positive in FinishedState");
	}

	@Test
	void testIncrementPageNumberThrowsException() {
		assertThrows(UnsupportedOperationException.class, () -> book.getState().handleIncrementPageNumber(10));
	}
}
