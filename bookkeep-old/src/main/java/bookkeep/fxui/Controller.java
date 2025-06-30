package bookkeep.fxui;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import bookkeep.enums.BookFormat;
import bookkeep.enums.Genre;
import bookkeep.models.Book;
import bookkeep.models.BookBuilder;
import bookkeep.models.OwnedBook;
import bookkeep.models.collections.BookStorage;
import bookkeep.models.history.BookEvent;
import bookkeep.models.history.BookHistory;
import bookkeep.models.states.FinishedState;
import bookkeep.models.states.InProgressState;
import bookkeep.models.states.NotStartedState;
import bookkeep.persistance.LibrarySerializer;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.util.Callback;

public class Controller {

	// Main container references
	@FXML
	private TabPane mainTabPane;
	@FXML
	private Tab libraryTab;
	@FXML
	private Tab shelvesTab;
	@FXML
	private Tab addBookTab;
	@FXML
	private Tab settingsTab;

	// Library Tab components
	@FXML
	private TableView<Book> bookTableView;
	@FXML
	private TableColumn<Book, String> titleColumn;
	@FXML
	private TableColumn<Book, String> authorColumn;
	@FXML
	private TableColumn<Book, String> yearColumn;
	@FXML
	private TableColumn<Book, String> genreColumn;
	@FXML
	private TableColumn<Book, String> stateColumn;
	@FXML
	private TextField searchField;
	@FXML
	private ComboBox<String> searchTypeComboBox;

	// Book details pane
	@FXML
	private Pane bookDetailsPane;
	@FXML
	private Label bookTitleLabel;
	@FXML
	private Label bookAuthorLabel;
	@FXML
	private Label bookYearLabel;
	@FXML
	private Label bookGenreLabel;
	@FXML
	private Label bookStateLabel;
	@FXML
	private Label bookFormatLabel;
	@FXML
	private Label bookPageLabel;
	@FXML
	private Button changeStateButton;
	@FXML
	private TextField pageNumberField;
	@FXML
	private Button updatePageButton;
	@FXML
	private TextArea commentArea;
	@FXML
	private Button addCommentButton;
	@FXML
	private TextArea quoteArea;
	@FXML
	private TextField quotePageField;
	@FXML
	private Button addQuoteButton;
	@FXML
	private TextArea reviewArea;
	@FXML
	private Slider ratingSlider;
	@FXML
	private Button addReviewButton;
	@FXML
	private ListView<String> eventsListView;

	// Shelves Tab components
	@FXML
	private ComboBox<String> shelfSelector;
	@FXML
	private Button addShelfButton;
	@FXML
	private Button removeShelfButton;
	@FXML
	private TableView<Book> shelfBooksTableView;
	@FXML
	private TableColumn<Book, String> shelfBookTitleColumn;
	@FXML
	private TableColumn<Book, String> shelfBookAuthorColumn;
	@FXML
	private TableColumn<Book, String> shelfBookYearColumn;
	@FXML
	private Button addBookToShelfButton;
	@FXML
	private Button removeBookFromShelfButton;

	// Add Book Tab components
	@FXML
	private TextField newBookTitle;
	@FXML
	private TextField newBookAuthor;
	@FXML
	private TextField newBookYear;
	@FXML
	private TextField newBookPages;
	@FXML
	private ComboBox<Genre> newBookGenre;
	@FXML
	private ComboBox<BookFormat> newBookFormat;
	@FXML
	private ComboBox<String> newBookShelf;
	@FXML
	private CheckBox createNewShelfCheckbox;
	@FXML
	private TextField newShelfNameField;
	@FXML
	private Button addNewBookButton;

	// Settings Tab components
	@FXML
	private Button saveLibraryButton;
	@FXML
	private Button loadLibraryButton;
	@FXML
	private Label lastSavedLabel;

	// Class variables
	private BookStorage library;
	private LibrarySerializer serializer;
	private Book selectedBook;
	private String selectedShelf;

