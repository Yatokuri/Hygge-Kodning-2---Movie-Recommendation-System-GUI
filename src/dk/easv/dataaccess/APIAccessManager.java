package dk.easv.dataaccess;

import java.net.http.HttpRequest;

import dk.easv.entities.Movie;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Properties;
import java.io.FileInputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

public class APIAccessManager  {
    private static final String configFile = "config/config.settings";
    private static String TMDBAPI_KEY;

    public APIAccessManager() throws Exception {
        Properties APIProperties = new Properties();
        APIProperties.load(new FileInputStream((configFile)));
        TMDBAPI_KEY = (APIProperties.getProperty("TMDBAPI"));
    }

    public void findInformationForMovie(Movie movie) throws Exception {
        String cleanedTitle = cleanMovieTitle(movie.getTitle());
        String searchTitle = cleanedTitle.replace(" ", "%20"); //We add this sign to all space so the api work properly

        // Setup for the TMDB API to get the credits and the actual movie information
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.themoviedb.org/3/search/multi?api_key=" + TMDBAPI_KEY + "&query=" + searchTitle))
                .header("accept", "application/json")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        // Inserts the results from the HttpRequests into Json objects
        JSONObject json = new JSONObject(response.body());
        JSONArray results = json.getJSONArray("results");

        if (!results.isEmpty()) {
            int targetYear = movie.getYear(); // Get the target year from the movie object
            String firstPosterPath = null;

            for (int i = 0; i < results.length(); i++) {
                JSONObject mediaObject = results.getJSONObject(i);
                String mediaType = mediaObject.getString("media_type");

                if (mediaType.equals("movie") || mediaType.equals("tv")) {
                    String posterPath = findPosterPath(mediaObject, mediaType, targetYear);
                    if (posterPath != null) {
                        movie.setPosterPath(posterPath);
                        return; // Exit the method since a poster with correct year is found
                    } else if (firstPosterPath == null) {
                        firstPosterPath = mediaObject.optString("poster_path"); // Store the first poster path encountered
                    }
                }
            }

            // If no poster with correct year is found, set the poster path to the first one encountered
            if (firstPosterPath != null) {
                movie.setPosterPath("https://image.tmdb.org/t/p/w400/" + firstPosterPath);
            } else {
                movie.setPosterPath("NoMovieIMGFound");
            }
        } else {
            movie.setPosterPath("NoMovieIMGFound");
            System.out.println("No movie or TV series entries found for " + movie.getTitle());
        }
    }

    // Helper method to remove everything in the title there prevent us from find a picture
    private String cleanMovieTitle(String originalTitle) {
        // Remove specified suffix from the movie title
        String cleanedTitle = originalTitle.replace(": Collector's Edition: Bonus Material", "");
        cleanedTitle = cleanedTitle.replace(": Special Edition: Bonus Material", "");
        cleanedTitle = cleanedTitle.replace("(Full-screen)", "");
        cleanedTitle = cleanedTitle.replace("(Stage Play)", "");

        // Remove everything after the last colon in the movie title
        int lastIndex = cleanedTitle.lastIndexOf(':');
        return lastIndex != -1 ? cleanedTitle.substring(0, lastIndex) : cleanedTitle;
    }

    //This get used to find the movie poster path
    private String findPosterPath(JSONObject mediaObject, String mediaType, int targetYear) {
        String posterPath = mediaObject.optString("poster_path");
        if (!posterPath.isEmpty()) {
            String date = mediaType.equals("movie") ? mediaObject.optString("release_date", "") :
                    mediaObject.optString("first_air_date", "");

            if (!date.isEmpty()) {
                int releaseYear = Integer.parseInt(date.substring(0, 4)); // Extract the year from the date
                if (releaseYear == targetYear) {
                        return "https://image.tmdb.org/t/p/w400/" + posterPath;
                }
            }
        }
        return null;
    }
}