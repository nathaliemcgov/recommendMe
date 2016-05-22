package edu.uw.nmcgov.recommendme;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.HashMap;

/**
 * Created by austinweale on 3/9/16.
 */
public class ExistingLogin extends Activity {

    private static final String TAG = "existing";
    private int count;
    private EditText usernameField;
    private String username;
    private EditText passwordField;
    private String password;
    private HashMap<String, String> map;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_login_activity);
        count = 0;
        map = new HashMap<String, String>();
        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        Button button = (Button)findViewById(R.id.proceed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                usernameField = (EditText) findViewById(R.id.existing_email_entry);
                username = usernameField.getText().toString().trim();

                passwordField = (EditText) findViewById(R.id.existing_password_entry);
                password = passwordField.getText().toString().trim();

                if (username.length() > 0 && password.length() > 0) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please fill enter email address and password";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                } else {
                    recommendationsForYou(username, password);
                }
            }
        });
    }

    public void recommendationsForYou(String user, String password) {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", username);
        intent.putExtra("password", password.hashCode());

        Context context = getApplicationContext();
        CharSequence text = "User does not exist";
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, text, duration);

        firebase.checkUserExists(user, password, intent, this, toast);
    }
}
