package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class CoolTipActivity extends AppCompatActivity {

    private Button nextButton;
    private String user;
    private String activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cool_tip);

        user = "";
        activity = "";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("activity") != null && bundle.getString("activity").length() > 0) {
                activity = bundle.getString("activity");
            } else {
                activity = "";
            }

            if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
                user = bundle.getString("user");
            } else {
                user = "";
            }
            Log.v("tag", user + " user");
        }

        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSecondToolTip();
            }
        });
    }

    private void showSecondToolTip() {
        Intent intent = new Intent(this, SecondCoolTipActivity.class);
        intent.putExtra("activity", activity);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
