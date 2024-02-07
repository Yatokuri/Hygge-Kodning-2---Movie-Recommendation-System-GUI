package dk.easv.dataaccess;

import java.net.http.HttpRequest;

import dk.easv.entities.Movie;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class APIAccessManager  {
    private static final String configFile = "config/config.settings";
    private static String TMDBAPI_KEY, OMDBAPI_KEY;
    private String posterPath;

    public APIAccessManager() throws Exception {
        Properties APIProperties = new Properties();
        APIProperties.load(new FileInputStream((configFile)));
        TMDBAPI_KEY = (APIProperties.getProperty("TMDBAPI"));
    }

    public void findInformationForMovie(Movie movie) throws Exception {
        //filmTitle = filmTitle.substring(filmTitle.indexOf("org/") + 4, filmTitle.indexOf("-", filmTitle.indexOf("org/") + 4));

        String originalText = movie.getTitle();
        String searchTitle = originalText.replace(" ", "%20");


        // Setup for the TMDB API to get the credits and the actual movie information
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/search/movie?api_key=" + TMDBAPI_KEY + "&query=" + searchTitle))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());


        // Inserts the results from the HttpRequests into Json objects
        JSONObject json = new JSONObject(response.body());
        JSONArray results = json.getJSONArray("results");
       // System.out.println(json);


        if (!results.isEmpty()) { // Check if there are any movie entries
            JSONObject firstMovie = results.getJSONObject(0); // Get the first movie object

        if (firstMovie.has("poster_path")) { // Grabbing the Poster Path from the json and update movie poster path
            movie.setPosterPath("https://image.tmdb.org/t/p/w400/" + firstMovie.getString("poster_path"));
        }
        }
        else {
            movie.setPosterPath("https://ih1.redbubble.net/image.1893341687.8294/fposter,small,wall_texture,product,750x1000.jpg");
        }
        //TODO In the movie list there is also tv series so if not a movie check tv before missing image
    }
}