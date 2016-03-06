package edu.uw.nmcgov.recommendme;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaDetails extends Fragment {
    private final String TAG = "MediaDetails";

    private TextView selectedTitle;
    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;

    public MediaDetails() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_media_details, container, false);

        // Sets header to selected media title
        selectedTitle = (TextView) rootView.findViewById(R.id.selectedMediaTitle);
        Bundle bundle = this.getArguments();
        String selectedMediaTitle = bundle.getString("mediaTitle");
        selectedTitle.setText(selectedMediaTitle);

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) rootView.findViewById(R.id.thumbsUpBtn);
        thumbsDownBtn = (ImageButton) rootView.findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnTouchListener(new View.OnTouchListener() {
            int touchCount = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG, thumbsUpBtn.isPressed() + "");

                thumbsUpBtn.setPressed(true);
                thumbsDownBtn.setPressed(false);
                return true;
            }
        });

        thumbsDownBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                thumbsDownBtn.setPressed(true);
                thumbsUpBtn.setPressed(false);
                return true;
            }
        });


        return rootView;
    }

}
