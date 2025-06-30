package bookkeep.models.history;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookkeep.enums.EventType;
import bookkeep.models.history.BookEventBuilder;

class BookHistoryTest {

	private BookHistory history;

	@BeforeEach
	void setUp() {
		history = new BookHistory();
	}

	/*
	 * ================================= INITIAL STATE TESTS
	 * =================================
	 */

	@Test
	void testInitialHistoryIsEmpty() {
		List<BookEvent> events = history.getListOfEvents();
		assertNotNull(events, "Event list should not be null");
		assertTrue(events.isEmpty(), "Event list should be empty initially");
	}

	/* ============================= ADD EVENT TESTS ============================ */

	@Test
	void testAddCommentEvent() {
		BookEvent commentEvent = BookEventBuilder.forComment("Great book").atPage(10).build();
		history.addEvent(commentEvent);
		List<BookEvent> events = history.getListOfEvents();
		assertEquals(1, events.size(), "Should have one event after adding a comment");
		assertEquals(commentEvent, events.get(0), "The added comment event should match");
	}

	@Test
	void testAddQuoteEvent() {
		BookEvent quoteEvent = BookEventBuilder.forQuote("Inspiring quote").atPage(20).build();
		history.addEvent(quoteEvent);
		List<BookEvent> events = history.getListOfEvents();
		assertEquals(1, events.size(), "Should have one event after adding a quote");
		assertEquals(quoteEvent, events.get(0), "The added quote event should match");
	}

	@Test
	void testAddAfterThoughtEvent() {
		BookEvent afterThoughtEvent = BookEventBuilder.forAfterthought("Late thought").atPage(30).build();
		history.addEvent(afterThoughtEvent);
		List<BookEvent> events = history.getListOfEvents();
		assertEquals(1, events.size(), "Should have one event after adding an afterthought");
		assertEquals(afterThoughtEvent, events.get(0), "The added afterthought event should match");
	}

	@Test
	void testAddStartedReadingEvent() {
		BookEvent startedEvent = BookEventBuilder.forStartedReading().build();
		history.addEvent(startedEvent);
		assertEquals(startedEvent, history.getStartedReading(), "Started reading event should be set");
	}

	@Test
	void testAddFinishedReadingEvent() {
		BookEvent finishedEvent = BookEventBuilder.forFinishedReading().build();
		history.addEvent(finishedEvent);
		assertEquals(finishedEvent, history.getFinishedReading(), "Finished reading event should be set");
	}

	@Test
	void testAddReviewEventThrowsException() {
		BookEvent reviewEvent = BookEventBuilder.forReview("Review text", 4).build();
		Exception exception = assertThrows(IllegalArgumentException.class, () -> history.addEvent(reviewEvent));
		assertTrue(exception.getMessage().contains("Reviews should not be added through addEvent Method"),
				"Adding a review event through addEvent should throw an exception");
	}

	/* =========================== GATHER SUBSET TESTS ========================== */

	@Test
	void testGetCommentsSubset() {
		BookEvent comment1 = BookEventBuilder.forComment("Comment one").atPage(5).build();
		BookEvent comment2 = BookEventBuilder.forComment("Comment two").atPage(10).build();
		BookEvent quote = BookEventBuilder.forQuote("A quote").atPage(15).build();
		history.addEvent(comment1);
		history.addEvent(quote);
		history.addEvent(comment2);

		List<BookEvent> comments = history.getComments();
		assertEquals(2, comments.size(), "There should be 2 comment events");
		assertTrue(comments.contains(comment1));
		assertTrue(comments.contains(comment2));
	}

	@Test
	void testGetAfterThoughtsSubset() {
		BookEvent afterThought = BookEventBuilder.forAfterthought("Afterthought").atPage(20).build();
		BookEvent comment = BookEventBuilder.forComment("Comment").atPage(5).build();
		history.addEvent(afterThought);
		history.addEvent(comment);

		List<BookEvent> afterThoughts = history.getAfterThoughts();
		assertEquals(1, afterThoughts.size(), "There should be 1 afterthought event");
		assertEquals(afterThought, afterThoughts.get(0));
	}

	@Test
	void testGetQuotesSubset() {
		BookEvent quote1 = BookEventBuilder.forQuote("Quote one").atPage(25).build();
		BookEvent quote2 = BookEventBuilder.forQuote("Quote two").atPage(30).build();
		BookEvent comment = BookEventBuilder.forComment("Comment").atPage(5).build();
		history.addEvent(quote1);
		history.addEvent(comment);
		history.addEvent(quote2);

		List<BookEvent> quotes = history.getQuotes();
		assertEquals(2, quotes.size(), "There should be 2 quote events");
		assertTrue(quotes.contains(quote1));
		assertTrue(quotes.contains(quote2));
	}

	/* ================================ SORT TEST =============================== */

	@Test
	void testSortOrdersEventsChronologically() throws InterruptedException {
		// Create events with a slight delay between them.
		BookEvent event1 = BookEventBuilder.forComment("First").atPage(5).build();
		Thread.sleep(10);
		BookEvent event2 = BookEventBuilder.forQuote("Second").atPage(10).build();
		Thread.sleep(10);
		BookEvent event3 = BookEventBuilder.forAfterthought("Third").atPage(15).build();

		// Add them in reverse order.
		history.addEvent(event3);
		history.addEvent(event1);
		history.addEvent(event2);

		// Call sort() to arrange them chronologically.
		history.sort();

		List<BookEvent> sortedEvents = history.getListOfEvents();
		assertEquals(event1, sortedEvents.get(0), "Event1 should be first after sorting");
		assertEquals(event2, sortedEvents.get(1), "Event2 should be second after sorting");
		assertEquals(event3, sortedEvents.get(2), "Event3 should be third after sorting");
	}

	/* ========================== REVIEW GET/SET TEST ========================== */

	@Test
	void testReviewGetterAndSetter() {
		BookEvent reviewEvent = BookEventBuilder.forReview("Excellent!", 5).build();
		history.setReview(reviewEvent);
		assertTrue(history.hasReview(), "hasReview() should return true after setting a review");
		assertEquals(reviewEvent, history.getReview(), "getReview() should return the review event that was set");
	}

	/* ============================== TOSTRING TEST ============================= */
	@Test
	void testToStringReturnsListContents() {
		BookEvent commentEvent = BookEventBuilder.forComment("Test comment").atPage(10).build();
		history.addEvent(commentEvent);
		String toStringOutput = history.toString();
		assertTrue(toStringOutput.contains("Test comment"), "toString() should contain the comment text");
	}
}
