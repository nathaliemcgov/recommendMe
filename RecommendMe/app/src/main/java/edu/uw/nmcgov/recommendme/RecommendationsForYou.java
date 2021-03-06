package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
        implements RecommendationTileGrid.OnMediaSelectionListener {

    private GridView tileGrid;
    private ArrayList<String> recommendationList;
    ArrayAdapter<String> adapter;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_for_you);

        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
            user = bundle.getString("user");
        } else {
            user = "";
        }
        Log.v("tag", user + " RcmdsForYou");

        // Fragment to display tile grid
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        RecommendationTileGrid tileGridFragment = new RecommendationTileGrid();

        Bundle userBundle = new Bundle();
        userBundle.putString("user", user);        // User's email
        tileGridFragment.setArguments(userBundle);

        ft.add(R.id.gridContainer2, tileGridFragment, "Grid");
        ft.commit();
    }

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);

        MenuItem searchMenuOption = menu.findItem(R.id.recommendationsForYou);
        searchMenuOption.setVisible(false);
        return true;
    }

    // Gets action user chooses from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Show search screen
            case R.id.searchRecommendations:
                showSearchForRecommendations();
                return true;
            case R.id.profile:
                showProfile();
                return true;
            case R.id.savedRecommendations:
                showSavedRecommendations();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show search screen
    private void showSearchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showSavedRecommendations() {
        Intent intent = new Intent(this, SavedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // When a tile is selected, move to fragment that gives details about the tile selected
    @Override
    public void onMediaSelected(String mediaTile) {

        // Fragment that contains details about the selected tile
        MediaDetails details = new MediaDetails();

        Intent intent = new Intent(this, MediaDetails.class);
        intent.putExtra("title", mediaTile);
        intent.putExtra("user", user);
        intent.putExtra("activity", "recommendationsforyou");
        startActivity(intent);
    }
}
