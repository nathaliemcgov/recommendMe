package edu.uw.nmcgov.recommendme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.firebase.client.Firebase;

public class UpdateEmailActivity extends AppCompatActivity {

    private static final String TAG = "update email activity";
    private String user;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");   // User's email

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        if(user == null) user = "";

        Log.v(TAG, user);

        Button updateEmailFinish = (Button) findViewById(R.id.updateEmailBtn);
    }
}
