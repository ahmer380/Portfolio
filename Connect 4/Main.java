import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.util.Objects;

/**
 * Write a description of JavaFX class MainController here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Main extends Application
{ 
    /**
     * The start method is the main entry point for every JavaFX application. 
     * It is called after the init() method has returned and after 
     * the system is ready for the application to begin running.
     *
     * @param  stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage)throws Exception
    {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("MainMenuPage.fxml")));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Connect 4 Game");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}