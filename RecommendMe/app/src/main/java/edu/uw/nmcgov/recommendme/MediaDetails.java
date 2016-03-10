package edu.uw.nmcgov.recommendme;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
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
public class MediaDetails extends Fragment {
    private final String TAG = "MediaDetails";

    private TextView selectedTitle;
    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RCMDFirebase firebase;

    public MediaDetails() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_media_details, container, false);

        Firebase.setAndroidContext(container.getContext());
        firebase = new RCMDFirebase();

        // Sets header to selected media title
        selectedTitle = (TextView) rootView.findViewById(R.id.selectedMediaTitle);
        Bundle bundle = this.getArguments();
        final String selectedMediaTitle = bundle.getString("mediaTitle");
        selectedTitle.setText(selectedMediaTitle);

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) rootView.findViewById(R.id.thumbsUpBtn);
//        thumbsDownBtn = (ImageButton) rootView.findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                button.setSelected(!button.isSelected());
                Log.v("tag", selectedMediaTitle);
                if (button.isSelected()) {    // If the user 'likes' the title
//                    // Send to db the user's email + title of liked media
                    firebase.setLike(selectedMediaTitle, "email@email.com");
//                } else {      // If the user 'unlikes' the title
//                    // Send to db the user's email + title of unliked media
                }
            }
        });

        FragmentManager fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        PhoneCapability fragTwo = new PhoneCapability();
        ft.add(R.id.fragContainer, fragTwo);
        ft.commit();

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
        return rootView;
    }

}
