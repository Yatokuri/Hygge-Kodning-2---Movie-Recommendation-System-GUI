package dk.easv.presentation.controller;

import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class NetfliksD implements Initializable {
    private static final int ROWS = 5;
    private static final int COLUMNS = 5;
    private static final int TOTAL_IMAGES = 20;

    private Map<Integer, Integer> rowIndices = new HashMap<>();
    private Map<Integer, Integer> highestRowIndices = new HashMap<>();
    private Stage primaryStage;
    private Scene scene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize rowIndices with 0 for each row
        for (int i = 0; i < ROWS; i++) {
            rowIndices.put(i, 0);
            highestRowIndices.put(i, COLUMNS); // Set to the default value (maximum images in a row)
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void startupNetfliks() {
        GridPane gridPane = createImageGrid();
        scene = new Scene(gridPane, 800, 600);
        primaryStage.setScene(scene);
    }

    private GridPane createImageGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        for (int row = 0; row < ROWS; row++) {
            updateRow(gridPane, row);
        }

        return gridPane;
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
            imageView.setFitWidth(80);
            imageView.setFitHeight(120);

            gridPane.add(imageView, colIndex, row);

            colIndex++;
        }

        // Create Left Arrow Button
        Button leftArrowButton = new Button("←");
        leftArrowButton.setOnAction(e -> handleLeftArrow(gridPane, row));

        // Create Right Arrow Button
        Button rightArrowButton = new Button("→");
        rightArrowButton.setOnAction(e -> handleRightArrow(gridPane, row));

        // Create HBox for the first column
        HBox arrowBoxFirstColumn = new HBox(10, leftArrowButton);
        arrowBoxFirstColumn.setAlignment(Pos.CENTER);

        // Create HBox for the last column
        HBox arrowBoxLastColumn = new HBox(10, rightArrowButton);
        arrowBoxLastColumn.setAlignment(Pos.CENTER);

        // Add the HBoxes to the first and last columns of the row
        gridPane.add(arrowBoxFirstColumn, 0, row);
        gridPane.add(arrowBoxLastColumn, COLUMNS - 1, row);
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
