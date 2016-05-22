package edu.uw.nmcgov.recommendme;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EmailPasswordFragment extends Fragment {
    private RCMDFirebase myFirebase;
    private String email;
    private String password;
    private int index;
    private EditText password1CreateAcc;
    private EditText password2CreateAcc;
    private List<String> movieList;
    private List<String> bookList;
    private List<String> musicList;

    public EmailPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_email_password, container, false);

        Firebase.setAndroidContext(container.getContext());
        myFirebase = new RCMDFirebase();

        // Hide 2 password edit text fields
        password1CreateAcc = (EditText) rootView.findViewById(R.id.password1CreateAcc);
        password1CreateAcc.setVisibility(View.INVISIBLE);
        password2CreateAcc = (EditText) rootView.findViewById(R.id.password2CreateAcc);
        password2CreateAcc.setVisibility(View.INVISIBLE);

        Bundle bundle = this.getArguments();
        // Lists of titles to send to Firebase after user enters email and password
        movieList = makeList(bundle.getString("movies").split(","));
        bookList = makeList(bundle.getString("books").split(","));
        musicList = makeList(bundle.getString("music").split(","));

        index = 0;

        Button nextBtnEmailPass = (Button) rootView.findViewById(R.id.nextBtnEmailPass);
        nextBtnEmailPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    // Getting email address entered
                    EditText emailAddrCreateAcc = (EditText) rootView.findViewById(R.id.emailAddrCreateAcc);
                    email = emailAddrCreateAcc.getText().toString();

                    index++;

                    // Reached password entry screen
                    // Change text at top of page
                    TextView titleText = (TextView) rootView.findViewById(R.id.credsTitle);
                    titleText.setText("Now set a password\n(Enter it twice)");

                    // Hide email address edit text
                    EditText emailEntry = (EditText) rootView.findViewById(R.id.emailAddrCreateAcc);
                    emailEntry.setVisibility(View.INVISIBLE);
                } else {
                    // User is done entering email address and password
                    // Getting password entered in field 1 and 2
                    String password1 = password1CreateAcc.getText().toString();
                    String password2 = password2CreateAcc.getText().toString();

                    // Passwords match - account created
                    // Send desert island lists to firebase
                    if (password1.equals(password2)) {
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
                                                Intent intent = new Intent(getContext(), RecommendationsForYou.class);
                                                intent.putExtra("user", email);
                                                startActivity(intent);
                                            }
                                        });// Music
                                    }
                                });// Books
                            }
                        });// Movies
                    }
                    else {
                        // Alert that passwords entered do not match
                        toasted();
                    }
                }

            }
        });
        return rootView;
    }

    // Makes list of titles to send to firebase
    private List<String> makeList(String[] titles) {
        List<String> typeTitles = new ArrayList<String>();

        for (String title : titles) {
            typeTitles.add(title);
        }
        return typeTitles;
    }

    public void toasted() {
        Context context = getContext();
        CharSequence text = "Sorry, your passwords do not match";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
