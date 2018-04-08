package com.example.android.moviesproject;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by martin on 08/03/2018.
 */

public class NetworkUtils {

    // Setting up variables used later
    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String base_url = "https://api.themoviedb.org/3/";
    private static final String api_key = "?api_key=5ac3fb128a86e9d627fed3e9ce026494";
    private static final String img_url = "http://image.tmdb.org/t/p/";
    private static final String img_size_small = "w185";
    private static final String img_size_medium = "w780";

    // Builds movie list URL, needs sort parameter to work
    public static URL buildUrl(String sort) {
        URL url;
        String address = base_url + sort + api_key;
        try {
            url = new URL(address);
        }catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
        Log.v(TAG, "Built URL: " + url);
        return url;
    }

    // Builds poster URL for movie list
    public static URL buildSmallPosterUrl(String path) {
        URL url = null;
        String posterUrl = img_url + img_size_small + path;
        try {
            url = new URL(posterUrl);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built img URL: " + url);
        return url;
    }

    // Builds poster URL for movie details
    public static URL buildMediumPosterUrl(String path) {
        URL url = null;
        String posterUrl = img_url + img_size_medium + path;
        try {
            url = new URL(posterUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built img URL: " + url);
        return url;
    }

    // Builds reviews URL
    public static URL buildReviewsUrl(String movieId) {
        URL url = null;
        String reviewsUrl = base_url + "movie/" + movieId + "/reviews" + api_key + "&page=1";
        try {
            url = new URL(reviewsUrl);
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built reviews URL: " + url);
        return url;
    }

    // Takes care of setting up connection to the URL
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput){
                Log.v(TAG, "Http request sucessful");
                return scanner.next();

            }else {
                return null;
            }
        }finally {
            urlConnection.disconnect();
        }
    }
}
