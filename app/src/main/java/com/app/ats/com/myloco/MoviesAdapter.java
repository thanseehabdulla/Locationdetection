package com.app.ats.com.myloco;

import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by abdulla on 19/3/17.
 */

class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    private final MainActivity context;
    private List<Movie> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public MoviesAdapter(List<Movie> moviesList, MainActivity mainActivity) {
        this.moviesList = moviesList;
        this.context=mainActivity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }

    public void prepareMovieData() {
        SharedPreferences emer2=context.getSharedPreferences("list",MODE_PRIVATE);

        String snnn=emer2.getString("num1","0");
        if(snnn!="0"){
            String[] numbers = snnn.split(",");
            final String[] mobile = new String[numbers.length];
moviesList.clear();
            for (int i = 0; i < numbers.length; i++) {
                mobile[i] = (numbers[i]);
                Movie movie = new Movie(mobile[i], "Task initiated", "MyLoco");
                moviesList.add(movie);
            }

        }
        notifyDataSetChanged();
    }
}
