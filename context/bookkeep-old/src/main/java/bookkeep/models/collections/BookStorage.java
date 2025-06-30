package bookkeep.models.collections;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.Book;
import bookkeep.models.BookBuilder;

public class BookStorage implements Serializable {

	private final Map<UUID, Book> bookRepository;
	private final List<BookShelf> bookShelves;

	private static final BiPredicate<Book, Object> filterByAuthor = (book, author) -> {
		if (author instanceof String string) {
			return book.getAuthorName().toLowerCase().contains(string.toLowerCase());
		}
		return false;
	};

	private static final BiPredicate<Book, Object> filterByTitle = (book, title) -> {
		if (title instanceof String) {
			return book.getTitle().toLowerCase().contains(((String) title).toLowerCase());
		}
		return false;
	};

	private static final BiPredicate<Book, Object> filterByPublicationYear = (book, year) -> Integer
			.valueOf(book.getPublicationYear()).equals(year);

	public BookStorage() {
		this.bookRepository = new HashMap<>();
		this.bookShelves = new ArrayList<>();
	}

	/* ========================================================================== */
	/* EXTERNAL METHODS */
	/* ========================================================================== */

	/* ============================== Book Methods ============================== */
	public void addBook(Book book) {
		bookRepository.put(book.getUUID(), book);
	}

	public void removeBook(Book book) {
		UUID id = book.getUUID();
		bookRepository.remove(id);
		for (BookShelf shelf : bookShelves) {
			shelf.removeId(id);
		}
	}

	public List<Book> getAllBooks() {
		return bookRepository.values().stream().collect(Collectors.toList());
	}

	/* ============================ BookShelf Methods =========================== */

	public void addShelf(String name) {
		BookShelf shelf = new BookShelf(name);
		bookShelves.add(shelf);
	}

	public void removeShelf(String name) {
		bookShelves.removeIf(shelf -> shelf.getName().equals(name));
	}

	public void addBookToShelf(String shelfName, Book book) {
		// First make sure the book is in the library
		if (!bookRepository.containsKey(book.getUUID())) {
			addBook(book);
		}
		// Add the book to the shelf if present
		getShelfByName(shelfName)
				.orElseThrow(() -> new IllegalArgumentException("Shelf not found"))
				.addId(book.getUUID());
	}

	public void removeBookFromShelf(String shelfName, Book book) {
		getShelfByName(shelfName)
				.orElseThrow(() -> new IllegalArgumentException("Shelf not found"))
				.removeId(book.getUUID());
	}

	public List<Book> getBooksFromShelfName(String name) {
		BookShelf shelf = getShelfByName(name).orElseThrow();
		return getBooksFromShelf(shelf);
	}

	public List<String> getShelfNames() {
		return bookShelves.stream()
				.map(BookShelf::getName)
				.collect(Collectors.toList());
	}

	/* ============================ Filtering methods =========================== */

	public List<Book> getBooksByAuthor(String author) {
		List<UUID> UUIDsByAuthor = findUUIDsByAuthor(author);
		return getBooksFromListOfUUIDs(UUIDsByAuthor);
	}

	public Book getBookByTitle(String title) {
		// Beware: Assuming only one book with said title.
		List<UUID> UUIDsByTitle = findUUIDsByTitle(title);
		if (UUIDsByTitle.isEmpty()) {
			throw new IllegalArgumentException("Book title not found");
		} else {
			return getBook(UUIDsByTitle.get(0));
		}
	}

	public List<Book> getBooksByYear(int year) {
		List<UUID> UUIDsByYear = findUUIDsByYear(year);
		return getBooksFromListOfUUIDs(UUIDsByYear);
	}

	public List<Book> getBooksByYearInterval(int startYear, int endYear) {
		List<Book> books = new ArrayList<>();
		for (int year = startYear; year < endYear; year++) {
			books.addAll(getBooksByYear(year));
		}
		return books;
	}

