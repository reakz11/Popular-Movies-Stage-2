package com.example.android.moviesproject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final String defaultSortType = "movie/popular";
    private RecyclerView mRecyclerView;
    private ArrayList<MoviesList> movie_list;
    MoviesAdapter adapter;

    private final String noNetwork = "Network unavailable.Please connect and refresh app";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_list);

        movie_list = new ArrayList<>();
        mRecyclerView = findViewById(R.id.rv_movies_list);
        adapter = new MoviesAdapter(movie_list);

        //Setting the RecyclerView to a fixed size
        mRecyclerView.setHasFixedSize(true);
        //Setting the layout manager for the RecyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 3, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        //Setting the adapter for the RecyclerView
        adapter = new MoviesAdapter(movie_list);
        mRecyclerView.setAdapter(adapter);

        loadData();
    }

    // Executes makeMoviesSearchQuery if network is available
    // If there is no network, toast is displayed
    private void loadData() {
        if (isNetworkAvailable()) {
            makeMoviesSearchQuery(defaultSortType);
        }else{
            Toast.makeText(this, noNetwork, Toast.LENGTH_LONG).show();
        }
    }

    // Creates new URL and starts MoviesQueryTask with it
    private void makeMoviesSearchQuery(String sort_type) {
        URL movieSearchUrl = NetworkUtils.buildUrl(sort_type);
        new MoviesQueryTask().execute(movieSearchUrl);

        Log.v(TAG, "makeMoviesSearchQuery executed");
    }


    // Getting data from movieSearchUrl
    public class MoviesQueryTask extends  AsyncTask<URL, Void, Void> {

        @Override
        protected Void doInBackground (URL... params) {
            URL searchUrl = params[0];

            try  {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                try {
                    movie_list.clear();
                    JSONObject object = new JSONObject(jsonResponse);
                    JSONArray jsonArray = object.getJSONArray("results");

                    // Getting data from JSON
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        MoviesList addMovies = new MoviesList();
                        addMovies.overview = obj.getString("overview");
                        addMovies.releaseDate = obj.getString("release_date");
                        addMovies.title = obj.getString("title");
                        addMovies.voteAverage = obj.getDouble("vote_average");
                        addMovies.posterPath = obj.getString("poster_path");
                        addMovies.movieId = obj.getString("id");
                        movie_list.add(addMovies);
                    }
                } catch (JSONException j) {
                    j.printStackTrace();
                    return null;
                }
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute (Void aVoid) {
            if (movie_list != null) {
                adapter.setMovieData(movie_list);
                Log.v(TAG, "onPostExecute done");
            }
        }
    }

    // Inflates menu with refresh "button" and sort options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    // Changes movie list based on what menu option user selects
    // If there is no network, toast is displayed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isNetworkAvailable()){
            int itemThatWasClickedId = item.getItemId();
            if (itemThatWasClickedId == R.id.sort_rating) {
                makeMoviesSearchQuery("movie/top_rated");
                Log.v(TAG, "sort_rating clicked");
                return true;
            } else if (itemThatWasClickedId == R.id.sort_popularity){
                makeMoviesSearchQuery("movie/popular");
                Log.v(TAG, "sort_popularity clicked");
                return true;
            } else if (itemThatWasClickedId == R.id.refresh){
                loadData();
            }
        } else {
            Toast.makeText(this, noNetwork, Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    // Checks if network is available
    public boolean isNetworkAvailable(){
        boolean status;
        try {
            ConnectivityManager cm =
                    (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            status = activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return status;
    }
}
