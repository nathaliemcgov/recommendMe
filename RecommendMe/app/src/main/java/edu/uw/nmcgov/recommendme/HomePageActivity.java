package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

public class HomePageActivity extends AppCompatActivity {

    private Button loginBtn;
    private Button setupBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        // Click listener for login button
        loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existingLogin();
            }
        });

        setupBtn = (Button) findViewById(R.id.setupButton);
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingUp();
            }
        });

    }

    // Starts login activity
    private void existingLogin() {
        Intent intent = new Intent(this, ExistingLogin.class);
        startActivity(intent);
    }

    private void settingUp() {
        Intent intent = new Intent(this, SetupPageActivity.class);
        startActivity(intent);
    }
}