	/* ========================================================================== */
	/* INTERNAL METHODS */
	/* ========================================================================== */
	/* ================================= Getters ================================ */
	private Book getBook(UUID id) {
		return bookRepository.get(id);
	}

	private Optional<BookShelf> getShelfByName(String name) {
		return bookShelves.stream()
				.filter(shelf -> shelf.getName().equals(name))
				.findFirst();
	}

	private List<Book> getBooksFromShelf(BookShelf shelf) {
		return shelf.getUUIDs().stream().map(bookRepository::get).collect(Collectors.toList());
	}

	private List<Book> getBooksFromListOfUUIDs(List<UUID> UUIDs) {
		return UUIDs.stream().map(bookRepository::get).collect(Collectors.toList());
	}

	/* ================================ Filtering =============================== */
	private List<UUID> findUUIDs(BiPredicate<Book, Object> condition, Object value) {
		return bookRepository.values().stream()
				.filter(book -> condition.test(book, value))
				.map(Book::getUUID)
				.collect(Collectors.toList());
	}

	private List<UUID> findUUIDsByAuthor(String author) {
		return findUUIDs(filterByAuthor, author);
	}

	private List<UUID> findUUIDsByTitle(String title) {
		return findUUIDs(filterByTitle, title);
	}

	private List<UUID> findUUIDsByYear(int year) {
		return findUUIDs(filterByPublicationYear, year);
	}

	/*
	 * ================================ Other Methods
	 * ================================
	 */
	public void makeDummyLibrary() {
		this.addBook(new BookBuilder()
				.withTitle("The Hobbit")
				.withAuthorName("J.R.R. Tolkien")
				.withPublicationYear(1937)
				.withPageCount(310)
				.withGenre(Genre.FANTASY)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook());

		this.addBook(new BookBuilder()
				.withTitle("1984")
				.withAuthorName("George Orwell")
				.withPublicationYear(1949)
				.withPageCount(328)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.DIGITAL)
				.buildOwnedBook());

		this.addBook(new BookBuilder()
				.withTitle("To Kill a Mockingbird")
				.withAuthorName("Harper Lee")
				.withPublicationYear(1960)
				.withPageCount(281)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook());

		this.addBook(new BookBuilder()
				.withTitle("The Catcher in the Rye")
				.withAuthorName("J.D. Salinger")
				.withPublicationYear(1951)
				.withPageCount(277)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook());

		this.addBook(new BookBuilder()
				.withTitle("Brave New World")
				.withAuthorName("Aldous Huxley")
				.withPublicationYear(1932)
				.withPageCount(268)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.DIGITAL)
				.buildOwnedBook());

		this.addBook(new BookBuilder()
				.withTitle("Moby-Dick")
				.withAuthorName("Herman Melville")
				.withPublicationYear(1851)
				.withPageCount(585)
				.withGenre(Genre.FICTION)
				.withFormat(BookFormat.PHYSICAL)
				.buildOwnedBook());

		// Create and add shelves
		BookShelf fictionShelf = new BookShelf("Fiction Classics");
		BookShelf fantasyShelf = new BookShelf("Fantasy Adventures");

		// Assign sample books to shelves
		this.bookRepository.values().forEach(book -> {
			if (book.getGenre() == Genre.FICTION)
				fictionShelf.addId(book.getUUID());
			if (book.getGenre() == Genre.FANTASY)
				fantasyShelf.addId(book.getUUID());

		});

		// Add shelves to storage
		this.bookShelves.add(fictionShelf);
		this.bookShelves.add(fantasyShelf);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("Books: [");

		for (Book book : bookRepository.values()) {
			result.append(book.toString()).append(",\n");
		}

		// Safely remove the last comma and space, only if books exist
		if (!bookRepository.isEmpty()) {
			result.setLength(result.length() - 2);
		}
		result.append("]");

		return result.toString();
	}
}