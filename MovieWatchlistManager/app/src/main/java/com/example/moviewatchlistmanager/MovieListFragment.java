package com.example.moviewatchlistmanager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MovieListFragment extends Fragment {

    private RecyclerView recyclerView;
    private MovieAdapter adapter;

    public MovieListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = view.findViewById(R.id.recycler_movies);
        Button addButton = view.findViewById(R.id.button_add_movie);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new MovieAdapter(null, id -> {
            AddEditMovieFragment fragment = AddEditMovieFragment.newInstance(id);
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new AddEditMovieFragment())
                    .addToBackStack(null)
                    .commit();
        });

        loadMovies();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMovies();
    }

    private void loadMovies() {
        Cursor cursor = requireContext().getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                MovieContract.MovieEntry.COLUMN_TITLE + " ASC"
        );

        adapter.swapCursor(cursor);
    }
}