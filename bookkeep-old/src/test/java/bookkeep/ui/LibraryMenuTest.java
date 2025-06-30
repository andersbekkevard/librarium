package bookkeep.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bookkeep.models.Book;
import bookkeep.models.collections.BookStorage;

class LibraryMenuTest {

	private final PrintStream originalOut = System.out;
	private final InputStream originalIn = System.in;
	private ByteArrayOutputStream outContent;
	private BookStorage bookStorage;

	@BeforeEach
	void setUp() {
		outContent = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outContent));
		// Use an initially empty BookStorage.
		bookStorage = new BookStorage();
	}

	@AfterEach
	void tearDown() {
		System.setOut(originalOut);
		System.setIn(originalIn);
	}

	@Test
	void testExitImmediately() {
		// Simulate entering "0" immediately at the main menu.
		// Provide an extra newline for pressEnterToContinue().
		String simulatedInput = "0\n\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

		LibraryMenu menu = new LibraryMenu(bookStorage);
		menu.makeTestMenu();
		menu.start();

		String output = outContent.toString();
		assertTrue(output.contains("Exiting application. Goodbye!"),
				"Output should contain exit message");
	}

	@Test
	void testAddNewBookFlowSkipShelf() {
		/*
		 * Simulated flow:
		 * 1. At main menu, choose option 3 (Add New Book to Library).
		 * 2. Enter book details:
		 * Title: Test Book
		 * Author: Test Author
		 * Publication Year: 2021
		 * Page Count: 100
		 * Genre: FICTION
		 * Format: DIGITAL
		 * 3. When prompted to choose a shelf, choose option 0 (skip adding to shelf).
		 * 4. Press Enter to continue.
		 * 5. At main menu, choose 0 to exit.
		 * 6. Provide final newline for the last pressEnterToContinue().
		 */
		String simulatedInput = String.join("\n",
				"3", // Main menu: Add New Book to Library.
				"Test Book", // Title.
				"Test Author", // Author.
				"2021", // Publication Year.
				"100", // Page Count.
				"FICTION", // Genre.
				"DIGITAL", // Format.
				"0", // When asked for shelf selection, choose 0 (skip).
				"", // Press Enter to continue in shelf selection.
				"0", // At main menu, exit.
				"" // Final pressEnterToContinue at exit.
		) + "\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

		LibraryMenu menu = new LibraryMenu(bookStorage);
		menu.makeTestMenu();
		menu.start();

		// Verify that one book was added.
		List<Book> books = bookStorage.getAllBooks();
		assertEquals(1, books.size(), "One book should have been added to the library");
		assertEquals("Test Book", books.get(0).getTitle(), "The book title should be 'Test Book'");

		String output = outContent.toString();
		assertTrue(output.contains("New book 'Test Book' added to library."),
				"Output should confirm that the new book was added");
	}

	@Test
	void testLibraryPersistenceMenuSave() {
		/*
		 * Simulated flow:
		 * 1. At main menu, choose option 4 (Library Persistence).
		 * 2. In the persistence menu, choose option 1 (Save Library).
		 * 3. Press Enter to continue.
		 * 4. In the persistence menu, choose 0 to return to the main menu.
		 * 5. At main menu, choose 0 to exit.
		 * 6. Provide final newline for the last pressEnterToContinue().
		 */
		String simulatedInput = String.join("\n",
				"4", // Main menu: Library Persistence.
				"1", // Persistence menu: Save Library.
				"", // Press Enter to continue.
				"0", // Return to Main Menu.
				"0", // Main menu: Exit.
				"" // Final pressEnterToContinue.
		) + "\n";
		System.setIn(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));

		LibraryMenu menu = new LibraryMenu(bookStorage);
		menu.makeTestMenu();
		menu.start();

		String output = outContent.toString();
		assertTrue(output.contains("Library saved successfully."),
				"Output should confirm that the library was saved");
	}
}
