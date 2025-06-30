package bookkeep.models.states;

import java.io.Serializable;
import java.time.Duration;

import bookkeep.models.OwnedBook;

public abstract class ReadingState implements Serializable {

	protected OwnedBook book;

	public ReadingState(OwnedBook book) {
		this.book = book;
	}

	public abstract void startReading();

	public abstract void stopReading();

	public abstract String getStateName();

	public abstract void handleComment(String comment);

	public abstract void handleQuote(String quote, int quotePageNumber);

	public abstract void handleReview(String reviewText, int rating);

	public abstract Duration handleReadingDuration();

	public abstract void handleIncrementPageNumber(int increment);

	public abstract void changeState();
}
