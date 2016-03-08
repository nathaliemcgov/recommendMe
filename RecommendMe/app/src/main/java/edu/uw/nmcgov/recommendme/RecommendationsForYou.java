package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);
        return true;
    }

    // Gets action user chooses from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Show profile screen
            case R.id.profile:
                showProfile();
                return true;
            // Show search screen
            case R.id.searchRecommendations:
                showSearchForRecommendations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show profile screen
    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    // Show search screen
    private void showSearchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        startActivity(intent);
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
