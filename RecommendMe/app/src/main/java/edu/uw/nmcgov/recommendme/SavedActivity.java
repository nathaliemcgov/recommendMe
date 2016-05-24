package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity implements RecommendationTileGrid.OnMediaSelectionListener {

    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RCMDFirebase firebase;
    private String user;
    RecommendationTileGrid tileGridFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
                user = bundle.getString("user");
            }
        }

        // Fragment to display tile grid for saved media titles
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        Bundle bundle = new Bundle();
        bundle.putString("user", user);
        tileGridFragment = new RecommendationTileGrid();
        tileGridFragment.setArguments(bundle);
        ft.add(R.id.gridContainer3, tileGridFragment, "Grid");
        ft.commit();
    }

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);

        MenuItem saved = menu.findItem(R.id.savedRecommendations);
        saved.setVisible(false);

        if (user == null || user.length() == 0) {
            MenuItem profileMenuOption = menu.findItem(R.id.profile);
            profileMenuOption.setVisible(false);
            MenuItem recsForYou = menu.findItem(R.id.recommendationsForYou);
            recsForYou.setVisible(false);
        }
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
            case R.id.recommendationsForYou:
                showRecommendationsForYou();
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

    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showRecommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        if (user != null && user.length() > 0) {
            intent.putExtra("user", user);
        }
        startActivity(intent);
    }

    @Override
    public void onMediaSelected(String mediaTile) {
        // Fragment that contains details about the selected tile
        MediaDetails details = new MediaDetails();

        Intent intent = new Intent(this, MediaDetails.class);
        intent.putExtra("title", mediaTile);
        startActivity(intent);
    }
}
