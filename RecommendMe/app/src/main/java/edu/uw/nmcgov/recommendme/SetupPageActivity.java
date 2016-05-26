package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SetupPageActivity extends Activity {

    private static final String TAG = "start";
    private Button createProfBtn;
    private Button exploreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_page);

        // Click listener for create profile button
        createProfBtn = (Button) findViewById(R.id.createProfBtn);
        createProfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfileActivity();
            }
        });

        exploreButton = (Button) findViewById(R.id.exploreButton);
        exploreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFirstToolTip();
            }
        });
    }

    // Starts create profile activity
    private void createProfileActivity() {
        Intent intent = new Intent(this, CreateProfileActivity.class);
        startActivity(intent);
    }

    // Shows the first tooltip screen
    private void showFirstToolTip() {
        Intent intent = new Intent(this, CoolTipActivity.class);
        startActivity(intent);
    }
}