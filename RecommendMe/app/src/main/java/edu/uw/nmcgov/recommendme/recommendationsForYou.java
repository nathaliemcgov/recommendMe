package edu.uw.nmcgov.recommendme;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

public class RecommendationsForYou extends AppCompatActivity
        implements RecommendationTileGrid.OnMediaSelectionListener{

    private GridView tileGrid;
    private ArrayList<String> recommendationList;
    ArrayAdapter<String> adapter;

    static final String[] recommendations = new String[] {
            "The Departed", "The Illiad"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_for_you);

        // Fragment to display tile grid
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        RecommendationTileGrid tileGridFragment = new RecommendationTileGrid();
        ft.add(R.id.gridContainer2, tileGridFragment, "Grid");
        ft.commit();
    }

    // When a tile is selected, move to fragment that gives details about the tile selected
    @Override
    public void onMediaSelected(String mediaTile) {
        // Fragment that contains details about the selected tile
        MediaDetails details = new MediaDetails();

        Bundle bundle = new Bundle();
        bundle.putString("mediaTitle", mediaTile);

        details.setArguments(bundle);

        // Display title detail fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.recommendationGrid, details)
                .addToBackStack(null)
                .commit();
    }
}
