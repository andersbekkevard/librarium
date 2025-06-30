package bookkeep.models;

import bookkeep.enums.Genre;
import bookkeep.models.history.BookHistory;
import bookkeep.models.states.ReadingState;

public class WishlistBook extends Book {
	private int price;

	public WishlistBook() {
	}

	public WishlistBook(String title, String authorName, int publicationYear, int pageCount, Genre genre, int price) {
		super(title, authorName, publicationYear, pageCount, genre);
		this.price = price;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return super.toString() + ", price=" + price;
	}

	@Override
	public BookHistory getHistory() {
		throw new UnsupportedOperationException("Can not get history of WishlistBook");
	}

	@Override
	public void addComment(String comment) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addComment'");
	}

	@Override
	public void addQuote(String quote, int quotePageNumber) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'addQuote'");
	}

	@Override
	public void review(String reviewText, int rating) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'review'");
	}

	@Override
	public void incrementPageNumber(int increment) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'incrementPageNumber'");
	}

	@Override
	public ReadingState getState() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public String getStateName() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void changeState() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public int getPageNumber() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

}
