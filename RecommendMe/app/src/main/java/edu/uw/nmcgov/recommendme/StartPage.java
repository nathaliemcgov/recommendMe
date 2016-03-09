package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class StartPage extends Activity {

    private Button loginBtn;
    private Button exploreButton;

    private static final String TAG = "start";
    private String[] types;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        // Click listener for login button
        exploreButton = (Button) findViewById(R.id.exploreButton);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForRecommendations();
            }
        });

        // Click listener for login button
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recommendationsForYou();
            }
        });
    }

    // Starts activity so user can search title
    private void searchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        startActivity(intent);
    }

    // Starts explore recommendations activity
    private void recommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        startActivity(intent);
    }
}