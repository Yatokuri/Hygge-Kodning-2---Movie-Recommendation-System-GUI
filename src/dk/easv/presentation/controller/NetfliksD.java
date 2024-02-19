package dk.easv.presentation.controller;

import dk.easv.entities.Movie;
import dk.easv.presentation.model.AppModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class NetfliksD implements Initializable {
    private static final int ROWS = 3;
    private static int currentMovieShowingCount = 5;
    private final static int DEFAULT_PRE_LOADED_IMG = 0;

    private static final double MOVIE_IMG_HEIGHT = 220.0;
    private static final double MOVIE_IMG_WIDTH = 147.0;


    private final Map<Integer, Integer> rowIndices = new HashMap<>(); //This hold an index value used for the left and right click function
    private final Map<Integer, ImageView> imageMovieList = new HashMap<>(); //Here we save all imageview with a unique key
    private final Map<Integer, List<Integer>> imageMovieListIdentifier = new HashMap<>(); //Here we save all image key to a specific list of picture (key)
    private final Map<Integer, Integer> imageInMovieListCount = new HashMap<>(); //Here we save the count of each row of img
    private final Map<Integer, Integer> imageInMovieListCurrentLastIndex = new HashMap<>(); //Here we save the last index of current showed movies
    private final Map<Integer, Integer> imageInMovieListCurrentFirstIndex = new HashMap<>(); //Here we save the first index of current showed movies

    ArrayList<GridPane> gridPaneList = new ArrayList<>();

    ArrayList<Double> previousSpacerValue = new ArrayList<>(); // This save the value with the space we need between arrows
    ArrayList<Double> previousSpacerLockValue = new ArrayList<>(); // This save how many picture there is 100% showed
    ArrayList<String> movieListTest = new ArrayList<>();

    private ArrayList<String> movieDisplayLabels = new ArrayList<>();
    private Stage primaryStage, loginStage;
    @FXML
    private VBox mainDisplay, movieDisplayHelper, movieDisplayMovie, movieDisplayList, movieDisplayDonate;
    @FXML
    private MenuButton userName;

    private AppModel appModel;

    private LogInController loginController;

    @FXML
    private MenuItem menuItemLogout;
    @FXML
    private HBox navBar;

    public NetfliksD(){

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logoutUserFunction();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
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

    private void setLabelNameAndList() {
        movieDisplayLabels.add("Movies seen");
        movieDisplayLabels.add("Recommended movies");
        movieDisplayLabels.add("Movies not seen");

        movieListTest.add("obsTopMovieNotSeen");
        movieListTest.add("obsTopMovieSeen");
        movieListTest.add("obsTopMoviesSimilarUsers");

    }

    Image loadingImage = new Image(getClass().getResourceAsStream("/Icons/loading.gif"));
    public void startupNetfliksLoadingScreen() {
        // Load the loading GIF
        ImageView loadingImageView = new ImageView(loadingImage);

        HBox loadingImageHBox = new HBox();
        loadingImageHBox.setAlignment(Pos.CENTER);
        loadingImageHBox.setPrefHeight((primaryStage.getHeight()- navBar.getHeight()));
        loadingImageHBox.getChildren().add(loadingImageView);

        // Set the size of the loading image as needed
        loadingImageView.setFitWidth(200);
        loadingImageView.setFitHeight(200);
        // Add the loading image to the movieDisplayHelper
        movieDisplayHelper.getChildren().add(loadingImageHBox);
        movieDisplayList.setVisible(false);
    }

    public void createDonateSite() {
        // Load the loading GIF
        ImageView loadingImageView = new ImageView(loadingImage);
        HBox loadingImageHBox = new HBox();
        loadingImageHBox.setAlignment(Pos.CENTER);
        loadingImageHBox.setPrefHeight((primaryStage.getHeight()- navBar.getHeight()));
        loadingImageHBox.getChildren().add(loadingImageView);
        // Set the size of the loading image as needed
        loadingImageView.setFitWidth(200);
        loadingImageView.setFitHeight(200);
        // Add the loading image to the movieDisplayHelper
        movieDisplayDonate.getChildren().add(loadingImageHBox);
        movieDisplayDonate.setVisible(false);
    }

    public void createListSite() {
        // Load the loading GIF
        ImageView loadingImageView = new ImageView(loadingImage);
        HBox loadingImageHBox = new HBox();
        loadingImageHBox.setAlignment(Pos.CENTER);
        loadingImageHBox.setPrefHeight((primaryStage.getHeight()- navBar.getHeight()));
        loadingImageHBox.getChildren().add(loadingImageView);
        // Set the size of the loading image as needed
        loadingImageView.setFitWidth(200);
        loadingImageView.setFitHeight(200);
        // Add the loading image to the movieDisplayHelper
        movieDisplayList.getChildren().add(loadingImageHBox);
    }

    public void startupNetfliks() {
        setLabelNameAndList(); //Temp Way
        appModel.loadUsers();
        appModel.loadData(appModel.getObsLoggedInUser()); //We load the data from the user log in
        userName.setText(appModel.getObsLoggedInUser().getName());

        // Asynchronously load movies
        CompletableFuture<Void> loadMoviesFuture = CompletableFuture.runAsync(() -> {
            for (int r = 0; r < ROWS; r++) {
                loadMoreMovie(r, DEFAULT_PRE_LOADED_IMG);
                rowIndices.put(r, 0); // Initialize rowIndices with 0 for each row
            }
        });

        // After movies are loaded, update UI with picture
        loadMoviesFuture.thenRun(() -> {
            Platform.runLater(() -> {
                movieDisplayHelper.getChildren().clear();
                currentMovieShowingCount = calculateImagesToShow();
                createImageGrid();
                listenersWindowsSize();
                createListSite();
                createDonateSite();
            });
        });
    }

    private void loadMoreMovie(Integer movieRowIndex, Integer loadingStartIndex) {

        System.out.println("Dit index: " + movieRowIndex + "Dit startindex" + loadingStartIndex);

        List<Movie> selectedMovies;
            try {
                selectedMovies = appModel.getMoviesFromIndex(movieListTest.get(movieRowIndex), loadingStartIndex);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            int i = loadingStartIndex;
            for (Movie m : selectedMovies) {
                ImageView imageView = getImageViewMovie(m);
                // Add the ImageView to the imageMovieList map
                imageMovieList.put(imageMovieList.size(), imageView);
                // Set the imageInMovieListCount for the current row (r)
                imageInMovieListCount.put(movieRowIndex, i);
                // Add to the multiKeyMap
                addToMultiKeyMap(movieRowIndex, imageMovieList.size()-1);

                i++;
            }
    }

    private static ImageView getImageViewMovie(Movie m) {
        String posterPath = m.getPosterPath();
        ImageView imageView = null;

        if (posterPath == null || posterPath.equals("NoMovieIMGFound") || posterPath.equals("https://image.tmdb.org/t/p/w400/")) {
            imageView = new ImageView("/Icons/NoMovieIMGFound.jpg");
        } else if (!posterPath.isEmpty()) {
            try (InputStream stream = new URL(posterPath).openStream()) {
                Image image = new Image(stream);
                imageView = new ImageView(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // Add event handler to print movie title when clicked
        assert imageView != null;
        imageView.setOnMouseClicked(event -> {
            System.out.println("Clicked on movie: " + m.getTitle());
        });

        return imageView;
    }

    private void logoutUserFunction(){

        menuItemLogout.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LogIn.fxml"));
                    Stage currentStage = (Stage) navBar.getScene().getWindow();
                    Parent root = loader.load();
                    currentStage.getScene().setRoot(root);

                    // Set the scene in the existing stage
                    //currentStage.setScene(new Scene(root));
                    currentStage.setMinWidth(0);
                    currentStage.setMinHeight(0);
                    currentStage.setMaxWidth(454);
                    currentStage.setMaxHeight(256);
                    currentStage.setResizable(false);
                    currentStage.setMaximized(false);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void listenersWindowsSize()  { // Add listener to scene's width and height properties
        primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> updateMovieLists());

        primaryStage.heightProperty().addListener((observable, oldValue, newValue) -> updateMovieLists());

        primaryStage.iconifiedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateMovieLists();
            }
        });

        primaryStage.maximizedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                updateMovieLists();
            }
        });
    }

    private int calculateImagesToShow() { //Helper method to know the number of picture there can be show in the moment
        return (int) Math.ceil(primaryStage.getWidth() / (MOVIE_IMG_WIDTH + 5)) ; // 5 take care of the gap between image
    }


    private void updateMovieLists() {
        currentMovieShowingCount = calculateImagesToShow();
        int columns = calculateImagesToShow(); // Assuming calculateImagesToShow() returns the number of columns

        for (GridPane gridPane : gridPaneList) {
            int numChildren = gridPane.getChildren().size();
            if (columns < numChildren) {
                gridPane.getChildren().remove(columns, numChildren);
                return;
            }
            for (GridPane g : gridPaneList) {
                updateRow(g, gridPaneList.indexOf(g), imageInMovieListCurrentFirstIndex.get(gridPaneList.indexOf(g)), columns);
            }
        }
    }

    Button rightArrowButton, leftArrowButton;

    private void createImageGrid() {
        for (int row = 0; row < ROWS; row++) {
            GridPane gridPane = new GridPane();
            gridPane.setHgap(5);
            gridPane.setVgap(0);
            gridPane.setPadding(new Insets(0, 0, 10, 5));
            gridPaneList.add(gridPane);

            // Create HBox to hold arrow buttons
            HBox arrowButtonsHBox = new HBox();
            arrowButtonsHBox.setAlignment(Pos.TOP_LEFT);
            VBox.setMargin(arrowButtonsHBox, new Insets(-MOVIE_IMG_HEIGHT - 5, 10, 0, 10));

            imageInMovieListCurrentLastIndex.put(row, 0);
            previousSpacerLockValue.add(row, 0.0);
            // Create Left Arrow Button
            leftArrowButton = new Button("←");
            leftArrowButton.setId("arrowButton"); // To CSS
            HBox.setHgrow(leftArrowButton, Priority.NEVER);

            // Create Right Arrow Button
            rightArrowButton = new Button("→");
            rightArrowButton.setId("arrowButton"); // To CSS
            HBox.setHgrow(rightArrowButton, Priority.NEVER);
            updateRow(gridPane, row, 0, currentMovieShowingCount);

            // Create Left Arrow Button OnAction
            int finalRow = row;
            leftArrowButton.setOnAction(e -> handleLeftArrow(gridPane, finalRow));

            // Create Right Arrow Button OnAction
            int finalRow1 = row;
            rightArrowButton.setOnAction(e -> handleRightArrow(gridPane, finalRow1));

            Region spacer = new Region();
            // Add arrows buttons to HBox
            arrowButtonsHBox.getChildren().addAll(leftArrowButton, spacer, rightArrowButton);

            VBox GridPaneArrowTitleVBox = new VBox();
            Label movieTabTitle = new Label(movieDisplayLabels.get(row));
            movieTabTitle.setId("movieTabTitle"); // To CSS

            VBox.setMargin(gridPane, new Insets(0, 0, MOVIE_IMG_HEIGHT/2.4, 0));
            movieDisplayHelper.setSpacing(MOVIE_IMG_HEIGHT/2.4);

            // gridPane.setStyle("-fx-background-color: #35af0e");
            // movieTabTitle.setStyle("-fx-background-color: #2a2323");
            // arrowButtonsHBox.setStyle("-fx-background-color: #f30707");


            GridPaneArrowTitleVBox.getChildren().addAll(movieTabTitle, gridPane, arrowButtonsHBox);
            movieDisplayHelper.getChildren().add(GridPaneArrowTitleVBox);

            previousSpacerValue.add(row, 0.0);

            AtomicBoolean ifBlockExecuted = new AtomicBoolean(true); // Flag to track if the condition block has been executed

            // Listener to print the gridPanes last column x-coordinate and window size
            primaryStage.widthProperty().addListener((observable, oldValue, newValue) -> {
                Platform.runLater(() -> {

                double value = previousSpacerValue.get(gridPaneList.indexOf(gridPane));
                double checkValue = (currentMovieShowingCount);
                double lastColumnX = 0.0;

                if (!gridPane.getChildren().isEmpty()) {
                    Node lastChild = gridPane.getChildren().get(gridPane.getChildren().size() - 1);
                    lastColumnX = GridPane.getColumnIndex(lastChild) * (lastChild.getBoundsInParent().getWidth() + gridPane.getHgap());
                }

                if (previousSpacerLockValue.get(gridPaneList.indexOf(gridPane)) == checkValue || previousSpacerLockValue.get(gridPaneList.indexOf(gridPane)) == (checkValue + 1)) {
                    value = primaryStage.getWidth() - ((currentMovieShowingCount - 1) * (MOVIE_IMG_WIDTH + 5));
                    previousSpacerValue.set(gridPaneList.indexOf(gridPane), value);
                    ifBlockExecuted.set(true); // Update the flag
                } else if (ifBlockExecuted.get()) { // Check if the condition block has been executed before
                    value = (previousSpacerValue.get(gridPaneList.indexOf(gridPane))+(5));
                    previousSpacerValue.set(gridPaneList.indexOf(gridPane), value);
                    ifBlockExecuted.set(false); // Reset the flag
                }

                spacer.setPrefWidth(lastColumnX-90+value);
                arrowButtonsHBox.setMaxSize(lastColumnX+value,Region.USE_COMPUTED_SIZE);
                gridPane.setMaxSize(lastColumnX,Region.USE_COMPUTED_SIZE);
                });
            });
        }

        // Update the right arrow from start
        updateRightArrow();
    }

    private void updateRightArrow() { // Trick our "Listener" to be detected
        primaryStage.setWidth(primaryStage.getWidth()+1);
        primaryStage.setWidth(primaryStage.getWidth()-1);
    }

    private void updateRow(GridPane gridPane, int row, int startIndex, int numImages) {
        if (startIndex < 0) {   // Handle special case where startindex = 0
            startIndex = imageInMovieListCount.get(row)-numImages-1; //Start from the end again
        }

        if (startIndex > imageInMovieListCount.get(row)) { //End of last start from start //
            startIndex = 0;
        }

        List<Integer> keys = getValuesForKey(row); // Assuming getValuesForKey(0) returns the list of keys for the current row
        int endIndex = Math.min(startIndex + numImages, keys.size());
        int colIndex = 0;
        gridPane.getChildren().clear();

        // Update the First and Last index in the hashmap
        imageInMovieListCurrentFirstIndex.put(row, startIndex);
        imageInMovieListCurrentLastIndex.put(row, startIndex+numImages);

        if (startIndex + numImages > imageInMovieListCount.get(row)-5) {
            loadMoreMovie(row, imageInMovieListCount.get(row) + 1);
        }

        for (int i = startIndex; i < endIndex; i++) {
            Integer key = keys.get(i);
            ImageView imageView = imageMovieList.get(key); // Retrieve ImageView using the key
            if (imageView == null)    {imageView = new ImageView("/Icons/NoMovieIMGFound.jpg");} //Handle special case where imageView is null
            imageView.setFitWidth(MOVIE_IMG_WIDTH);
            imageView.setFitHeight(MOVIE_IMG_HEIGHT);
            gridPane.add(imageView, colIndex, row);
            colIndex++;
        }

        previousSpacerLockValue.set(row, (double) (endIndex-startIndex)); // Update lock value
    }

    private void handleLeftArrow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);
        int totalImagesInRow = currentMovieShowingCount;

        if (currentIndex == 0)  {
            currentIndex--;
        }
        int startValue = imageInMovieListCurrentFirstIndex.get(row);
        // Loop through the images continuously
        currentIndex = (currentIndex - 1 + totalImagesInRow) % totalImagesInRow;

        rowIndices.put(row, currentIndex);


        if (startValue == 0)    { // When user want go left when there are showing first picture on the list
            updateRow(gridPane, row,  imageInMovieListCount.get(row)-currentMovieShowingCount+1, currentMovieShowingCount);
        } else if (startValue < currentMovieShowingCount)   { // When user want go left but the system cannot show the right only new movie it must start from 0 and show some dupe
            updateRow(gridPane, row, 0, currentMovieShowingCount);
        } else {
            updateRow(gridPane, row, startValue - currentMovieShowingCount, currentMovieShowingCount);
        }
        updateRightArrow();
    }

    private void handleRightArrow(GridPane gridPane, int row) {
        int currentIndex = rowIndices.get(row);

        if ((currentIndex + 1) * currentMovieShowingCount < calculateImagesToShow()) {
            currentIndex++;
        } else {
            currentIndex = 0;
        }
        rowIndices.put(row, currentIndex);
        updateRow(gridPane, row, imageInMovieListCurrentLastIndex.get(row)-1, currentMovieShowingCount);

        previousSpacerValue.set(gridPaneList.indexOf(gridPane), gridPane.getWidth() - ((currentMovieShowingCount - 1) * (MOVIE_IMG_WIDTH + 5))); // The same as createImageGrid() end
        updateRightArrow();
    }


    @FXML
    private void onDonateBtn(ActionEvent actionEvent) {
        movieDisplayHelper.setVisible(false);
        movieDisplayList.setVisible(false);
        movieDisplayDonate.setVisible(true);
    }

    @FXML
    private void onMyListBtn(ActionEvent actionEvent) {
        movieDisplayHelper.setVisible(false);
        movieDisplayDonate.setVisible(false);
        movieDisplayList.setVisible(true);
    }

    @FXML
    private void onHomeBtn(ActionEvent actionEvent) {
        movieDisplayHelper.setVisible(true);
        movieDisplayDonate.setVisible(false);
        movieDisplayList.setVisible(false);
    }

    @FXML
    private void onLogoBtn(MouseEvent mouseEvent) {
        movieDisplayHelper.setVisible(true);
        movieDisplayDonate.setVisible(false);
        movieDisplayList.setVisible(false);
    }
}
