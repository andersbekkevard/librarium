package bookkeep.persistance;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import bookkeep.models.collections.BookStorage;

public class LibrarySerializer {
	private static final String FILEPATH = "serializedlibrary\\library.ser";
	private static final String TEST_FILEPATH = "serializedlibrary\\testlibrary.ser";

	private boolean isTestSerializer = false;

	public void save(BookStorage library) throws IOException {
		String path = FILEPATH;
		if (isTestSerializer) {
			path = TEST_FILEPATH;
		}

		try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))) {
			out.writeObject(library);
		} catch (Exception e) {
			throw new IOException(path + " was not found");
		}
	}

	public BookStorage load() {
		String path = FILEPATH;
		if (isTestSerializer) {
			path = TEST_FILEPATH;
		}

		try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(path))) {
			return (BookStorage) in.readObject();
		} catch (Exception e) {
			// Dont bother throwing exceptions for missing file paths etc
			return new BookStorage();
		}
	}

	public void makeTestSerializer() {
		isTestSerializer = true;
	}

	public static void main(String[] args) {
		LibrarySerializer serializer = new LibrarySerializer();
		BookStorage library = serializer.load();
		System.out.println(library);
	}

}
