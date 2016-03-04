package edu.uw.nmcgov.recommendme;

import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RecommendationSearchResults extends AppCompatActivity
        implements RecommendationTileGrid.OnMediaSelectionListener {
    private final String TAG = "RecommendationSearchRes";

    private TextView titleSearchedFor;

    RecommendationTileGrid tileGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_search_results);

        // TextView for header of screen
        titleSearchedFor = (TextView) findViewById(R.id.titleSearchedFor);
        Bundle bundle = getIntent().getExtras();
        String titleSearched = bundle.getString("title");
        Log.v(TAG, titleSearched);

        // Adds title searched for to header
        titleSearchedFor.setText(titleSearched);

        // Fragment to display tile grid
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        RecommendationTileGrid tileGridFragment = new RecommendationTileGrid();
        ft.add(R.id.gridContainer, tileGridFragment, "Grid");
        ft.commit();
    }

    // When a tile is selected, move to fragment that gives details about the tile selected
    @Override
    public void onMediaSelected(Cursor mediaTile) {
        // Fragment that contains details about the selected tile
        MediaDetails details = new MediaDetails();

        // Gets the title of the selected title
        String selectedMedia = mediaTile.getString(0);

        Bundle bundle = new Bundle();
        bundle.putString("mediaTitle", selectedMedia);

        details.setArguments(bundle);

        // Display title detail fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recommendationSearchResults, details)
                .addToBackStack(null)
                .commit();
    }
}
