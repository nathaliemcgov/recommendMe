package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by iguest on 3/8/16.
 */
public class AMoneyPage extends AppCompatActivity {
    private Button loginBtn;
    private Button exploreButton;
    private static final String TAG = "start";
    private String[] types;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        index = 0;

        types = new String[5];
        types[0] = "email";
        types[1] = "password";
        types[2] = "movie";
        types[3] = "music";
        types[4] = "book";

        TextView text = (TextView)findViewById(R.id.type_box);
        text.setText(types[index]);
        index++;

        Button button = (Button)findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener() {
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
