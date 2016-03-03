package edu.uw.nmcgov.recommendme;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class StartPage extends AppCompatActivity {

    private static final String TAG = "start";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Button button = (Button)findViewById(R.id.profile_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "clicked");
                ProfileFragment profile = new ProfileFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, profile)
                        .commit();

            }
        });
    }
}
