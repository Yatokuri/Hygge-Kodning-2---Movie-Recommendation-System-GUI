package dk.easv;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/view/LogIn.fxml"));
            primaryStage.setTitle("Movie Recommendation System 0.02 Beta");
            primaryStage.getIcons().add(new Image("/Icons/mainIcon.png"));
            //primaryStage.setFullScreen(true);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
