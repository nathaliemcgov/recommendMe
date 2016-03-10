package edu.uw.nmcgov.recommendme;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaDetails extends AppCompatActivity {
    private final String TAG = "MediaDetails";

    private TextView selectedTitle;
    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RCMDFirebase firebase;

    public MediaDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_details);

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // Sets header to selected media title
        selectedTitle = (TextView) findViewById(R.id.selectedMediaTitle);

        Bundle bundle = getIntent().getExtras();
        final String selectedMediaTitle = bundle.getString("title");
        final String user = bundle.getString("user");

//        Bundle bundle = this.getArguments();
//        final String selectedMediaTitle = bundle.getString("mediaTitle");
        selectedTitle.setText(selectedMediaTitle);

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) findViewById(R.id.thumbsUpBtn);
//        thumbsDownBtn = (ImageButton) rootView.findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                Log.v("tag", selectedMediaTitle);
                if (button.isSelected()) {    // If the user 'likes' the title
//                    // Send to db the user's email + title of liked media
                    firebase.setLike(selectedMediaTitle, user);
//                } else {      // If the user 'unlikes' the title
//                    // Send to db the user's email + title of unliked media
                }
            }
        });

//        getActivity().getSupportFragmentManager().beginTransaction()
//                .add(R.id.fragContainer, fragTwo).commit();

//        thumbsDownBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                thumbsDownBtn.setPressed(true);
//                thumbsUpBtn.setPressed(false);
//                return true;
//            }
//        });
    }

}