	/**
	 * Initialize the controller with necessary objects.
	 * Called by App.java after FXML loading.
	 */
	public void initialize(BookStorage library, LibrarySerializer serializer) {
		this.library = library;
		this.serializer = serializer;

		// Initialize UI components
		setupLibraryTab();
		setupShelvesTab();
		setupAddBookTab();
		setupSettingsTab();

		// Hide book details initially until a book is selected
		bookDetailsPane.setVisible(false);

		// Load library data
		refreshLibraryView();
		refreshShelfSelector();
	}

	/**
	 * Setup for the Library tab
	 */
	private void setupLibraryTab() {
		// Configure table columns for book table
		titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
		authorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthorName()));
		yearColumn.setCellValueFactory(
				data -> new SimpleStringProperty(String.valueOf(data.getValue().getPublicationYear())));
		genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre().toString()));
		stateColumn.setCellValueFactory(data -> {
			if (data.getValue() instanceof OwnedBook) {
				return new SimpleStringProperty(((OwnedBook) data.getValue()).getStateName());
			}
			return new SimpleStringProperty("N/A");
		});

		// Configure search type combo box
		searchTypeComboBox.setItems(FXCollections.observableArrayList(
				"Title", "Author", "Year", "Genre"));
		searchTypeComboBox.getSelectionModel().selectFirst();

		// Set up selection listener for book table
		bookTableView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						selectedBook = newSelection;
						showBookDetails(selectedBook);
					}
				});

		// Set up search functionality
		searchField.textProperty()
				.addListener((obs, oldText, newText) -> filterBooks(newText, searchTypeComboBox.getValue()));
	}

	/**
	 * Setup for the Shelves tab
	 */
	private void setupShelvesTab() {
		// Configure table columns for shelf books table
		shelfBookTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
		shelfBookAuthorColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getAuthorName()));
		shelfBookYearColumn.setCellValueFactory(
				data -> new SimpleStringProperty(String.valueOf(data.getValue().getPublicationYear())));

		// Set up selection listener for shelf selector
		shelfSelector.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						selectedShelf = newSelection;
						refreshShelfBooksView();
					}
				});

		// Set up selection listener for shelf books table
		shelfBooksTableView.getSelectionModel().selectedItemProperty().addListener(
				(obs, oldSelection, newSelection) -> {
					if (newSelection != null) {
						selectedBook = newSelection;
						showBookDetails(selectedBook);
						mainTabPane.getSelectionModel().select(libraryTab);
					}
				});
	}

	/**
	 * Setup for the Add Book tab
	 */
	private void setupAddBookTab() {
		// Set up genre and format combo boxes
		newBookGenre.setItems(FXCollections.observableArrayList(Genre.values()));
		newBookFormat.setItems(FXCollections.observableArrayList(BookFormat.values()));

		// Default values
		newBookGenre.getSelectionModel().select(Genre.FICTION);
		newBookFormat.getSelectionModel().select(BookFormat.PHYSICAL);

		// Hide/show new shelf name field based on checkbox
		newShelfNameField.visibleProperty().bind(createNewShelfCheckbox.selectedProperty());

		// Link checkbox and combo box states
		createNewShelfCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
			newBookShelf.setDisable(newVal);
			if (newVal) {
				newBookShelf.getSelectionModel().clearSelection();
			}
		});
	}

	/**
	 * Setup for the Settings tab
	 */
	private void setupSettingsTab() {
		lastSavedLabel.setText("Library last saved: Not saved yet");
	}

	/**
	 * Refresh the library view with all books
	 */
	private void refreshLibraryView() {
		List<Book> allBooks = library.getAllBooks();
		bookTableView.setItems(FXCollections.observableArrayList(allBooks));
	}

	/**
	 * Refresh the shelf selector with all available shelves
	 */
	private void refreshShelfSelector() {
		List<String> shelfNames = library.getShelfNames();
		shelfSelector.setItems(FXCollections.observableArrayList(shelfNames));
		newBookShelf.setItems(FXCollections.observableArrayList(shelfNames));

		if (!shelfNames.isEmpty()) {
			shelfSelector.getSelectionModel().selectFirst();
			selectedShelf = shelfSelector.getValue();
			refreshShelfBooksView();
		}
	}

	/**
	 * Refresh the books displayed for the selected shelf
	 */
	private void refreshShelfBooksView() {
		if (selectedShelf != null) {
			List<Book> shelfBooks = library.getBooksFromShelfName(selectedShelf);
			shelfBooksTableView.setItems(FXCollections.observableArrayList(shelfBooks));
		} else {
			shelfBooksTableView.getItems().clear();
		}
	}

	/**
	 * Filter the book table based on search text and type
	 */
	private void filterBooks(String searchText, String searchType) {
		if (searchText == null || searchText.isEmpty()) {
			refreshLibraryView();
			return;
		}

		List<Book> filteredBooks = library.getAllBooks().stream()
				.filter(book -> {
					switch (searchType) {
						case "Title":
							return book.getTitle().toLowerCase().contains(searchText.toLowerCase());
						case "Author":
							return book.getAuthorName().toLowerCase().contains(searchText.toLowerCase());
						case "Year":
							try {
								int year = Integer.parseInt(searchText);
								return book.getPublicationYear() == year;
							} catch (NumberFormatException e) {
								return false;
							}
						case "Genre":
							return book.getGenre().toString().toLowerCase().contains(searchText.toLowerCase());
						default:
							return true;
					}
				})
				.collect(Collectors.toList());

		bookTableView.setItems(FXCollections.observableArrayList(filteredBooks));
	}

	/**
	 * Display detailed information about the selected book
	 */
	private void showBookDetails(Book book) {
		if (!(book instanceof OwnedBook)) {
			// Only OwnedBooks are fully supported for now
			bookDetailsPane.setVisible(false);
			return;
		}

		OwnedBook ownedBook = (OwnedBook) book;

		// Basic book information
		bookTitleLabel.setText(ownedBook.getTitle());
		bookAuthorLabel.setText("by " + ownedBook.getAuthorName());
		bookYearLabel.setText("Published: " + ownedBook.getPublicationYear());
		bookGenreLabel.setText("Genre: " + ownedBook.getGenre().toString());
		bookStateLabel.setText("Status: " + ownedBook.getStateName());
		bookFormatLabel.setText("Format: " + ownedBook.getFormat().toString());
		bookPageLabel.setText("Page: " + ownedBook.getPageNumber() + " of " + ownedBook.getPageCount());

		// Configure buttons and inputs based on book state
		configureUIForBookState(ownedBook);

		// Load book history/events
		loadBookEvents(ownedBook);

		// Make the details pane visible
		bookDetailsPane.setVisible(true);
	}

	/**
	 * Configure the UI elements based on the book's current state
	 */
	private void configureUIForBookState(OwnedBook book) {
		boolean isNotStarted = book.getState() instanceof NotStartedState;
		boolean isInProgress = book.getState() instanceof InProgressState;
		boolean isFinished = book.getState() instanceof FinishedState;

		// Change state button text and action based on current state
		if (isNotStarted) {
			changeStateButton.setText("Start Reading");
			pageNumberField.setDisable(true);
			updatePageButton.setDisable(true);
			commentArea.setDisable(true);
			addCommentButton.setDisable(true);
			quoteArea.setDisable(true);
			quotePageField.setDisable(true);
			addQuoteButton.setDisable(true);
			reviewArea.setDisable(true);
			ratingSlider.setDisable(true);
			addReviewButton.setDisable(true);
		} else if (isInProgress) {
			changeStateButton.setText("Finish Reading");
			pageNumberField.setDisable(false);
			updatePageButton.setDisable(false);
			commentArea.setDisable(false);
			addCommentButton.setDisable(false);
			quoteArea.setDisable(false);
			quotePageField.setDisable(false);
			addQuoteButton.setDisable(false);
			reviewArea.setDisable(true);
			ratingSlider.setDisable(true);
			addReviewButton.setDisable(true);
		} else if (isFinished) {
			changeStateButton.setText("Finished");
			changeStateButton.setDisable(true);
			pageNumberField.setDisable(true);
			updatePageButton.setDisable(true);
			commentArea.setDisable(false);
			addCommentButton.setDisable(false);
			quoteArea.setDisable(false);
			quotePageField.setDisable(false);
			addQuoteButton.setDisable(false);
			reviewArea.setDisable(false);
			ratingSlider.setDisable(false);
			addReviewButton.setDisable(false);

			// If book has a review already, load it
			BookHistory history = book.getHistory();
			if (history.hasReview()) {
				BookEvent review = history.getReview();
				reviewArea.setText(review.getText());
			}
		}
	}

	/**
	 * Load and display book events in the events list view
	 */
	private void loadBookEvents(OwnedBook book) {
		BookHistory history = book.getHistory();
		ObservableList<String> events = FXCollections.observableArrayList();

		// Add reading state events
		if (history.getStartedReading() != null) {
			events.add(formatEvent(history.getStartedReading()));
		}
		if (history.getFinishedReading() != null) {
			events.add(formatEvent(history.getFinishedReading()));
		}

		// Add comments
		for (BookEvent event : history.getComments()) {
			events.add(formatEvent(event));
		}

		// Add quotes
		for (BookEvent event : history.getQuotes()) {
			events.add(formatEvent(event));
		}

		// Add afterthoughts
		for (BookEvent event : history.getAfterThoughts()) {
			events.add(formatEvent(event));
		}

		// Sort events chronologically
		events.sort((e1, e2) -> e1.compareTo(e2));

		eventsListView.setItems(events);
	}

	/**
	 * Format a book event for display
	 */
	private String formatEvent(BookEvent event) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		LocalDateTime dateTime = LocalDateTime.ofInstant(
				event.getTimestamp(), ZoneId.systemDefault());

		String formattedTime = dateTime.format(formatter);
		String eventType = event.getType().toString();
		String eventText = event.getText() != null ? event.getText() : "";

		return formattedTime + " | " + eventType + (eventText.isEmpty() ? "" : " | " + eventText);
	}

	/* --- BUTTON HANDLERS --- */

	/**
	 * Handle changing the state of a book
	 */
	@FXML
	private void handleChangeState() {
		if (selectedBook instanceof OwnedBook) {
			OwnedBook book = (OwnedBook) selectedBook;
			book.changeState();

			// Refresh UI
			showBookDetails(book);
			refreshLibraryView();
		}
	}

	/**
	 * Handle updating the page number of a book
	 */
	@FXML
	private void handleUpdatePage() {
		if (selectedBook instanceof OwnedBook) {
			OwnedBook book = (OwnedBook) selectedBook;
			try {
				int newPage = Integer.parseInt(pageNumberField.getText());
				if (newPage >= 0 && newPage <= book.getPageCount()) {
					int increment = newPage - book.getPageNumber();
					book.incrementPageNumber(increment);
					showBookDetails(book);
					bookPageLabel.setText("Page: " + book.getPageNumber() + " of " + book.getPageCount());
				} else {
					showAlert(Alert.AlertType.ERROR, "Invalid Page Number",
							"Page number must be between 0 and " + book.getPageCount());
				}
			} catch (NumberFormatException e) {
				showAlert(Alert.AlertType.ERROR, "Invalid Input",
						"Please enter a valid number");
			} catch (Exception e) {
				showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
			}
		}
	}

	/**
	 * Handle adding a comment to a book
	 */
	@FXML
	private void handleAddComment() {
		if (selectedBook instanceof OwnedBook) {
			OwnedBook book = (OwnedBook) selectedBook;
			String comment = commentArea.getText().trim();

			if (!comment.isEmpty()) {
				try {
					book.addComment(comment);
					commentArea.clear();
					loadBookEvents(book);
				} catch (Exception e) {
					showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
				}
			}
		}
	}

	/**
	 * Handle adding a quote to a book
	 */
	@FXML
	private void handleAddQuote() {
		if (selectedBook instanceof OwnedBook) {
			OwnedBook book = (OwnedBook) selectedBook;
			String quote = quoteArea.getText().trim();

			if (!quote.isEmpty()) {
				try {
					int page = Integer.parseInt(quotePageField.getText());
					book.addQuote(quote, page);
					quoteArea.clear();
					quotePageField.clear();
					loadBookEvents(book);
				} catch (NumberFormatException e) {
					showAlert(Alert.AlertType.ERROR, "Invalid Page",
							"Please enter a valid page number");
				} catch (Exception e) {
					showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
				}
			}
		}
	}

	/**
	 * Handle adding a review to a book
	 */
	@FXML
	private void handleAddReview() {
		if (selectedBook instanceof OwnedBook) {
			OwnedBook book = (OwnedBook) selectedBook;
			String review = reviewArea.getText().trim();

			if (!review.isEmpty()) {
				try {
					int rating = (int) ratingSlider.getValue();
					book.review(review, rating);
					loadBookEvents(book);
					showAlert(Alert.AlertType.INFORMATION, "Review Added",
							"Your review has been added successfully");
				} catch (Exception e) {
					showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
				}
			}
		}
	}

	/**
	 * Handle adding a new shelf
	 */
	@FXML
	private void handleAddShelf() {
		TextInputDialog dialog = new TextInputDialog();
		dialog.setTitle("Add Shelf");
		dialog.setHeaderText("Create a new shelf");
		dialog.setContentText("Shelf name:");

		Optional<String> result = dialog.showAndWait();
		result.ifPresent(name -> {
			if (!name.trim().isEmpty()) {
				library.addShelf(name);
				refreshShelfSelector();
				shelfSelector.getSelectionModel().select(name);
			}
		});
	}

	/**
	 * Handle removing a shelf
	 */
	@FXML
	private void handleRemoveShelf() {
		if (selectedShelf != null) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Remove Shelf");
			alert.setHeaderText("Remove Shelf: " + selectedShelf);
			alert.setContentText("Are you sure you want to remove this shelf? Books will remain in the library.");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				library.removeShelf(selectedShelf);
				refreshShelfSelector();
			}
		}
	}

	/**
	 * Handle adding a book to the current shelf
	 */
	@FXML
	private void handleAddBookToShelf() {
		if (selectedShelf == null) {
			showAlert(Alert.AlertType.ERROR, "No Shelf Selected",
					"Please select a shelf first");
			return;
		}

		// Create a dialog to select a book
		Dialog<Book> dialog = new Dialog<>();
		dialog.setTitle("Add Book to Shelf");
		dialog.setHeaderText("Select a book to add to shelf: " + selectedShelf);

		// Set the button types
		ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

		// Create the book list view and add it to the dialog
		ListView<Book> bookListView = new ListView<>();

		// Only show books not already in the shelf
		List<Book> booksNotInShelf = library.getAllBooks().stream()
				.filter(book -> {
					try {
						List<Book> shelfBooks = library.getBooksFromShelfName(selectedShelf);
						return !shelfBooks.contains(book);
					} catch (Exception e) {
						return true;
					}
				})
				.collect(Collectors.toList());

		bookListView.setItems(FXCollections.observableArrayList(booksNotInShelf));

		// Set cell factory to display book titles
		bookListView.setCellFactory(new Callback<ListView<Book>, ListCell<Book>>() {
			@Override
			public ListCell<Book> call(ListView<Book> param) {
				return new ListCell<Book>() {
					@Override
					protected void updateItem(Book book, boolean empty) {
						super.updateItem(book, empty);
						if (empty || book == null) {
							setText(null);
						} else {
							setText(book.getTitle() + " by " + book.getAuthorName());
						}
					}
				};
			}
		});

		dialog.getDialogPane().setContent(bookListView);

		// Convert the result to a book when the add button is clicked
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == addButtonType) {
				return bookListView.getSelectionModel().getSelectedItem();
			}
			return null;
		});

		Optional<Book> result = dialog.showAndWait();
		result.ifPresent(book -> {
			library.addBookToShelf(selectedShelf, book);
			refreshShelfBooksView();
		});
	}

	/**
	 * Handle removing a book from the current shelf
	 */
	@FXML
	private void handleRemoveBookFromShelf() {
		if (selectedShelf == null) {
			return;
		}

		Book book = shelfBooksTableView.getSelectionModel().getSelectedItem();
		if (book != null) {
			library.removeBookFromShelf(selectedShelf, book);
			refreshShelfBooksView();
		}
	}

	/**
	 * Handle adding a new book to the library
	 */
	@FXML
	private void handleAddNewBook() {
		try {
			// Validate inputs
			String title = newBookTitle.getText().trim();
			String author = newBookAuthor.getText().trim();
			int year = Integer.parseInt(newBookYear.getText().trim());
			int pages = Integer.parseInt(newBookPages.getText().trim());
			Genre genre = newBookGenre.getValue();
			BookFormat format = newBookFormat.getValue();

			if (title.isEmpty() || author.isEmpty()) {
				showAlert(Alert.AlertType.ERROR, "Missing Information",
						"Title and author are required");
				return;
			}

			// Create and add the book
			OwnedBook newBook = new BookBuilder()
					.withTitle(title)
					.withAuthorName(author)
					.withPublicationYear(year)
					.withPageCount(pages)
					.withGenre(genre)
					.withFormat(format)
					.buildOwnedBook();

			library.addBook(newBook);

			// Add to shelf if needed
			if (createNewShelfCheckbox.isSelected()) {
				String newShelfName = newShelfNameField.getText().trim();
				if (!newShelfName.isEmpty()) {
					library.addShelf(newShelfName);
					library.addBookToShelf(newShelfName, newBook);
					refreshShelfSelector();
				}
			} else if (newBookShelf.getValue() != null) {
				library.addBookToShelf(newBookShelf.getValue(), newBook);
			}

			// Clear the form
			newBookTitle.clear();
			newBookAuthor.clear();
			newBookYear.clear();
			newBookPages.clear();

			// Refresh views
			refreshLibraryView();
			refreshShelfSelector();

			showAlert(Alert.AlertType.INFORMATION, "Book Added",
					"The book has been added to your library");

			// Switch to the library tab
			mainTabPane.getSelectionModel().select(libraryTab);

		} catch (NumberFormatException e) {
			showAlert(Alert.AlertType.ERROR, "Invalid Input",
					"Please enter valid numbers for year and pages");
		} catch (Exception e) {
			showAlert(Alert.AlertType.ERROR, "Error", e.getMessage());
		}
	}

	/**
	 * Handle saving the library
	 */
	@FXML
	private void handleSaveLibrary() {
		try {
			serializer.save(library);
			lastSavedLabel.setText("Library last saved: " +
					LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			showAlert(Alert.AlertType.INFORMATION, "Library Saved",
					"Your library has been saved successfully");
		} catch (IOException e) {
			showAlert(Alert.AlertType.ERROR, "Save Error",
					"Error saving library: " + e.getMessage());
		}
	}

	/**
	 * Handle loading the library
	 */
	@FXML
	private void handleLoadLibrary() {
		Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
		alert.setTitle("Load Library");
		alert.setHeaderText("Load library from disk");
		alert.setContentText("This will replace your current library. Continue?");

		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			try {
				library = serializer.load();
				refreshLibraryView();
				refreshShelfSelector();
				showAlert(Alert.AlertType.INFORMATION, "Library Loaded",
						"Library has been loaded successfully");
			} catch (Exception e) {
				showAlert(Alert.AlertType.ERROR, "Load Error",
						"Error loading library: " + e.getMessage());
			}
		}
	}

	/**
	 * Helper method to show alerts
	 */
	private void showAlert(Alert.AlertType type, String title, String content) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}
}