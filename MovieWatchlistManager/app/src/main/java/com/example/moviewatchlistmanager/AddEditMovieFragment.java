package com.example.moviewatchlistmanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AddEditMovieFragment extends Fragment {

    private static final String ARG_MOVIE_ID = "movie_id";

    private EditText editTitle;
    private EditText editGenre;
    private EditText editYear;
    private RadioGroup radioStatus;
    private RadioButton radioPlanned;
    private RadioButton radioWatched;
    private SeekBar seekRating;
    private RatingTextView textRatingPreview;
    private Button buttonSave;
    private Button buttonDelete;

    private long movieId = -1;
    private boolean isEditMode = false;

    public AddEditMovieFragment() {
    }

    public static AddEditMovieFragment newInstance(long movieId) {
        AddEditMovieFragment fragment = new AddEditMovieFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MOVIE_ID, movieId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_MOVIE_ID)) {
            movieId = getArguments().getLong(ARG_MOVIE_ID);
            isEditMode = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        editTitle = view.findViewById(R.id.edit_title);
        editGenre = view.findViewById(R.id.edit_genre);
        editYear = view.findViewById(R.id.edit_year);
        radioStatus = view.findViewById(R.id.radio_status);
        radioPlanned = view.findViewById(R.id.radio_planned);
        radioWatched = view.findViewById(R.id.radio_watched);
        seekRating = view.findViewById(R.id.seek_rating);
        textRatingPreview = view.findViewById(R.id.text_rating_preview);
        buttonSave = view.findViewById(R.id.button_save);
        buttonDelete = view.findViewById(R.id.button_delete);

        seekRating.setMax(4);
        seekRating.setProgress(0);
        textRatingPreview.setRating(1);
        radioPlanned.setChecked(true);

        seekRating.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textRatingPreview.setRating(progress + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buttonSave.setOnClickListener(v -> saveMovie());
        buttonDelete.setOnClickListener(v -> deleteMovie());

        if (isEditMode) {
            loadMovieData();
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            buttonDelete.setVisibility(View.GONE);
        }

        return view;
    }

    private void loadMovieData() {
        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);

        Cursor cursor = requireContext().getContentResolver().query(
                uri,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_TITLE));
            String genre = cursor.getString(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_GENRE));
            int year = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_YEAR));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_STATUS));
            int rating = cursor.getInt(cursor.getColumnIndexOrThrow(MovieContract.MovieEntry.COLUMN_RATING));

            editTitle.setText(title);
            editGenre.setText(genre);
            editYear.setText(String.valueOf(year));

            if (status == MovieContract.MovieEntry.STATUS_WATCHED) {
                radioWatched.setChecked(true);
            } else {
                radioPlanned.setChecked(true);
            }

            seekRating.setProgress(rating - 1);
            textRatingPreview.setRating(rating);

            cursor.close();
        }
    }

    private void saveMovie() {
        String title = editTitle.getText().toString().trim();
        String genre = editGenre.getText().toString().trim();
        String yearText = editYear.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            editTitle.setError("Title is required");
            editTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(yearText)) {
            editYear.setError("Year is required");
            editYear.requestFocus();
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            editYear.setError("Enter a valid year");
            editYear.requestFocus();
            return;
        }

        if (year < 1900 || year > 2100) {
            editYear.setError("Year must be between 1900 and 2100");
            editYear.requestFocus();
            return;
        }

        int status = radioWatched.isChecked()
                ? MovieContract.MovieEntry.STATUS_WATCHED
                : MovieContract.MovieEntry.STATUS_PLANNED;

        int rating = seekRating.getProgress() + 1;

        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
        values.put(MovieContract.MovieEntry.COLUMN_GENRE, genre);
        values.put(MovieContract.MovieEntry.COLUMN_YEAR, year);
        values.put(MovieContract.MovieEntry.COLUMN_STATUS, status);
        values.put(MovieContract.MovieEntry.COLUMN_RATING, rating);

        if (isEditMode) {
            Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);
            int rowsUpdated = requireContext().getContentResolver().update(uri, values, null, null);

            if (rowsUpdated > 0) {
                Toast.makeText(getContext(), "Movie updated", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Update failed", Toast.LENGTH_SHORT).show();
            }
        } else {
            Uri uri = requireContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

            if (uri != null) {
                Toast.makeText(getContext(), "Movie saved", Toast.LENGTH_SHORT).show();
                requireActivity().getSupportFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Save failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteMovie() {
        if (!isEditMode) {
            return;
        }

        Uri uri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, movieId);
        int rowsDeleted = requireContext().getContentResolver().delete(uri, null, null);

        if (rowsDeleted > 0) {
            Toast.makeText(getContext(), "Movie deleted", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }
}