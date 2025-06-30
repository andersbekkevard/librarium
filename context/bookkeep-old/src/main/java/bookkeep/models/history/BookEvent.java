package bookkeep.models.history;

import java.io.Serializable;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

import bookkeep.enums.EventType;

/**
 * Represents an event related to a book's history, such as starting, stopping,
 * commenting, reviewing, or quoting. Each event type determines which fields
 * are relevant:
 * 
 * @param timestamp  The time the event occurred.
 *                   Used by all event types.
 * 
 * @param type       The type of the event (for example STARTED_READING,
 *                   COMMENT,
 *                   REVIEW).
 * 
 * @param text       The text associated with the event.
 *                   Used only for COMMENT, QUOTE, AFTERTHOUGHT, and REVIEW.
 * 
 * @param pageNumber The page number associated with the event.
 *                   Used only for COMMENT, QUOTE, and AFTERTHOUGHT.
 * 
 * @param rating     A rating from 0 to 5.
 *                   Used only for REVIEW.
 */

public class BookEvent implements Serializable {

	private Instant timestamp;
	private EventType type;
	private String text;
	private int pageNumber;
	private int rating;

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	private static final int LOWEST_RATING = 0;
	private static final int HIGHEST_RATING = 5;

	/**
	 * Private constructor used by BookEventBuilder
	 */
	BookEvent(BookEventBuilder builder) {
		this.timestamp = builder.getTimestamp();
		this.type = builder.getType();
		this.text = builder.getText();
		this.pageNumber = builder.getPageNumber();
		this.rating = builder.getRating();
	}

	/**
	 * Creates a new BookEventBuilder for fluent construction
	 */
	public static BookEventBuilder builder() {
		throw new UnsupportedOperationException("Use specific factory methods like BookEventBuilder.forComment() instead");
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public EventType getType() {
		return type;
	}

	public String getText() {
		return text;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public int getRating() {
		return rating;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("BookEvent{");
		sb.append("timestamp=").append(timestamp);
		sb.append(", type=").append(type);
		sb.append(", text=").append(text);
		sb.append(", pageNumber=").append(pageNumber);
		sb.append('}');
		return sb.toString();
	}

}
