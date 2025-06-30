package bookkeep.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;

class BookBuilderTest {

	@Test
	void testBuildOwnedBookSuccess() {
		BookBuilder builder = new BookBuilder();
		OwnedBook ownedBook = builder.withTitle("Test Title")
				.withAuthorName("Test Author")
				.withPublicationYear(2020)
				.withPageCount(350)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook();

		assertNotNull(ownedBook, "The built OwnedBook should not be null.");
		assertEquals("Test Title", ownedBook.getTitle(), "Title should match");
		assertEquals("Test Author", ownedBook.getAuthorName(), "Author should match");
		assertEquals(2020, ownedBook.getPublicationYear(), "Publication year should match");
		assertEquals(350, ownedBook.getPageCount(), "Page count should match");
		assertEquals(Genre.FICTION, ownedBook.getGenre(), "Genre should match");
		assertEquals(BookFormat.PHYSICAL, ownedBook.getFormat(), "Format should match");
	}

	@Test
	void testBuildWishlistBookSuccess() {
		BookBuilder builder = new BookBuilder();
		WishlistBook wishlistBook = builder.withTitle("Wishlist Title")
				.withAuthorName("Wishlist Author")
				.withPublicationYear(2018)
				.withPageCount(200)
				.withGenre(Genre.FICTION)
				.withPrice(25)
				.buildWishlistBook();

		assertNotNull(wishlistBook, "The built WishlistBook should not be null.");
		assertEquals("Wishlist Title", wishlistBook.getTitle(), "Title should match");
		assertEquals("Wishlist Author", wishlistBook.getAuthorName(), "Author should match");
		assertEquals(2018, wishlistBook.getPublicationYear(), "Publication year should match");
		assertEquals(200, wishlistBook.getPageCount(), "Page count should match");
		assertEquals(Genre.FICTION, wishlistBook.getGenre(), "Genre should match");
		assertEquals(25, wishlistBook.getPrice(), "Price should match");
	}

	@Test
	void testBuildOwnedBookMissingTitle() {
		BookBuilder builder = new BookBuilder();
		builder.withAuthorName("Test Author")
				.withPublicationYear(2020)
				.withPageCount(350)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildOwnedBook(),
				"Missing title should throw IllegalStateException");
		assertEquals("Title is required.", exception.getMessage(), "Exception message should indicate missing title");
	}

	@Test
	void testBuildOwnedBookMissingAuthorName() {
		BookBuilder builder = new BookBuilder();
		builder.withTitle("Test Title")
				.withPublicationYear(2020)
				.withPageCount(350)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildOwnedBook(),
				"Missing author name should throw IllegalStateException");
		assertEquals("Author name is required.", exception.getMessage(),
				"Exception message should indicate missing author name");
	}

	@Test
	void testBuildWishlistBookMissingTitle() {
		BookBuilder builder = new BookBuilder();
		builder.withAuthorName("Wishlist Author")
				.withPublicationYear(2018)
				.withPageCount(200)
				.withGenre(Genre.FICTION)
				.withPrice(25);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildWishlistBook(),
				"Missing title should throw IllegalStateException");
		assertEquals("Title is required.", exception.getMessage(), "Exception message should indicate missing title");
	}

	@Test
	void testBuildWishlistBookMissingAuthorName() {
		BookBuilder builder = new BookBuilder();
		builder.withTitle("Wishlist Title")
				.withPublicationYear(2018)
				.withPageCount(200)
				.withGenre(Genre.FICTION)
				.withPrice(25);

		IllegalStateException exception = assertThrows(IllegalStateException.class,
				() -> builder.buildWishlistBook(),
				"Missing author name should throw IllegalStateException");
		assertEquals("Author name is required.", exception.getMessage(),
				"Exception message should indicate missing author name");
	}
}
