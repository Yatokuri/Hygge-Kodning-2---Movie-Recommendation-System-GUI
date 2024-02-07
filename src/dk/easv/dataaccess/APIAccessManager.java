package dk.easv.dataaccess;

import java.net.http.HttpRequest;

import javafx.scene.control.Alert;
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

    private void findInformationForMovie(String filmTitle) throws Exception {
            //filmTitle = filmTitle.substring(filmTitle.indexOf("org/") + 4, filmTitle.indexOf("-", filmTitle.indexOf("org/") + 4));

            // Setup for the TMDB API to get the credits and the actual movie information
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.themoviedb.org/3/search/movie?api_key=" + TMDBAPI_KEY + "&query=" + filmTitle))
                    .header("accept", "application/json")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            // Inserts the results from the HttpRequests into Json objects
            JSONObject json = new JSONObject(response.body());

            if (json.has("original_title")) { //We look for Genre in the json cause that what we need know in comma separated string
                //Movie
                // Grabbing the Poster Path from the json results and saving it for insertion into movie object
                posterPath = "https://image.tmdb.org/t/p/w400/" + json.getString("poster_path");
            }
    }
}