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
    private Button createProfBtn;
    private static final String TAG = "start";

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
                existingLogin();
            }
        });

        // Click listener for create profile button
        createProfBtn = (Button) findViewById(R.id.createProfBtn);
        createProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfileActivity();
            }
        });
    }

    // Starts activity so user can search title
    private void searchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        startActivity(intent);
    }

    // Starts create profile activity
    private void createProfileActivity() {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }

    // Starts login activity
    private void existingLogin() {
        Intent intent = new Intent(this, ExistingLogin.class);
        startActivity(intent);
    }
}