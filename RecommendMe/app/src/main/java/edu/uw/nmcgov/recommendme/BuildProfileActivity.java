package edu.uw.nmcgov.recommendme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;

public class BuildProfileActivity extends AppCompatActivity {

    private static final String TAG = "BuildProfileActivity";
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_profile);

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();
    }
}
