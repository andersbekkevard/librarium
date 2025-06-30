package bookkeep.app;

import bookkeep.models.collections.BookStorage;
import bookkeep.ui.LibraryMenu;

public class Main {

	public static void main(String[] args) {
		BookStorage library = new BookStorage();
		library.makeDummyLibrary();
		LibraryMenu menu = new LibraryMenu(library);
		menu.start();
	}
}