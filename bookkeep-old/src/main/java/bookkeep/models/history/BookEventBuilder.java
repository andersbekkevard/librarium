package bookkeep.models.history;

import java.time.Instant;

import bookkeep.enums.EventType;

/**
 * Builder class for creating BookEvent objects with a fluent API.
 * Provides type-safe construction for different event types with appropriate validation.
 */
public class BookEventBuilder {
    
    private EventType type;
    private String text;
    private int pageNumber;
    private int rating;
    private Instant timestamp;
    
    private static final int LOWEST_RATING = 0;
    private static final int HIGHEST_RATING = 5;
    
    private BookEventBuilder() {
        this.timestamp = Instant.now();
    }
    
    /**
     * Creates a builder for a STARTED_READING event
     */
    public static BookEventBuilder forStartedReading() {
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.STARTED_READING;
        return builder;
    }
    
    /**
     * Creates a builder for a FINISHED_READING event
     */
    public static BookEventBuilder forFinishedReading() {
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.FINISHED_READING;
        return builder;
    }
    
    /**
     * Creates a builder for a COMMENT event
     */
    public static BookEventBuilder forComment(String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new IllegalArgumentException("Comment text cannot be null or empty");
        }
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.COMMENT;
        builder.text = comment;
        return builder;
    }
    
    /**
     * Creates a builder for a QUOTE event
     */
    public static BookEventBuilder forQuote(String quote) {
        if (quote == null || quote.trim().isEmpty()) {
            throw new IllegalArgumentException("Quote text cannot be null or empty");
        }
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.QUOTE;
        builder.text = quote;
        return builder;
    }
    
    /**
     * Creates a builder for an AFTERTHOUGHT event
     */
    public static BookEventBuilder forAfterthought(String afterthought) {
        if (afterthought == null || afterthought.trim().isEmpty()) {
            throw new IllegalArgumentException("Afterthought text cannot be null or empty");
        }
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.AFTERTHOUGHT;
        builder.text = afterthought;
        return builder;
    }
    
    /**
     * Creates a builder for a REVIEW event
     */
    public static BookEventBuilder forReview(String reviewText, int rating) {
        if (reviewText == null || reviewText.trim().isEmpty()) {
            throw new IllegalArgumentException("Review text cannot be null or empty");
        }
        if (rating < LOWEST_RATING || rating > HIGHEST_RATING) {
            throw new IllegalArgumentException("Rating has to be between " + LOWEST_RATING + " and " + HIGHEST_RATING);
        }
        BookEventBuilder builder = new BookEventBuilder();
        builder.type = EventType.REVIEW;
        builder.text = reviewText;
        builder.rating = rating;
        return builder;
    }
    
    /**
     * Sets the page number for events that support it (COMMENT, QUOTE, AFTERTHOUGHT)
     */
    public BookEventBuilder atPage(int pageNumber) {
        if (type == EventType.STARTED_READING || type == EventType.FINISHED_READING) {
            throw new IllegalArgumentException("Page number cannot be set for " + type + " events");
        }
        if (type == EventType.REVIEW) {
            throw new IllegalArgumentException("Page number cannot be set for REVIEW events, use rating instead");
        }
        if (pageNumber < 0) {
            throw new IllegalArgumentException("Page number must be non-negative");
        }
        this.pageNumber = pageNumber;
        return this;
    }
    
    /**
     * Sets a custom timestamp (mainly for testing purposes)
     */
    public BookEventBuilder atTime(Instant timestamp) {
        if (timestamp == null) {
            throw new IllegalArgumentException("Timestamp cannot be null");
        }
        this.timestamp = timestamp;
        return this;
    }
    
    /**
     * Builds and returns the BookEvent instance
     */
    public BookEvent build() {
        return new BookEvent(this);
    }
    
    // Package-private getters for BookEvent constructor
    EventType getType() {
        return type;
    }
    
    String getText() {
        return text;
    }
    
    int getPageNumber() {
        return pageNumber;
    }
    
    int getRating() {
        return rating;
    }
    
    Instant getTimestamp() {
        return timestamp;
    }
}