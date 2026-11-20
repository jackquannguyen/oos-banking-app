package ui;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class FxApplication extends Application {
    /**
     * JavaFX controller works based on MVC. JavaFX MVC can be achieved by FXML.
     * FXML is an XML based language used to develop the graphical user interfaces
     * @param primaryStage
     * @throws IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Bank Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
