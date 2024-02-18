package dk.easv.presentation.model;

import dk.easv.entities.*;
import dk.easv.logic.LogicManager;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class AppModel {

    LogicManager logic = new LogicManager();
    // Models of the data in the view
    private final ObservableList<User>  obsUsers = FXCollections.observableArrayList();
    private final ObservableList<Movie> obsTopMovieSeen = FXCollections.observableArrayList();
    private final ObservableList<Movie> obsTopMovieNotSeen = FXCollections.observableArrayList();
    private final ObservableList<UserSimilarity>  obsSimilarUsers = FXCollections.observableArrayList();
    private final ObservableList<TopMovie> obsTopMoviesSimilarUsers = FXCollections.observableArrayList();

    private final SimpleObjectProperty<User> obsLoggedInUser = new SimpleObjectProperty<>();



    public List<Movie> getMoviesFromIndex(String ObLName, int startIndex) throws Exception {
        List<Movie> listObLName = FXCollections.observableArrayList();

        if (ObLName.equals("obsTopMoviesSimilarUsers")) { // Here we need to take the movie instance inside the TopMovie entities
            ObservableList<TopMovie> topMovies = (ObservableList<TopMovie>) getClass().getDeclaredField(ObLName).get(this);
            listObLName = topMovies.stream() // Iterate through the sublist of listObLName
                    .map(TopMovie::getMovie)
                    .collect(Collectors.toList());
        }

        if (listObLName.isEmpty())    {
            listObLName = (ObservableList<Movie>) getClass().getDeclaredField(ObLName).get(this);
        }
        // Use reflection to get the ObservableList<Movie> object by its name Test way
        int endIndex = Math.min(startIndex + 20, listObLName.size()); //We always load 20 picture ahead

        // Create a list to hold CompletableFuture instances
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = startIndex; i < endIndex; i++) {
            // Create a CompletableFuture for each movie and add it to the list, so it can run faster
            int finalI = i;
            List<Movie> finalListObLName = listObLName;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    logic.giveMoviePosterPath(finalListObLName.get(finalI));
                } catch (Exception e) {
                    e.printStackTrace(); // Handle exceptions appropriately
                }
            });
            futures.add(future);
        }

        // Wait for all CompletableFuture instances to complete
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        allOf.join();

        // Return the updated list of movies
        return new ArrayList<>(listObLName.subList(startIndex, endIndex));
    }


    public void loadUsers(){
        obsUsers.clear();
        obsUsers.addAll(logic.getAllUsers());
    }

    public void loadData(User user) {
        obsTopMovieSeen.clear();
        obsTopMovieSeen.addAll(logic.getTopAverageRatedMovies(user));

        obsTopMovieNotSeen.clear();
        obsTopMovieNotSeen.addAll(logic.getTopAverageRatedMoviesUserDidNotSee(user));

        obsSimilarUsers.clear();
        obsSimilarUsers.addAll(logic.getTopSimilarUsers(user));

        obsTopMoviesSimilarUsers.clear();
        obsTopMoviesSimilarUsers.addAll(logic.getTopMoviesFromSimilarPeople(user));
    }

    public ObservableList<User> getObsUsers() {
        return obsUsers;
    }

    public ObservableList<Movie> getObsTopMovieSeen() {
        return obsTopMovieSeen;
    }

    public ObservableList<Movie> getObsTopMovieNotSeen() {
        return obsTopMovieNotSeen;
    }

    public ObservableList<UserSimilarity> getObsSimilarUsers() {
        return obsSimilarUsers;
    }

    public ObservableList<TopMovie> getObsTopMoviesSimilarUsers() {
        return obsTopMoviesSimilarUsers;
    }

    public User getObsLoggedInUser() {
        return obsLoggedInUser.get();
    }

    public SimpleObjectProperty<User> obsLoggedInUserProperty() {
        return obsLoggedInUser;
    }

    public void setObsLoggedInUser(User obsLoggedInUser) {
        this.obsLoggedInUser.set(obsLoggedInUser);
    }

    public boolean loginUserFromUsername(String userName) {
        User u = logic.getUser(userName);
        obsLoggedInUser.set(u);
        if (u==null)
            return false;
        else
            return true;
    }
}
