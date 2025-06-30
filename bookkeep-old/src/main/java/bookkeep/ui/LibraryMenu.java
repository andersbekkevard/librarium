package bookkeep.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.Book;
import bookkeep.models.BookBuilder;
import bookkeep.models.OwnedBook;
import bookkeep.models.collections.BookStorage;
import bookkeep.models.history.BookEvent;
import bookkeep.models.history.BookHistory;
import bookkeep.persistance.LibrarySerializer;

public class LibraryMenu {

	/* ================================ CONSTANTS =============================== */
	private static final String RETURN_LABEL = "Return";
	private static final String PRESS_ENTER_MSG = "Press enter to continue...";
	private static final String NO_BOOKS_MSG = "No books available.";
	private static final String NO_SHELVES_MSG = "No shelves available. Please add a shelf first.";
	private static final String LIBRARY_PERSISTENCE_HEADER = "===== Library Persistence =====";

	/* ================================= FIELDS ================================= */
	// Note: removed final from bookStorage so it can be updated on load.
	private BookStorage bookStorage;
	private final Scanner scanner;
	private boolean isTestMenu = false;

	/* =============================== CONSTRUCTOR ============================== */
	public LibraryMenu(BookStorage bookStorage) {
		this.bookStorage = bookStorage;
		scanner = new Scanner(System.in);
	}

	public void makeTestMenu() {
		isTestMenu = true;
	}

	/* ========================= GENERAL HELPER METHODS ========================= */
	/**
	 * Clears the terminal screen using ANSI escape codes.
	 */
	private void clearScreen() {
		System.out.print("\033[H\033[2J");
		System.out.flush();
	}

	/**
	 * Prompts the user to press Enter to continue.
	 */
	private void pressEnterToContinue() {
		System.out.println(PRESS_ENTER_MSG);
		scanner.nextLine();
	}

	/**
	 * Prompts the user for a numeric choice between min and max (inclusive).
	 */
	private int getChoice(int min, int max) {
		while (true) {
			System.out.print("Enter choice (" + min + "-" + max + "): ");
			String input = scanner.nextLine();
			try {
				int choice = Integer.parseInt(input);
				if (choice >= min && choice <= max) {
					return choice;
				} else {
					System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input. Please enter a number.");
			}
		}
	}

	private boolean getYesOrNo(String header) {
		System.out.print(header);
		System.out.println(" (y/n)");
		while (true) {
			String answer = scanner.nextLine();
			if (answer.toLowerCase().equals("y")) {
				return true;
			} else if (answer.toLowerCase().equals("n")) {
				return false;
			}
			System.out.println("Try again. (y/n)");
		}

	}

	/**
	 * Displays a menu with the given header and list of options.
	 * Option 0 is always reserved for the provided returnLabel.
	 *
	 * @param header      The header text to display.
	 * @param options     A list of options.
	 * @param returnLabel The label for option 0.
	 * @return The user's numeric selection.
	 */
	private int selectOption(String header, List<String> options) {
		clearScreen();
		System.out.println(header);
		for (int i = 0; i < options.size(); i++) {
			System.out.println((i + 1) + ". " + options.get(i));
		}
		System.out.println("0. " + RETURN_LABEL);
		return getChoice(0, options.size());
	}

	/**
	 * Displays a list of books (with a header) and allows the user to select one to
	 * view its details.
	 * If the list is empty, a message is displayed.
	 *
	 * @param header The header text.
	 * @param books  The list of books to display.
	 */
	private void listBooksAndSelect(String header, List<Book> books) {
		boolean inListBooksAndSelectMenu = true;
		while (inListBooksAndSelectMenu) {
			clearScreen();
			System.out.println(header);
			if (books.isEmpty()) {
				System.out.println(NO_BOOKS_MSG);
				pressEnterToContinue();
				return;
			}
			for (int i = 0; i < books.size(); i++) {
				Book book = books.get(i);
				System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthorName() + " ("
						+ book.getPublicationYear() + ")");
			}
			System.out.println("0. " + RETURN_LABEL);
			int choice = getChoice(0, books.size());
			if (choice != 0) {
				bookMenu(books.get(choice - 1));
			} else
				inListBooksAndSelectMenu = false;
		}
	}

