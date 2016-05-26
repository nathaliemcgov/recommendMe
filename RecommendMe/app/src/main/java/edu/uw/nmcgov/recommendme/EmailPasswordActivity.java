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
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmailPasswordActivity extends AppCompatActivity {

    private RCMDFirebase myFirebase;
    private String email;
    private String password;
    private int index;
    private EditText password1CreateAcc;
    private EditText password2CreateAcc;
    private List<String> movieList;
    private List<String> bookList;
    private List<String> musicList;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_password);

        Firebase.setAndroidContext(this);
        myFirebase = new RCMDFirebase();

        mContext = this;

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

                    myFirebase.checkUniqueUser(email, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            index++;
                            Log.v("Log", "Unique");
                            // Reached password entry screen
                            // Change text at top of page
                            TextView titleText = (TextView) findViewById(R.id.credsTitle);
                            titleText.setText("Now set a password\n(Enter it twice)");

                            // Hide email address edit text
                            EditText emailEntry = (EditText) findViewById(R.id.emailAddrCreateAcc);
                            emailEntry.setVisibility(View.INVISIBLE);

                            password1CreateAcc.setVisibility(View.VISIBLE);
                            password2CreateAcc.setVisibility(View.VISIBLE);
                        }
                    }, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("Log", "non-unique");
                            int duration = Toast.LENGTH_LONG;
                            CharSequence text = "User already exists, please enter a different username";
                            Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                            toast.show();
                        }
                    });

                } else {
                    // User is done entering email address and password
                    // Getting password entered in field 1 and 2
                    String password1 = password1CreateAcc.getText().toString();
                    String password2 = password2CreateAcc.getText().toString();

                    if (password1.equals(password2)) {  // Passwords match - account created
                        // Getting hashcode of password
                        int hashedPass = password1.hashCode();

                        // Adding user to firebase
                        Map<String, Object> userMap = new HashMap<String, Object>();
                        userMap.put("name", email);
                        userMap.put("password", hashedPass);
                        myFirebase.createUser(userMap);

                        // Send desert island lists to firebase
                        myFirebase.setManyLikes(movieList, email, "movie", new Firebase.CompletionListener() {
                            @Override
                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                myFirebase.setManyLikes(bookList, email, "book", new Firebase.CompletionListener() {

                                    @Override
                                    public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                        myFirebase.setManyLikes(musicList, email, "music", new Firebase.CompletionListener() {
                                            @Override
                                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                                // Send user to recommendationsForYou
                                                Log.v("Final OnComplete", "Here");
                                                Intent intent = new Intent(mContext, RecommendationsForYou.class);
                                                intent.putExtra("user", email);
                                                startActivity(intent);
                                            }
                                        });// Music
                                    }
                                });// Books
                            }
                        });// Movies
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
            title = title.trim();
            if(title.length() > 0)
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
