package dk.easv.presentation.controller;

import dk.easv.presentation.model.AppModel;
import dk.easv.entities.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class NetfliksD implements Initializable {
    private static final int ROWS = 3;
    private static int COLUMNS = 7;
    private static final int TOTAL_IMAGES = 20;

    private static final double MOVIE_IMG_HEIGHT = 220.0;
    private static final double MOVIE_IMG_WIDTH = 147.0;

    private ImageView Image;

    private final Map<Integer, Integer> rowIndices = new HashMap<>();
    private final Map<Integer, Integer> highestRowIndices = new HashMap<>();
    // HashMap to store ImageViews for reuse
    private final Map<Integer, ImageView> imageViewMap = new HashMap<>();

    private final Map<Integer, ImageView> imageInMovielist = new HashMap<>();
    private final Map<Integer, Integer> imageInMovielistCount = new HashMap<>();

    private ArrayList<String> movieDisplayLabels = new ArrayList<>();
    private Stage primaryStage;
    private Scene scene;
    @FXML
    private VBox movieDisplay, movieDisplayHelper;
    @FXML
    private MenuButton userName;

    private AppModel appModel;

    public NetfliksD(){
        AppModel appModel = new AppModel();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void startupNetfliks() {
        AppModel appModel = new AppModel();
        List<String> selectedMovies = new ArrayList<>();
        selectedMovies = appModel.getMoviesFromIndex(20);
        System.out.println("_______________Selected movies________________");
        for (String movie : selectedMovies) {
            System.out.println(movie);
        }


     //   AppModel.getObsTopMovieSeen()

        //imageInMovielist.add()

      /*  for (int i = 0; i < ROWS; i++) {
            movie = AppModel.getObsTopMovieSeen();
            String posterPath = movie.getPosterPath();
            if (posterPath != null && !posterPath.isEmpty()) {
                InputStream stream;
                try {
                    stream = new URL(posterPath).openStream();
                    ImageView image = new ImageView(String.valueOf(stream));
                    stream.close();
                    imageInMovielist.put(i, image);
                    highestRowIndices.put(i, calculateImagesToShow()); // Set to the default value (maximum images in a row)
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            }
        */




        // Initialize rowIndices with 0 for each row
        for (int i = 0; i < ROWS; i++) {
            rowIndices.put(i, 0);
            highestRowIndices.put(i, calculateImagesToShow()); // Set to the default value (maximum images in a row)
        }
        COLUMNS = calculateImagesToShow();
       // userName.setText();
        setLabelName();
        createImageGrid();
        listenersWindowsSize();
    }

    private void listenersWindowsSize()  { // Add listener to scene's width and height properties
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
            updateMovieLists();
        });

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> {
            updateMovieLists();
        });
    }

    private int calculateImagesToShow() {
        double imagesPerRow = Math.ceil(primaryStage.getWidth() / MOVIE_IMG_WIDTH);
        int imagesPerRowInt = (int) imagesPerRow; // Convert imagesPerRow to integer

        // Update highestRowIndices for all rows
        for (int i = 0; i < ROWS; i++) {
            // highestRowIndices.put(i, imagesPerRowInt);
        }
        return imagesPerRowInt - 1;
    }

    private void updateMovieLists() {
        System.out.println("We want see " + calculateImagesToShow() + " picture(s)");
    }

    Button rightArrowButton, leftArrowButton;

    private void createImageGrid() {
        for (int row = 0; row < ROWS; row++) {
            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(0);
            gridPane.setPadding(new Insets(0, 0, 10, 5));

            // Create HBox to hold arrow buttons
            HBox arrowButtonsHBox = new HBox();
            arrowButtonsHBox.setAlignment(Pos.CENTER);
            VBox.setMargin(arrowButtonsHBox, new Insets(-MOVIE_IMG_HEIGHT-+5, 10,0 , 10));



            // Create Left Arrow Button
            leftArrowButton = new Button("←");
            leftArrowButton.setId("arrowButton");
            HBox.setHgrow(leftArrowButton, Priority.NEVER);

            // Create Right Arrow Button
            rightArrowButton = new Button("→");
            rightArrowButton.setId("arrowButton");
            HBox.setHgrow(rightArrowButton, Priority.NEVER);
            updateRow(gridPane, row);



            // Create Left Arrow Button OnAction
            int finalRow = row;
            leftArrowButton.setOnAction(e -> handleLeftArrow(gridPane, finalRow));
            leftArrowButton.setId("arrowButton");

            // Create Right Arrow Button OnAction
            int finalRow1 = row;
            rightArrowButton.setOnAction(e -> handleRightArrow(gridPane, finalRow1));
            rightArrowButton.setId("arrowButton");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            //TODO The right arrows is not always visible it depend of last grid pane!

            // Add arrows buttons to HBox
            arrowButtonsHBox.getChildren().addAll(leftArrowButton, spacer, rightArrowButton);




            VBox GridPaneArrowTitleVBox = new VBox();
            Label movieTabTitle = new Label(movieDisplayLabels.get(row));
            movieTabTitle.setId("movieTabTitle");



            VBox.setMargin(gridPane, new Insets(0, 0, MOVIE_IMG_HEIGHT/2.4, 0));
            movieDisplayHelper.setSpacing(MOVIE_IMG_HEIGHT/2.4);

            GridPaneArrowTitleVBox.getChildren().addAll(movieTabTitle, gridPane, arrowButtonsHBox);
            movieDisplayHelper.getChildren().add(GridPaneArrowTitleVBox);
        }
    }

    private void setLabelName() {
        movieDisplayLabels.add("Movies seen");
        movieDisplayLabels.add("Recommended movies");
        movieDisplayLabels.add("Movies not seen");
    }



    private void updateRow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);
        int startIndex = currentIndex * COLUMNS;
        int totalImagesInRow = highestRowIndices.get(row); // Use the maximum images in a row

        int endIndex = Math.min(startIndex + totalImagesInRow, TOTAL_IMAGES);

        int colIndex = 0;

        for (int i = startIndex; i < endIndex; i++) {
            Image image = new Image(getClass().getResourceAsStream("/MovieIMG/" + (i + 1) + ".jpg"));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(MOVIE_IMG_WIDTH);
            imageView.setFitHeight(MOVIE_IMG_HEIGHT);
            gridPane.add(imageView, colIndex, row);
            colIndex++;
        }

    }

    private void handleLeftArrow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);
        int totalImagesInRow = highestRowIndices.get(row);
        if (currentIndex == 0)  {
            currentIndex--;
        }

        // Loop through the images continuously
        currentIndex = (currentIndex - 1 + totalImagesInRow) % totalImagesInRow;

        rowIndices.put(row, currentIndex);
        updateRow(gridPane, row);
    }

    private void handleRightArrow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);

        if ((currentIndex + 1) * COLUMNS < TOTAL_IMAGES) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        rowIndices.put(row, currentIndex);
        updateRow(gridPane, row);
    }
}
