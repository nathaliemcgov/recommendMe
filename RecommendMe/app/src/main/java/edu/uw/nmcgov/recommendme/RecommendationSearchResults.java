package edu.uw.nmcgov.recommendme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RecommendationSearchResults extends AppCompatActivity {
    private final String TAG = "RecommendationSearchRes";

    private TextView titleSearchedFor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_search_results);

        // TextView for header of screen
        titleSearchedFor = (TextView) findViewById(R.id.titleSearchedFor);
        Bundle bundle = getIntent().getExtras();
        String titleSearched = bundle.getString("title");
        Log.v(TAG, titleSearched);

        titleSearchedFor.setText(titleSearched);
    }
}
