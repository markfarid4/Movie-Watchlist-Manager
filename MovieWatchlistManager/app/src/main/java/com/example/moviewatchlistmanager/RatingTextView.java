package com.example.moviewatchlistmanager;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class RatingTextView extends AppCompatTextView {

    public RatingTextView(Context context) {
        super(context);
        setRating(1);
    }

    public RatingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setRating(1);
    }

    public RatingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setRating(1);
    }

    public void setRating(int rating) {
        if (rating < 1) {
            rating = 1;
        }
        if (rating > 5) {
            rating = 5;
        }

        StringBuilder stars = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            if (i <= rating) {
                stars.append("★");
            } else {
                stars.append("☆");
            }
        }
        setText(stars.toString());
    }
}