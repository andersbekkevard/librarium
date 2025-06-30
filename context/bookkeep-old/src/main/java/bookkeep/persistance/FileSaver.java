package bookkeep.persistance;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Function;
import java.util.stream.Collectors;

import bookkeep.models.Book;
import bookkeep.models.BookBuilder;
import bookkeep.models.OwnedBook;
import bookkeep.models.collections.BookStorage;

public class FileSaver {

	private static final String FILE_PATH = "textlibrary.txt";
	private static final Function<String, OwnedBook> makeBookFromTitle = (s) -> {
		return new BookBuilder().withTitle(s).withAuthorName("GENERIC_AUTHOR").buildOwnedBook();
	};

	public void save(BookStorage library) {
		String contents = library.getAllBooks().stream().map(Book::getTitle).collect(Collectors.joining("\n"));

		try (FileWriter writer = new FileWriter(FILE_PATH)) {
			writer.write(contents);

		} catch (FileNotFoundException e) {
			System.err.println("Filepath not found");
		} catch (IOException e1) {
			System.err.println("I/O Exception");
		}
	}

	public BookStorage load() {
		BookStorage library = new BookStorage();

		try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
			reader.lines().forEach(s -> library.addBook(makeBookFromTitle.apply(s)));

		} catch (FileNotFoundException e) {
			System.err.println("Filepath not found");
		} catch (IOException e1) {
			System.err.println("I/O Exception");
		}

		return library;
	}

	public static void main(String[] args) {
		FileSaver saver = new FileSaver();
		BookStorage storage = saver.load();
		System.out.println(storage);

		storage.addBook(makeBookFromTitle.apply("xxx"));
		saver.save(storage);

	}
}
