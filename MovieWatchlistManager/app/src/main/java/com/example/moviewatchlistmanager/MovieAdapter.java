package com.example.moviewatchlistmanager;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(long id);
    }

    private Cursor mCursor;
    private final OnItemClickListener mListener;

    public MovieAdapter(Cursor cursor, OnItemClickListener listener) {
        mCursor = cursor;
        mListener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        if (mCursor == null || !mCursor.moveToPosition(position)) {
            return;
        }

        int idColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry._ID);
        int titleColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE);
        int genreColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_GENRE);
        int yearColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_YEAR);
        int statusColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_STATUS);
        int ratingColumn = mCursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RATING);

        long id = mCursor.getLong(idColumn);
        String title = mCursor.getString(titleColumn);
        String genre = mCursor.getString(genreColumn);
        int year = mCursor.getInt(yearColumn);
        int status = mCursor.getInt(statusColumn);
        int rating = mCursor.getInt(ratingColumn);

        holder.textTitle.setText(title);
        holder.textGenreYear.setText(genre + " • " + year);
        holder.textStatus.setText(status == MovieContract.MovieEntry.STATUS_WATCHED ? "Watched" : "Planned");
        holder.textRating.setRating(rating);

        holder.itemView.setOnClickListener(v -> mListener.onItemClick(id));
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) {
            mCursor.close();
        }
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle;
        TextView textGenreYear;
        TextView textStatus;
        RatingTextView textRating;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textGenreYear = itemView.findViewById(R.id.text_genre_year);
            textStatus = itemView.findViewById(R.id.text_status);
            textRating = itemView.findViewById(R.id.text_rating);
        }
    }
}