package com.example.android.moviesproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by martin on 10/03/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> {

    Context context;
    private List<MoviesList> movie_list;
    final String title = "title";
    final String overview = "overview";
    final String release_date = "release_date";
    final String poster = "poster_path";
    final String vote_average = "vote_average";
    final String movie_id = "id";

    public MoviesAdapter(List<MoviesList> movies) {
        this.movie_list = movies;
    }

    public void setMovieData(List<MoviesList> movieData) {
        movie_list = movieData;
        notifyDataSetChanged();
    }

    // New ViewHolder with OnClickListener
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView posterImageView;

        public ViewHolder(View view) {
            super(view);
            view.setClickable(true);
            view.setOnClickListener(this);
            posterImageView = view.findViewById(R.id.poster);
            context = view.getContext();
        }

        //When item is clicked, gets movie data and starts movieInfo activity
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent movieInfo = new Intent(context, MovieInfoActivity.class);
            movieInfo.putExtra(title, movie_list.get(position).title);
            movieInfo.putExtra(overview, movie_list.get(position).overview);
            movieInfo.putExtra(poster, movie_list.get(position).posterPath);
            movieInfo.putExtra(release_date, movie_list.get(position).releaseDate);
            movieInfo.putExtra(vote_average, String.valueOf(movie_list.get(position).voteAverage));
            movieInfo.putExtra(movie_id, movie_list.get(position).movieId);
            context.startActivity(movieInfo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view;
        view = LayoutInflater.from(context).inflate(R.layout.movie_list_item, parent, false);
        ViewHolder  viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String url = NetworkUtils.buildSmallPosterUrl(movie_list.get(position).posterPath).toString();
        Picasso.with(context).load(url)
                .into(holder.posterImageView);
    }

    @Override
    public int getItemCount() {
        if(null == movie_list) return 0;
        return movie_list.size();
    }
}
