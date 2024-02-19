package dk.easv.presentation.controller;

import dk.easv.presentation.model.AppModel;
import dk.easv.presentation.model.DisplayErrorModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ResourceBundle;

public class LogInController implements Initializable {
    @FXML private PasswordField passwordField;
    @FXML private TextField userId, passwordFieldPlain;
    private AppModel model;
    private DisplayErrorModel displayErrorModel;
    private Stage loginStage;
    private static final Image EyeOnIcon = new Image ("/Icons/EyeOn.png");
    private static final Image EyeOffIcon = new Image ("/Icons/EyeOff.png");
    @FXML
    private ImageView togglePasswordImg;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new AppModel();
        displayErrorModel = new DisplayErrorModel();
        togglePasswordImg.setImage(EyeOffIcon);
    }

    public void logIn(ActionEvent actionEvent) {
        model.loadUsers();
        model.loginUserFromUsername(userId.getText());
        if(model.getObsLoggedInUser()!=null)
        {
        if (passwordField.getText().startsWith("1"))  {
            displayErrorModel.displayErrorC("Wrong username or password");
            return;
        }
        try {
            //Main
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/presentation/view/App.fxml"));
            //Test D
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NetfliksD.fxml"));
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            currentStage.setTitle("Movie Recommendation System 0.5 Beta");
            Parent root = loader.load();
            // Set the scene in the existing stage
            currentStage.setScene(new Scene(root));

            //Fullscreen when opening
            Screen screen = Screen.getPrimary();
            currentStage.setMinWidth(600);
            currentStage.setMinHeight(320);
            currentStage.setMaxWidth(screen.getVisualBounds().getMaxX());
            currentStage.setMaxHeight(screen.getVisualBounds().getMaxY());
            currentStage.setX(screen.getVisualBounds().getMinX());
            currentStage.setY(screen.getVisualBounds().getMinY());
            currentStage.setWidth(screen.getBounds().getWidth());
            currentStage.setHeight(screen.getBounds().getHeight());
            currentStage.setMaximized(true);
            currentStage.setResizable(true);

            //Main
            // AppController controller = loader.getController();
            //controller.setModel(model);

            //Test D
            NetfliksD controller = loader.getController();
            controller.setPrimaryStage(currentStage);
            controller.setLoginStage(this.loginStage);
            controller.startupNetfliksLoadingScreen();
            controller.setModel(model);
            controller.startupNetfliks();

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load App.fxml");
            alert.showAndWait();
        }
        }
        else{
            displayErrorModel.displayErrorC("Wrong username or password");
        }
    }

    public void signUp(ActionEvent actionEvent) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("data/users.txt", true))){
            List<String> userLines = Files.readAllLines(Path.of("data/users.txt"));
            int newUserId = Integer.parseInt(userLines.getLast().substring(0,userLines.getLast().indexOf(","))) + 1;
            String newUserName = userId.getText();
            String newUserPassword = passwordField.getText();
            String newUser = newUserId + "," + newUserName + "," + newUserPassword;
            bufferedWriter.newLine();
            bufferedWriter.write(newUser);

            new Alert(Alert.AlertType.CONFIRMATION, "User created successfully");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "User not created successfully");
            throw new RuntimeException(e);
        }
    }
    public void setLoginStage(Stage loginStage) {this.loginStage = loginStage;}

    @FXML
    private void togglePassword() {
        if (togglePasswordImg.getImage() == EyeOnIcon) {
            togglePasswordImg.setImage(EyeOffIcon);
            passwordField.setVisible(false);
            passwordFieldPlain.setText(passwordField.getText());
            passwordFieldPlain.setVisible(true);

        } else {
            togglePasswordImg.setImage(EyeOnIcon);
            passwordField.setVisible(true);
            passwordField.setText(passwordFieldPlain.getText());
            passwordFieldPlain.setVisible(false);
        }
    }
}
