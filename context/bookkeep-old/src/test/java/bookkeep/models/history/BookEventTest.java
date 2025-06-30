package bookkeep.models.history;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import bookkeep.enums.EventType;

class BookEventTest {

	@Test
	void testBuilderForStartedReading() {
		BookEvent event = BookEventBuilder.forStartedReading().build();
		assertNotNull(event.getTimestamp(), "Timestamp should not be null");
		assertEquals(EventType.STARTED_READING, event.getType(), "Type should be STARTED_READING");
	}

	@Test
	void testBuilderForFinishedReading() {
		BookEvent event = BookEventBuilder.forFinishedReading().build();
		assertNotNull(event.getTimestamp(), "Timestamp should not be null");
		assertEquals(EventType.FINISHED_READING, event.getType(), "Type should be FINISHED_READING");
	}

	@Test
	void testBuilderForComment() {
		BookEvent event = BookEventBuilder.forComment("This is a comment").atPage(45).build();
		assertNotNull(event.getTimestamp(), "Timestamp should not be null");
		assertEquals(EventType.COMMENT, event.getType(), "Type should be COMMENT");
		assertEquals("This is a comment", event.getText(), "Text should be set correctly");
		assertEquals(45, event.getPageNumber(), "Page number should be set correctly");
	}

	@Test
	void testBuilderForCommentWithoutPage() {
		BookEvent event = BookEventBuilder.forComment("This is a comment").build();
		assertEquals(EventType.COMMENT, event.getType(), "Type should be COMMENT");
		assertEquals("This is a comment", event.getText(), "Text should be set correctly");
		assertEquals(0, event.getPageNumber(), "Page number should default to 0");
	}

	@Test
	void testBuilderForQuote() {
		BookEvent event = BookEventBuilder.forQuote("An inspiring quote").atPage(50).build();
		assertEquals(EventType.QUOTE, event.getType(), "Type should be QUOTE");
		assertEquals("An inspiring quote", event.getText(), "Text should be set correctly");
		assertEquals(50, event.getPageNumber(), "Page number should be set correctly");
	}

	@Test
	void testBuilderForAfterthought() {
		BookEvent event = BookEventBuilder.forAfterthought("Looking back...").atPage(100).build();
		assertEquals(EventType.AFTERTHOUGHT, event.getType(), "Type should be AFTERTHOUGHT");
		assertEquals("Looking back...", event.getText(), "Text should be set correctly");
		assertEquals(100, event.getPageNumber(), "Page number should be set correctly");
	}

	@Test
	void testBuilderForReviewWithValidRating() {
		BookEvent event = BookEventBuilder.forReview("A review", 4).build();
		assertEquals(EventType.REVIEW, event.getType(), "Type should be REVIEW");
		assertEquals("A review", event.getText(), "Review text should match");
		assertEquals(4, event.getRating(), "Rating should be set correctly");
	}

	@Test
	void testBuilderForReviewWithInvalidRatingLow() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forReview("Bad review", -1).build();
		});
		assertTrue(exception.getMessage().contains("Rating has to be between 0 and 5"));
	}

	@Test
	void testBuilderForReviewWithInvalidRatingHigh() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forReview("Bad review", 6).build();
		});
		assertTrue(exception.getMessage().contains("Rating has to be between 0 and 5"));
	}

	@Test
	void testBuilderThrowsForEmptyComment() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forComment("").build();
		});
		assertTrue(exception.getMessage().contains("Comment text cannot be null or empty"));
	}

	@Test
	void testBuilderThrowsForNullQuote() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forQuote(null).build();
		});
		assertTrue(exception.getMessage().contains("Quote text cannot be null or empty"));
	}

	@Test
	void testBuilderThrowsForEmptyReview() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forReview("   ", 3).build();
		});
		assertTrue(exception.getMessage().contains("Review text cannot be null or empty"));
	}

	@Test
	void testBuilderThrowsWhenSettingPageForStartedReading() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forStartedReading().atPage(1).build();
		});
		assertTrue(exception.getMessage().contains("Page number cannot be set for STARTED_READING events"));
	}

	@Test
	void testBuilderThrowsWhenSettingPageForFinishedReading() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forFinishedReading().atPage(1).build();
		});
		assertTrue(exception.getMessage().contains("Page number cannot be set for FINISHED_READING events"));
	}

	@Test
	void testBuilderThrowsWhenSettingPageForReview() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forReview("Review", 3).atPage(1).build();
		});
		assertTrue(exception.getMessage().contains("Page number cannot be set for REVIEW events"));
	}

	@Test
	void testBuilderThrowsForNegativePageNumber() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forComment("Test").atPage(-1).build();
		});
		assertTrue(exception.getMessage().contains("Page number must be non-negative"));
	}

	@Test
	void testTimestampIsRecent() {
		Instant before = Instant.now().minus(1, ChronoUnit.SECONDS);
		BookEvent event = BookEventBuilder.forStartedReading().build();
		Instant after = Instant.now().plus(1, ChronoUnit.SECONDS);
		assertTrue(event.getTimestamp().isAfter(before), "Timestamp should be after 'before'");
		assertTrue(event.getTimestamp().isBefore(after), "Timestamp should be before 'after'");
	}

	@Test
	void testCustomTimestamp() {
		Instant customTime = Instant.now().minus(1, ChronoUnit.HOURS);
		BookEvent event = BookEventBuilder.forStartedReading().atTime(customTime).build();
		assertEquals(customTime, event.getTimestamp(), "Custom timestamp should be set correctly");
	}

	@Test
	void testBuilderThrowsForNullTimestamp() {
		Exception exception = assertThrows(IllegalArgumentException.class, () -> {
			BookEventBuilder.forStartedReading().atTime(null).build();
		});
		assertTrue(exception.getMessage().contains("Timestamp cannot be null"));
	}

	@Test
	void testToStringContainsRelevantInformation() {
		BookEvent event = BookEventBuilder.forComment("Test comment").atPage(12).build();
		String output = event.toString();
		assertTrue(output.contains("COMMENT"), "toString should contain event type");
		assertTrue(output.contains("Test comment"), "toString should contain text");
		assertTrue(output.contains("12"), "toString should contain the page number");
	}

	@Test
	void testBuilderMethodChaining() {
		// Test that methods can be chained fluently
		BookEvent event = BookEventBuilder
			.forQuote("Chained quote")
			.atPage(25)
			.atTime(Instant.now().minus(5, ChronoUnit.MINUTES))
			.build();
		
		assertEquals(EventType.QUOTE, event.getType());
		assertEquals("Chained quote", event.getText());
		assertEquals(25, event.getPageNumber());
	}
}