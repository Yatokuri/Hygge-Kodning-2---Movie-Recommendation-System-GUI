package dk.easv.presentation.controller;

import dk.easv.entities.Movie;
import dk.easv.presentation.model.AppModel;
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


    private final Map<Integer, ImageView> imageMovieList = new HashMap<>(); //Here we save all image with a unique key
    private final Map<Integer, List<Integer>> imageMovieListIdentifier = new HashMap<>(); //Here we save all image key to a specific list of picture (key)
    private final Map<Integer, Integer> imageInMovieListCount = new HashMap<>(); //Here we save the count of each row of img

    ArrayList<String> movieListTest = new ArrayList<>();
    private ArrayList<String> movieDisplayLabels = new ArrayList<>();
;
    private Stage primaryStage;
    private Scene scene;
    @FXML
    private VBox movieDisplay, movieDisplayHelper;
    @FXML
    private MenuButton userName;

    private AppModel appModel;

    public NetfliksD(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setModel(AppModel appModel) {
        this.appModel = appModel;
    }

    // Method to add a value to the list associated with the given key
    public void addToMultiKeyMap(int key, int value) {
        // If the key is not present, create a new list and add it to the map
        imageMovieListIdentifier.putIfAbsent(key, new ArrayList<>());
        // Add the value to the list associated with the key
        imageMovieListIdentifier.get(key).add(value);
    }

    // Method to get the list of values associated with the given key
    public List<Integer> getValuesForKey(int key) {
        // Return the list associated with the key, or an empty list if key not found
        return imageMovieListIdentifier.getOrDefault(key, new ArrayList<>());
    }


    public void startupNetfliks() {
        appModel.loadUsers();
        appModel.loadData(appModel.getObsLoggedInUser()); //We load the data from the user log in

        movieListTest.add("obsTopMovieNotSeen");
        movieListTest.add("obsTopMovieSeen");
        movieListTest.add("obsTopMovieNotSeen");

        for (int r = 0; r < ROWS; r++) { //So many rows we want with movie
            List<Movie> selectedMovies;
            try {
                selectedMovies = appModel.getMoviesFromIndex(movieListTest.get(r), 20); // HEAVY TASK
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            int i = 0;
            for (Movie m : selectedMovies) {
                String posterPath = m.getPosterPath();

                if (posterPath != null && !posterPath.isEmpty()) {
                    try {
                        InputStream stream = new URL(posterPath).openStream();
                        Image image = new Image(stream); // Load the image from the input stream
                        stream.close();

                        // Check if the Image object is loaded successfully
                        if (image.isError()) {
                            System.out.println("Error loading image: " + image.getException().getMessage());
                        } else {
                           // System.out.println("Image loaded successfully.");
                            // Create ImageView
                            ImageView imageView = new ImageView(image); // Create an ImageView from the Image object
                            imageView.setPreserveRatio(true); // Preserve the image's aspect ratio

                            // Add the ImageView to the imageMovieList map
                            imageMovieList.put(imageMovieList.size(), imageView);

                            imageInMovieListCount.put(r, i);

                            addToMultiKeyMap(r, imageMovieList.size());

                            highestRowIndices.put(i, calculateImagesToShow()); // Set to the default value (maximum images in a row)
                            i++;
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            System.out.println("Row" + r +  "done");
            }




        // Initialize rowIndices with 0 for each row
        for (int i = 0; i < ROWS; i++) {
            rowIndices.put(i, 0);
            highestRowIndices.put(i, calculateImagesToShow()); // Set to the default value (maximum images in a row)
        }
        COLUMNS = calculateImagesToShow();
        userName.setText(appModel.getObsLoggedInUser().getName());
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
        COLUMNS = calculateImagesToShow();
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
            updateRow(gridPane, row, 0, COLUMNS);



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



    private void updateRow(GridPane gridPane, int row, int startIndex, int numImages) {
        List<Integer> keys = getValuesForKey(row); // Assuming getValuesForKey(0) returns the list of keys for the current row
        int endIndex = Math.min(startIndex + numImages, keys.size());
        int colIndex = 0;
        gridPane.getChildren().clear();

        for (int i = startIndex; i < endIndex; i++) {
            Integer key = keys.get(i);

            ImageView imageView = imageMovieList.get(key); // Retrieve ImageView using the key
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
        updateRow(gridPane, row, 0, COLUMNS);
    }

    private void handleRightArrow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);

        if ((currentIndex + 1) * COLUMNS < TOTAL_IMAGES) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        rowIndices.put(row, currentIndex);
        updateRow(gridPane, row, 0, COLUMNS);

        //TODO Something there get more picture we can show in next click always a click ahead
    }
}
