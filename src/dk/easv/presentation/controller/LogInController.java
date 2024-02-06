package dk.easv.presentation.controller;

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
import javafx.stage.Stage;
import org.w3c.dom.stylesheets.LinkStyle;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

        if(model.getObsLoggedInUser()!=null){
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
        System.out.println("Sign-Up initiated");
        try {
            FileReader fileReader = new FileReader("users.txt");
            List<String> lines = new ArrayList<>();
            Scanner sc = new Scanner(fileReader);
            while (sc.hasNextLine()){
                lines.add(sc.nextLine());
            }
            sc.close();
            String latestUser = lines.getLast();
            int newUserId = Integer.parseInt(latestUser.substring(0,latestUser.indexOf(","))) + 1;
            String newUserName = userId.getText();
            String newUserPassword = passwordField.getText();

            FileWriter myWriter = new FileWriter("users.txt", true);
            BufferedWriter bufferedWriter = new BufferedWriter(myWriter);
            String newUser = newUserId + "," + newUserName + "," + newUserPassword;
            bufferedWriter.newLine();
            bufferedWriter.write(newUser);
            System.out.println("New User created" + "\n UserId:" + newUserId +  "\n Username:" + newUserName + "\n Password." + newUserPassword);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
