package bookkeep.models;

import java.time.Duration;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.history.BookHistory;
import bookkeep.models.states.NotStartedState;
import bookkeep.models.states.ReadingState;

public class OwnedBook extends Book {

	private BookFormat format;
	private ReadingState state;
	public int pageNumber;
	private final BookHistory history;

	public OwnedBook() {
		this.state = new NotStartedState(this);
		this.history = new BookHistory();
		this.pageNumber = 0;
	}

	public OwnedBook(String title, String authorName, int publicationYear, int pageCount, Genre genre,
			BookFormat format) {
		super(title, authorName, publicationYear, pageCount, genre);
		this.format = format;
		this.state = new NotStartedState(this);
		this.history = new BookHistory();
		this.pageNumber = 0;
	}

	// region Getters and Setters
	public BookFormat getFormat() {
		return format;
	}

	public void setFormat(BookFormat format) {
		this.format = format;
	}

	// This method should not be used in any applications, only for backend
	// Preferred method is incrementPageNumber
	public void setPageNumber(int pageNumber) {
		if (0 > pageNumber || pageNumber > pageCount) {
			throw new IllegalArgumentException("Page number doesn't exist in the book");
		}
		this.pageNumber = pageNumber;
	}

	public int getPageNumber() {
		return pageNumber;
	}

	public void setState(ReadingState state) {
		this.state = state;
	}

	@Override
	public ReadingState getState() {
		return state;
	}

	@Override
	public BookHistory getHistory() {

		return history;
	}

	// endregion

	public Duration getReadingDuration() {
		return state.handleReadingDuration();
	}

	@Override
	public void addComment(String comment) {
		state.handleComment(comment);
	}

	@Override
	public void addQuote(String quote, int quotePageNumber) {
		state.handleQuote(quote, quotePageNumber);
	}

	@Override
	public void review(String reviewText, int rating) {
		state.handleReview(reviewText, rating);
	}

	@Override
	public void incrementPageNumber(int increment) {
		state.handleIncrementPageNumber(increment);
	}

	@Override
	public void changeState() {
		state.changeState();
	}

	@Override
	public String getStateName() {
		return state.getStateName();
	}

}
