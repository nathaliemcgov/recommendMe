package edu.uw.nmcgov.recommendme;


import android.os.Environment;
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


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

        // On click listener for "Save" button on selected tile
        Button saveTitleButton = (Button) findViewById(R.id.saveMediaTitleDetails);

        // Write title to phone's external storage
        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleSaveMediaTitle(selectedMediaTitle);
            }
        });

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

        // Switches to Madison's fragment
//        FragmentManager fm = getSupportFragmentManager();
//        FragmentTransaction ft = fm.beginTransaction();
//        PhoneCapability pc = new PhoneCapability();
//        ft.add(R.id.fragContainer, pc);
//        ft.commit();

//        thumbsDownBtn.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                thumbsDownBtn.setPressed(true);
//                thumbsUpBtn.setPressed(false);
//                return true;
//            }
//        });
    }

    // Writes saved media title to phone's external storage
    public void handleSaveMediaTitle(String title) {
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");
                String mediaTitle = title + "\n";

                try {
                    // create a filewriter and set append modus to true
                    FileWriter fw = new FileWriter(file, true);
                    fw.append(mediaTitle);
                    fw.close();

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }

                Log.v("tag", "file written: " + mediaTitle);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
