package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SearchForRecommendations extends AppCompatActivity {

    private final String TAG = "SearchForRcmds";

    private EditText searchMediaText;
    private Button searchTitleBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_recommendations);

        // Sets keyboard to always hidden on create
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // The text entry so user can search
        searchMediaText = (EditText) findViewById(R.id.searchMediaText);

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
            startActivity(intent);
        } else {
            // Shows toast to remind user to enter title in search field before pressing search
            Toast.makeText(SearchForRecommendations.this, "Please enter a book title, music artist,"
                    + " or movie title.", Toast.LENGTH_SHORT).show();
        }
    }
}