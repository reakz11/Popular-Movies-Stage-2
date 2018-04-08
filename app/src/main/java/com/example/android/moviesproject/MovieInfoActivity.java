package com.example.android.moviesproject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

/**
 * Created by martin on 10/03/2018.
 */

public class MovieInfoActivity extends AppCompatActivity {

    final String title = "title";
    final String overview = "overview";
    final String release_date = "release_date";
    final String poster = "poster_path";
    final String vote_average = "vote_average";
    final String max_rating = "/10";
    final String movie_id = "id";

    private TextView titleTextView;
    private TextView releaseDateTextView;
    private TextView overviewTextView;
    private TextView voteAverageTextView;
    private ImageView posterImageView;
    private TextView movieIdTextView;

    private ListView lv;
    ArrayList<HashMap<String, String>> reviewsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_info);

        reviewsList = new ArrayList<>();
        Intent intent = this.getIntent();

        lv = (ListView) findViewById(R.id.reviews);

        titleTextView = findViewById(R.id.movie_title);
        releaseDateTextView = findViewById(R.id.release_date);
        voteAverageTextView = findViewById(R.id.vote_average);
        overviewTextView = findViewById(R.id.overview);
        posterImageView = findViewById(R.id.poster);
        movieIdTextView = findViewById(R.id.movie_id);

        //If intent contains data, connect them to the views
        if ( intent != null && intent.hasExtra(title)){

            movieIdTextView.setText(intent.getStringExtra(movie_id));
            makeReviewsSearchQuery(intent.getStringExtra(movie_id));
            titleTextView.setText(intent.getStringExtra(title));
            releaseDateTextView.setText(intent.getStringExtra(release_date));
            overviewTextView.setText(intent.getStringExtra(overview));
            voteAverageTextView.setText(intent.getStringExtra(vote_average) + max_rating);
            String posterUrl = NetworkUtils.buildMediumPosterUrl(intent.getStringExtra(poster)).toString();
            Picasso.with(this)
                    .load(posterUrl)
                    .into(posterImageView);
            }
        }

    // Creates new URL and starts MoviesQueryTask with it
    private void makeReviewsSearchQuery(String movie_id) {
        URL reviewsSearchUrl = NetworkUtils.buildReviewsUrl(movie_id);
        new ReviewQueryTask().execute(reviewsSearchUrl);

        Log.v(TAG, "makeMoviesSearchQuery executed");
    }

    public class ReviewQueryTask extends AsyncTask<URL, Void, Void> {

        ArrayList<HashMap<String, String>> reviewsList;

        @Override
        protected void onPreExecute() {
            reviewsList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground (URL... params) {
            URL searchUrl = params[0];

            try  {
                String jsonResponse = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                Log.e(TAG, "Response from url: " + jsonResponse);
                try {
                    reviewsList.clear();
                    JSONObject object = new JSONObject(jsonResponse);
                    JSONArray reviews = object.getJSONArray("results");

                    // Getting data from JSON
                    for (int i = 0; i < reviews.length(); i++) {
                        JSONObject obj = reviews.getJSONObject(i);

                        String author = obj.getString("author");
                        String content = obj.getString("content");

                        HashMap<String, String> review = new HashMap<>();

                        review.put("author", author);
                        review.put("content", content);

                        reviewsList.add(review);
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
        protected void onPostExecute (Void result) {
            super.onPostExecute(result);

            ListAdapter adapter = new SimpleAdapter(
                    MovieInfoActivity.this, reviewsList,
                    R.layout.review_list_item, new String[] {"author", "content"}, new int[]{R.id.author, R.id.content});

            lv.setAdapter(adapter);

            Log.v(TAG, "onPostExecute done");
        }
    }
}
