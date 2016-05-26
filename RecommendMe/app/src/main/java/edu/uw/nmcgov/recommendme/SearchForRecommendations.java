package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;

public class SearchForRecommendations extends AppCompatActivity {

    private final String TAG = "SearchForRcmds";

    private AutoCompleteTextView searchMediaText;
    private Button searchTitleBtn;
    private String user;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_recommendations);

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // Sets keyboard to always hidden on create
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // The text entry so user can search
        searchMediaText = (AutoCompleteTextView) findViewById(R.id.searchMediaText);
        String[] suggestions = {};
        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(suggestions));
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, lst);

        searchMediaText.setAdapter(adapter);
        searchMediaText.setThreshold(0);

        searchMediaText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP)
                    firebase.autoComplete(searchMediaText.getText().toString(), adapter);
                searchMediaText.showDropDown();
                return false;
            }
        });

        // Click listener for search title button
        searchTitleBtn = (Button) findViewById(R.id.searchMediaBtn);
        searchTitleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hides keyboard when search button is clicked
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchMediaText.getWindowToken(), 0);

                searchTitleEntered();
            }
        });

        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
                user = bundle.getString("user");
            }
        }
    }

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);

        // Hides search option from menu because user is currently on search screen
        MenuItem searchMenuOption = menu.findItem(R.id.searchRecommendations);
        searchMenuOption.setVisible(false);
        if (user == null || user.length() == 0) {
            MenuItem recsForYou = menu.findItem(R.id.recommendationsForYou);
            recsForYou.setVisible(false);
        }
        return true;
    }

    // Gets action user chooses from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.recommendationsForYou:
                showRecommendationsForYou();
                return true;
            case R.id.savedRecommendations:
                showSavedRecommendations();
                return true;
            case R.id.profile:
                showProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRecommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showSavedRecommendations() {
        Intent intent = new Intent(this, SavedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // Called when search button is clicked in order to search for title entered
    private void searchTitleEntered() {
        String titleSearched = searchMediaText.getText().toString();
        Log.v(TAG, titleSearched);

        // Check to ensure user entered title to search for
        if (titleSearched.length() > 0) {
            // Adds title searched for as an extra for next activity
            Intent intent = new Intent(this, RecommendationSearchResults.class);
            intent.putExtra("title", titleSearched);
            intent.putExtra("user", user);
            startActivity(intent);
        } else {
            // Shows toast to remind user to enter title in search field before pressing search
            Toast.makeText(SearchForRecommendations.this, "Please enter a book title, music artist,"
                    + " or movie title.", Toast.LENGTH_SHORT).show();
        }
    }
}
