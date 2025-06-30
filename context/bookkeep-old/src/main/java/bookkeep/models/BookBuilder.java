package bookkeep.models;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;

public class BookBuilder {

	/* ============================== Common fields ============================= */
	private String title;
	private String authorName;
	private int publicationYear;
	private int pageCount;
	private Genre genre;
	/* ============================ OwnedBook fields ============================ */
	private BookFormat format;
	/* =========================== WishlistBook fields ========================== */
	private int price = 0;

	private void validateCommonFields() {
		if (title == null || title.isEmpty()) {
			throw new IllegalStateException("Title is required.");
		}
		if (authorName == null || authorName.isEmpty()) {
			throw new IllegalStateException("Author name is required.");
		}
	}

	public BookBuilder withTitle(String title) {
		this.title = title;
		return this;
	}

	public BookBuilder withAuthorName(String authorName) {
		this.authorName = authorName;
		return this;
	}

	public BookBuilder withPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
		return this;
	}

	public BookBuilder withPageCount(int pageCount) {
		this.pageCount = pageCount;
		return this;
	}

	public BookBuilder withGenre(Genre genre) {
		this.genre = genre;
		return this;
	}

	public BookBuilder withFormat(BookFormat format) {
		this.format = format;
		return this;
	}

	public BookBuilder withPrice(int price) {
		this.price = price;
		return this;
	}

	public OwnedBook buildOwnedBook() {
		validateCommonFields();
		return new OwnedBook(title, authorName, publicationYear, pageCount, genre, format);
	}

	public WishlistBook buildWishlistBook() {
		validateCommonFields();
		return new WishlistBook(title, authorName, publicationYear, pageCount, genre, price);
	}

	public static void main(String[] args) {
		OwnedBook book = new BookBuilder().withAuthorName("Name").withFormat(BookFormat.PHYSICAL).buildOwnedBook();
	}
}
