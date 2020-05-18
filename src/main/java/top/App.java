package top;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.*;

import java.io.IOException;

/**
 * JavaFX top.App
 */
public class App extends Application {

    private static Stage mainStage;

    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(this.getClass().getResource("/vector_graphics.fxml"));
        Pane root = loader.load();
        var scene = new Scene(root, 1280, 768);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getMainStage() {
        return mainStage;
    }

}