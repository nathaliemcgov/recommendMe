package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SecondCoolTipActivity extends AppCompatActivity {

    private Button goButton;
    private String activity;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_cool_tip);

        user = "";
        activity = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("activity") != null && bundle.getString("activity").length() > 0) {
                activity = bundle.getString("activity");
            } else {
                activity = "";
            }
            Log.v("tag", activity + " emailpassword");

            if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
                user = bundle.getString("user");
            } else {
                user = "";
            }
            Log.v("tag", user + " user");
        }

        goButton = (Button) findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activity.equals("emailpassword")) {
                    recommendationsForYou();
                } else {
                    searchForRecommendations();
                }
            }
        });
    }

    // Starts activity so user can search title
    private void searchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    // Brings user to recommendations for you
    private void recommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
