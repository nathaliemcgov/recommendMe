package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailPasswordActivity extends AppCompatActivity {

    private RCMDFirebase firebase;
    private String email;
    private String password;
    private int index;
    private EditText password1CreateAcc;
    private EditText password2CreateAcc;
    private List<String> movieList;
    private List<String> bookList;
    private List<String> musicList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // Hide 2 password edit text fields
        password1CreateAcc = (EditText) findViewById(R.id.password1CreateAcc);
        password1CreateAcc.setVisibility(View.INVISIBLE);
        password2CreateAcc = (EditText) findViewById(R.id.password2CreateAcc);
        password2CreateAcc.setVisibility(View.INVISIBLE);

        Bundle bundle = getIntent().getExtras();
        // Lists of titles to send to Firebase after user enters email and password
        movieList = makeList(bundle.getString("movies").split(","));
        bookList = makeList(bundle.getString("books").split(","));
        musicList = makeList(bundle.getString("music").split(","));

        index = 0;

        Button nextBtnEmailPass = (Button) findViewById(R.id.nextBtnEmailPass);
        nextBtnEmailPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    // Getting email address entered
                    EditText emailAddrCreateAcc = (EditText) findViewById(R.id.emailAddrCreateAcc);
                    email = emailAddrCreateAcc.getText().toString();
                    if (email.length() > 0 && !email.equals(" ")) {
                        index++;

                        // Reached password entry screen
                        // Change text at top of page
                        TextView titleText = (TextView) findViewById(R.id.credsTitle);
                        titleText.setText("Now set a password\n(Enter it twice)");

                        // Hide email address edit text
                        EditText emailEntry = (EditText) findViewById(R.id.emailAddrCreateAcc);
                        emailEntry.setVisibility(View.INVISIBLE);

                        password1CreateAcc.setVisibility(View.VISIBLE);
                        password2CreateAcc.setVisibility(View.VISIBLE);
                    } else {
                        toasted("username");
                    }
                } else {
                    // User is done entering email address and password
                    // Getting password entered in field 1 and 2
                    String password1 = password1CreateAcc.getText().toString();
                    String password2 = password2CreateAcc.getText().toString();

                    if (password1.length() > 0 && password2.length() > 0) {
                        if (password1.equals(password2)) {  // Passwords match - account created
                            // Getting hashcode of password
                            int hashedPass = password1.hashCode();

                            // Adding user to firebase
                            Map<String, String> userMap = new HashMap<String, String>();
                            userMap.put("name", email);
                            firebase.createUser(userMap);

                            // Send desert island lists to firebase
                            firebase.setManyLikes(movieList, email, "movie");    // Movies
                            firebase.setManyLikes(bookList, email, "book");    // Books
                            firebase.setManyLikes(musicList, email, "music");    // Music

                            // Send user to recommendationsForYou
                            Intent intent = new Intent(getApplicationContext(), RecommendationsForYou.class);
                            intent.putExtra("user", email);
                            startActivity(intent);
                        } else {
                            // Alert that passwords entered do not match
                            toasted("password");

                            // Clearing password fields
                            password1CreateAcc.getText().clear();
                            password2CreateAcc.getText().clear();
                        }
                    } else {
                        toasted("nopassword");
                    }
                }
            }
        });
    }

    // Makes list of titles to send to firebase
    private List<String> makeList(String[] titles) {
        List<String> typeTitles = new ArrayList<String>();

        for (String title : titles) {
            typeTitles.add(title);
        }
        return typeTitles;
    }

    public void toasted(String fieldName) {
        CharSequence text = "";
        if (fieldName.equals("username")) {
            text = "Please enter an email address";
        } else if (fieldName.equals("nopassword")) {
            text = "Please fill out both password fields";
        } else {
            text = "Sorry, your passwords do not match";
        }
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}