	/**
	 * Displays a list of books (with a header) for selection without immediately
	 * showing details. Returns the user's selection index (1-indexed) or 0 if
	 * returning.
	 *
	 * @param header The header text.
	 * @param books  The list of books.
	 * @return The selected index, or 0.
	 */
	private int selectBook(String header, List<Book> books) {
		clearScreen();
		System.out.println(header);
		if (books.isEmpty()) {
			System.out.println(NO_BOOKS_MSG);
			pressEnterToContinue();
			return 0;
		}
		for (int i = 0; i < books.size(); i++) {
			Book book = books.get(i);
			System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthorName());
		}
		System.out.println("0. " + RETURN_LABEL);
		return getChoice(0, books.size());
	}

	/* ================================ MAIN MENU =============================== */
	public void start() {
		boolean running = true;
		List<String> mainOptions = new ArrayList<>();
		mainOptions.add("Books Features");
		mainOptions.add("Shelf Management");
		mainOptions.add("Add New Book to Library");
		mainOptions.add("Library Persistence");

		while (running) {
			int choice = selectOption("===== Library Main Menu =====", mainOptions);
			switch (choice) {
				case 1 -> booksFeaturesMenu();
				case 2 -> shelfManagementMenu();
				case 3 -> addNewBook();
				case 4 -> libraryPersistenceMenu();
				case 0 -> {
					running = false;
					System.out.println("Exiting application. Goodbye!");
					pressEnterToContinue();
				}
			}
		}
		scanner.close();
	}

	/* =========================== BOOKS FEATURES MENU ========================== */
	private void booksFeaturesMenu() {
		boolean inBooksMenu = true;
		List<String> options = new ArrayList<>();
		options.add("Show All Books");
		options.add("Show Books by Author");
		options.add("Show Books by Publication Year");
		options.add("Show Books by Year Interval");

		while (inBooksMenu) {
			int choice = selectOption("===== Books Features Menu =====", options);
			switch (choice) {
				case 1 -> listBooksAndSelect("--- All Books in Library ---", bookStorage.getAllBooks());
				case 2 -> showBooksByAuthor();
				case 3 -> showBooksByYear();
				case 4 -> showBooksByYearInterval();
				case 0 -> inBooksMenu = false;
			}
		}
	}

	private void showBooksByAuthor() {
		clearScreen();
		System.out.print("Enter author name: ");
		String author = scanner.nextLine();
		List<Book> books = bookStorage.getBooksByAuthor(author);
		listBooksAndSelect("Books by " + author + ":", books);
	}

	private void showBooksByYear() {
		clearScreen();
		System.out.print("Enter publication year: ");
		try {
			int year = Integer.parseInt(scanner.nextLine());
			List<Book> books = bookStorage.getBooksByYear(year);
			listBooksAndSelect("Books published in " + year + ":", books);
		} catch (NumberFormatException e) {
			System.out.println("Invalid year entered.");
			pressEnterToContinue();
		}
	}

	private void showBooksByYearInterval() {
		clearScreen();
		try {
			System.out.print("Enter start year: ");
			int startYear = Integer.parseInt(scanner.nextLine());
			System.out.print("Enter end year: ");
			int endYear = Integer.parseInt(scanner.nextLine());
			List<Book> books = bookStorage.getBooksByYearInterval(startYear, endYear);
			listBooksAndSelect("Books published between " + startYear + " and " + endYear + ":", books);
		} catch (NumberFormatException e) {
			System.out.println("Invalid year(s) entered.");
			pressEnterToContinue();
		}
	}

	/* ========================== SHELF MANAGEMENT MENU ========================= */
	private void shelfManagementMenu() {
		boolean inShelfMenu = true;
		List<String> options = new ArrayList<>();
		options.add("Select a Shelf");
		options.add("Add Shelf");

		while (inShelfMenu) {
			int choice = selectOption("===== Shelf Management Menu =====", options);
			switch (choice) {
				case 1 -> selectShelf();
				case 2 -> addShelf();
				case 0 -> inShelfMenu = false;
			}
		}
	}

	private void selectShelf() {
		List<String> shelves = bookStorage.getShelfNames();
		if (shelves.isEmpty()) {
			clearScreen();
			System.out.println(NO_SHELVES_MSG);
			pressEnterToContinue();
			return;
		}
		int choice = selectOption("Select a shelf:", shelves);
		if (choice == 0) {
			return;
		}
		String selectedShelf = shelves.get(choice - 1);
		shelfSubMenu(selectedShelf);
	}

	private void shelfSubMenu(String shelfName) {
		boolean inSubMenu = true;
		List<String> options = new ArrayList<>();
		options.add("Add Book to Shelf");
		options.add("Remove Book from Shelf");
		options.add("Delete this Shelf");
		options.add("View Books in Shelf");

		while (inSubMenu) {
			int choice = selectOption("=== Shelf: " + shelfName + " ===", options);
			switch (choice) {
				case 1 -> addBookToShelf(shelfName);
				case 2 -> removeBookFromShelf(shelfName);
				case 3 -> {
					bookStorage.removeShelf(shelfName);
					System.out.println("Shelf '" + shelfName + "' deleted.");
					pressEnterToContinue();
					inSubMenu = false;
				}
				case 4 -> viewBooksInShelf(shelfName);
				case 0 -> inSubMenu = false;
			}
		}
	}

	private void addShelf() {
		clearScreen();
		System.out.print("Enter new shelf name: ");
		String shelfName = scanner.nextLine();
		bookStorage.addShelf(shelfName);
		System.out.println("Shelf added: " + shelfName);
		pressEnterToContinue();
	}

	/**
	 * Returns a list of books in the library that are not already in the specified
	 * shelf.
	 */
	private List<Book> getBooksNotInShelf(String shelfName) {
		List<Book> allBooks = bookStorage.getAllBooks();
		List<Book> shelfBooks = bookStorage.getBooksFromShelfName(shelfName);
		Set<String> shelfUUIDs = shelfBooks.stream()
				.map(book -> book.getUUID().toString())
				.collect(Collectors.toSet());
		return allBooks.stream()
				.filter(book -> !shelfUUIDs.contains(book.getUUID().toString()))
				.collect(Collectors.toList());
	}

	private void addBookToShelf(String shelfName) {
		List<Book> availableBooks = getBooksNotInShelf(shelfName);
		int choice = selectBook("Select a book to add to shelf '" + shelfName + "':", availableBooks);
		if (choice == 0) {
			return;
		}
		Book selectedBook = availableBooks.get(choice - 1);
		bookStorage.addBookToShelf(shelfName, selectedBook);
		System.out.println("Book '" + selectedBook.getTitle() + "' added to shelf '" + shelfName + "'.");
		pressEnterToContinue();
	}

	private void removeBookFromShelf(String shelfName) {
		List<Book> shelfBooks = bookStorage.getBooksFromShelfName(shelfName);
		int choice = selectBook("Select a book to remove from shelf '" + shelfName + "':", shelfBooks);
		if (choice == 0) {
			return;
		}
		Book selectedBook = shelfBooks.get(choice - 1);
		bookStorage.removeBookFromShelf(shelfName, selectedBook);
		System.out.println("Book '" + selectedBook.getTitle() + "' removed from shelf '" + shelfName + "'.");
		pressEnterToContinue();
	}

	private void viewBooksInShelf(String shelfName) {
		List<Book> shelfBooks = bookStorage.getBooksFromShelfName(shelfName);
		listBooksAndSelect("Books in shelf '" + shelfName + "':", shelfBooks);
	}

	/* ======================== LIBRARY PERSISTENCE MENU ======================== */
	private void libraryPersistenceMenu() {
		LibrarySerializer serializer = new LibrarySerializer();
		if (isTestMenu) {
			serializer.makeTestSerializer();
		}

		List<String> options = new ArrayList<>();
		options.add("Save Library");
		options.add("Load Library");

		boolean inPersistenceMenu = true;
		while (inPersistenceMenu) {
			int choice = selectOption(LIBRARY_PERSISTENCE_HEADER, options);
			switch (choice) {
				case 1 -> {
					try {
						serializer.save(bookStorage);
						System.out.println("Library saved successfully.");
					} catch (IOException e) {
						System.out.println("Error saving library: " + e.getMessage());
					}
					pressEnterToContinue();
				}
				case 2 -> {
					bookStorage = serializer.load();
					System.out.println("Library loaded successfully.");
					pressEnterToContinue();
				}
				case 0 -> inPersistenceMenu = false;
			}
		}
	}

	/* ============================== ADD NEW BOOK ============================== */
	private void addNewBook() {
		clearScreen();
		System.out.println("--- Add New Book ---");

		System.out.print("Title: ");
		String title = scanner.nextLine();

		System.out.print("Author: ");
		String author = scanner.nextLine();

		int year;
		try {
			System.out.print("Publication Year: ");
			year = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid year input. Book not added.");
			pressEnterToContinue();
			return;
		}

		int pageCount;
		try {
			System.out.print("Page Count: ");
			pageCount = Integer.parseInt(scanner.nextLine());
		} catch (NumberFormatException e) {
			System.out.println("Invalid page count. Book not added.");
			pressEnterToContinue();
			return;
		}

		System.out.print("Genre (e.g., FICTION, FANTASY): ");
		String genreInput = scanner.nextLine();
		Genre genre;
		try {
			genre = Genre.valueOf(genreInput.toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid genre input. Defaulting to FICTION.");
			genre = Genre.FICTION;
		}

		System.out.print("Format (DIGITAL/PHYSICAL): ");
		String formatInput = scanner.nextLine();
		BookFormat format;
		try {
			format = BookFormat.valueOf(formatInput.toUpperCase());
		} catch (IllegalArgumentException e) {
			System.out.println("Invalid format input. Defaulting to PHYSICAL.");
			format = BookFormat.PHYSICAL;
		}

		BookBuilder builder = new BookBuilder();
		OwnedBook newBook = builder.withTitle(title)
				.withAuthorName(author)
				.withPublicationYear(year)
				.withPageCount(pageCount)
				.withGenre(genre)
				.withFormat(format)
				.buildOwnedBook();
		bookStorage.addBook(newBook);
		System.out.println("New book '" + title + "' added to library.");

		// Delegate the shelf selection logic to a helper method.
		processShelfSelectionForBook(newBook);
	}

	/**
	 * Helper function to processes the user's choice to add the new book to a
	 * shelf. Offers the option to choose one of the existing shelves or to create a
	 * new shelf. If the user chooses to skip, no shelf is used.
	 */
	private void processShelfSelectionForBook(Book newBook) {
		// Retrieve existing shelves.
		List<String> shelves = bookStorage.getShelfNames();

		// Build the options list: existing shelves plus an extra option to create a new
		// shelf.
		List<String> options = new ArrayList<>(shelves);
		options.add("Create new shelf");

		int choice = selectOption("Select a shelf to add the book to:", options);

		if (choice == 0) {
			System.out.println("Book remains in library only.");
		} else if (choice == options.size()) { // User selected the last option: "Create new shelf"
			createShelfAndAddBook(newBook);
		} else {
			// User selected an existing shelf.
			String selectedShelf = options.get(choice - 1);
			bookStorage.addBookToShelf(selectedShelf, newBook);
			System.out.println("Book added to shelf '" + selectedShelf + "'.");
		}
		pressEnterToContinue();
	}

	/**
	 * Helper function that prompts the user for a new shelf name, creates the
	 * shelf, and adds the given book to it.
	 */
	private void createShelfAndAddBook(Book newBook) {
		System.out.print("Enter new shelf name: ");
		String newShelfName = scanner.nextLine();
		bookStorage.addShelf(newShelfName);
		bookStorage.addBookToShelf(newShelfName, newBook);
		System.out.println("Book added to new shelf '" + newShelfName + "'.");
	}

	/* ================================ BOOK MENU =============================== */
	private void bookMenu(Book book) {
		clearScreen();
		List<String> options = new ArrayList<>();
		options.add("View details");
		options.add("History");
		options.add("Interact");

		boolean inBookMenu = true;
		while (inBookMenu) {
			int choice = selectOption("===== Book Menu =====", options);
			switch (choice) {
				case 1 -> displayBookDetails(book);
				case 2 -> displayBookHistory(book);
				case 3 -> interactWithBook(book);
				case 0 -> {
					inBookMenu = false;
				}
			}
		}
	}

	/* ========================== DISPLAY BOOK DETAILS ========================== */
	private void displayBookDetails(Book book) {
		clearScreen();
		System.out.println("Title: " + book.getTitle());
		System.out.println("Author: " + book.getAuthorName());
		System.out.println("Publication Year: " + book.getPublicationYear());
		System.out.println("Page Count: " + book.getPageCount());
		System.out.println("Genre: " + book.getGenre());
		if (book instanceof OwnedBook) {
			OwnedBook owned = (OwnedBook) book;
			System.out.println("Format: " + owned.getFormat());
			System.out.println("Current Page: " + owned.getPageNumber());
		}
		pressEnterToContinue();
	}

	/* ========================== DISPLAY BOOK HISTORY ========================== */
	private void displayBookHistory(Book book) {
		clearScreen();
		if (!(book instanceof OwnedBook)) {
			System.out.println("No History for Books that are not Owned:");
			return;
		}
		BookHistory history = book.getHistory();
		System.out.println("History:");
		System.out.println("Started Reading: " + history.getStartedReading());
		System.out.println("Finished Reading: " + history.getFinishedReading());
		System.out.println("Reading Duration: " + history.getReadingDuration());
		for (BookEvent event : history.getListOfEvents()) {
			System.out.println(event);
		}
		System.out.println("Review: " + history.getReview());

		pressEnterToContinue();
	}

	/* =========================== INTERACT WITH BOOK =========================== */
	/*
	 * The logic for interacting with a book is tightly coupled with the book class
	 * itself, and with its states.
	 * This is why I try to handle most if the interaction internally in the book,
	 * and not here in the LibraryMenu class. A try catch block lets only "legal"
	 * actions be performed
	 */
	private void interactWithBook(Book book) {
		clearScreen();
		if (!(book instanceof OwnedBook)) {
			System.out.println("Can only interact with owned books at this point");
			pressEnterToContinue();
			return;
		}
		// The rest of the method we can assume the book is an OwnedBook
		List<String> options = new ArrayList<>();
		options.add("Change state");
		options.add("Increment pagenumber");
		options.add("Comment");
		options.add("Quote");
		options.add("Review");

		boolean inInteractWithBookMenu = true;
		while (inInteractWithBookMenu) {
			int choice = selectOption("===== Book Menu =====", options);
			switch (choice) {
				case 1 -> changeState(book);
				case 2 -> incrementPageNumber(book);
				case 3 -> comment(book);
				case 4 -> writeQuote(book);
				case 5 -> review(book);
				case 0 -> inInteractWithBookMenu = false;
			}
			pressEnterToContinue();
		}
	}

	public void changeState(Book book) {
		clearScreen();
		System.out.println("Current state is: " + book.getStateName());
		boolean wantsToChangeState = getYesOrNo("Are you sure?");
		if (wantsToChangeState) {
			book.changeState();
			System.out.println("State changed");
		} else {
			System.out.println("State remains unchanged");
		}
	}

	public void incrementPageNumber(Book book) {
		clearScreen();
		System.out.println("Current page = " + book.getPageNumber() + "/" + book.getPageCount());
		System.out.println("Increment Page:");
		int choice = getChoice(0, 200);
		try {
			book.incrementPageNumber(choice);
			System.out.println("Page Number incremented");
		} catch (IllegalArgumentException e) {
			System.out.println("Cannot increment that far");
		} catch (Exception e) {
			System.out.println("Cannot increment page number in current state");
		}
	}

	public void comment(Book book) {
		clearScreen();
		System.out.println("Write comment:");
		String comment = scanner.nextLine();
		boolean wantsToKeepComment = getYesOrNo("Are you sure?");

		try {
			if (wantsToKeepComment) {
				book.addComment(comment);
				System.out.println("Comment added successfully");
			}

		} catch (Exception e) {
			System.out.println("Cannot comment in current state");
		}
	}

	public void writeQuote(Book book) {
		clearScreen();
		System.out.println("Write quote:");
		String quote = scanner.nextLine();
		System.out.println("On page number:");
		int pageNumber = getChoice(0, book.getPageCount());
		boolean wantsToKeepQuote = getYesOrNo("Are you sure you want to add this quote?");

		try {
			if (wantsToKeepQuote) {
				book.addQuote(quote, pageNumber);
				System.out.println("Quote added successfully");
			}

		} catch (Exception e) {
			System.out.println("Cannot comment in current state");
		}
	}

	public void review(Book book) {
		int MIN_RATING = 1;
		int MAX_RATING = 5;

		clearScreen();

		System.out.println("Write review:");
		String reviewText = scanner.nextLine();
		System.out.println("Rating (out of five stars):");
		int rating = getChoice(MIN_RATING, MAX_RATING);
		boolean wantsToAddReview = getYesOrNo("Are you sure you want to add this review?");

		try {
			if (wantsToAddReview) {
				book.review(reviewText, rating);
				System.out.println("Rating performed successfully");
			}

		} catch (Exception e) {
			System.out.println("Cannot rate in current state");
		}
	}
}
