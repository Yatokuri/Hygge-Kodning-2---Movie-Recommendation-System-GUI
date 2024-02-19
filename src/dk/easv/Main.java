package dk.easv;

import dk.easv.presentation.controller.LogInController;
import dk.easv.presentation.controller.NetfliksD;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage loginStage) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogIn.fxml"));
            Parent root = loader.load();
            loginStage.setTitle("Movie Recommendation Login System 0.5 Beta");
            loginStage.getIcons().add(new Image("/Icons/mainIcon.png"));
            loginStage.setScene(new Scene(root));
            loginStage.setResizable(false);
            loginStage.show();
            LogInController controller = loader.getController();
            controller.setLoginStage(loginStage);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
