package bookkeep.fxui;

import bookkeep.models.collections.BookStorage;
import bookkeep.persistance.LibrarySerializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
	private final static String FXML_PATH = "/bookkeep/fxui/MainView.fxml";
	private final static int SCENE_WIDTH = 1000;
	private final static int SCENE_HEIGHT = 700;

	private BookStorage library;
	private LibrarySerializer serializer;

	@Override
	public void start(Stage primaryStage) throws Exception {
		serializer = new LibrarySerializer();

		try {
			library = serializer.load();
			if (library.getAllBooks().isEmpty()) {
				library.makeDummyLibrary();
			}
		} catch (Exception e) {
			library = new BookStorage();
			library.makeDummyLibrary();
		}

		FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_PATH));
		Parent root = loader.load();

		Controller controller = loader.getController();
		controller.initialize(library, serializer);

		primaryStage.setTitle("Librarium - Personal Library Tracker");
		primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
		primaryStage.show();
	}

	@Override
	public void stop() {
		// Save the library state when the application closes
		try {
			serializer.save(library);
		} catch (Exception e) {
			System.err.println("Failed to save library: " + e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
