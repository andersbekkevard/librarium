package bookkeep.models.states;

import java.time.Duration;

import bookkeep.enums.EventType;
import bookkeep.models.OwnedBook;
import bookkeep.models.history.BookEvent;
import bookkeep.models.history.BookEventBuilder;

public class NotStartedState extends ReadingState {

	public NotStartedState(OwnedBook book) {
		super(book);
	}

	@Override
	public void startReading() {
		book.setState(new InProgressState(book));
		BookEvent startedReadingEvent = BookEventBuilder.forStartedReading().build();
		book.getHistory().addEvent(startedReadingEvent);
	}

	@Override
	public void stopReading() {
		throw new UnsupportedOperationException("Cannot stop a book in NotStartedState");
	}

	@Override
	public String getStateName() {
		return "NotStartedState";
	}

	@Override
	public void handleComment(String comment) {
		throw new UnsupportedOperationException("Cannot comment a book in NotStartedState");
	}

	@Override
	public void handleQuote(String quote, int quotePageNumber) {
		throw new UnsupportedOperationException("Cannot quote a book in NotStartedState");
	}

	@Override
	public void handleReview(String reviewText, int rating) {
		throw new UnsupportedOperationException("Cannot review an unfinished book");
	}

	@Override
	public Duration handleReadingDuration() {
		return Duration.ZERO;
	}

	@Override
	public void handleIncrementPageNumber(int increment) {
		throw new UnsupportedOperationException("Cannot Increment page number of a book in NotStartedState");
	}

	@Override
	public void changeState() {
		startReading();
	}

}
