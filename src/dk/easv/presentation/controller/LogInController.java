package dk.easv.presentation.controller;

import dk.easv.entities.User;
import dk.easv.presentation.model.AppModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.w3c.dom.stylesheets.LinkStyle;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;

public class LogInController implements Initializable {
    @FXML private PasswordField passwordField;
    @FXML private TextField userId;
    private AppModel model;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        model = new AppModel();
    }

    public void logIn(ActionEvent actionEvent) {
        model.loadUsers();
        model.loginUserFromUsername(userId.getText());
        String password;

        try {
            List<String> userLines = Files.readAllLines(Path.of("data/users.txt"));
            if (model.getObsLoggedInUser()!= null){
                for (String s: userLines ) {
                    System.out.println(s);
                }
                //String currentUser = userLines.get()
                //int userId = model.getObsLoggedInUser().getId();
                //int userIndex = userLines.indexOf();
                //String currentUser = userLines.get(userIndex);

                //String[] user = currentUser.split(",");
                //System.out.println(user[2]);
                //password = user[2];
                //System.out.println(password);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(model.getObsLoggedInUser()!=null)
        {
            //if (passwordField == password || password == null)
            try {
            //Main
            //FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/presentation/view/App.fxml"));
            //Test D
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/NetfliksD.fxml"));
            Stage currentStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            Parent root = loader.load();
            // Set the scene in the existing stage
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Movie Recommendation System 0.01 Beta");
            currentStage.setMinWidth(600);
            currentStage.setMinHeight(320);

            //Fullscreen when opening
            Screen screen = Screen.getPrimary();
            currentStage.setX(screen.getVisualBounds().getMinX());
            currentStage.setY(screen.getVisualBounds().getMinY());
            currentStage.setWidth(screen.getBounds().getWidth());
            currentStage.setHeight(screen.getBounds().getHeight());
            currentStage.setMaximized(true);

            //Main
            //AppController controller = loader.getController();
            //controller.setModel(model);

            //Test D
            NetfliksD controller = loader.getController();
            controller.setPrimaryStage(currentStage);
            controller.startupNetfliks();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Could not load App.fxml");
            alert.showAndWait();
        }
        }
        else{
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wrong username or password");
            alert.showAndWait();
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

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
