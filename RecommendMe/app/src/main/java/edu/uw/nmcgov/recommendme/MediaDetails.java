package edu.uw.nmcgov.recommendme;


import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class MediaDetails extends AppCompatActivity {
    private final String TAG = "MediaDetails";

    private TextView selectedTitle;
    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RCMDFirebase firebase;
    private String selectedMediaTitle;
    private Button wikiButton;
    private TextView contentDetails;
    private String user;
    private String activity;

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
//        Log.v("TAGGGGG", bundle.getString("activity"));
        selectedMediaTitle = bundle.getString("title");
        if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
            user = bundle.getString("user");
        } else {
            user = "";
        }

//        if (bundle.getString("activity") != null && bundle.getString("activity").length() > 0) {
//            activity = bundle.getString("activity");
//        }

//        RelativeLayout ratio = (RelativeLayout) findViewById(R.id.contentPercentPrompt);
//        if (activity.equals("recommendationsforyou")) {
//            ratio.setVisibility(View.GONE);
//        } else {
//            selectedTitle.setText(selectedMediaTitle);
//        }

        selectedTitle.setText(selectedMediaTitle);

        WikipediaData wikipediaData = new WikipediaData();
        wikipediaData.execute();

        // On click listener for "Save" button on selected tile
        ImageButton saveTitleButton = (ImageButton) findViewById(R.id.saveMediaTitleDetails);

        // Write title to phone's external storage
        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                handleSaveMediaTitle(selectedMediaTitle);
            }
        });

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) findViewById(R.id.thumbsUpBtn);
        thumbsDownBtn = (ImageButton) findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                //Set the button's appearance
                if (!button.isSelected() && user != null && !user.equals("")) {    // If the user 'likes' the title
//                    // Send to db the user's email + title of liked media
                    button.setSelected(!button.isSelected());
                    firebase.setLike(selectedMediaTitle, user);
//                } else {      // If the user 'unlikes' the title
//                    // Send to db the user's email + title of unliked media
                } else if (user == null || user.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please login to like media";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        thumbsDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                //Set the button's appearance
                Log.v("tag", selectedMediaTitle);
                if (!button.isSelected() && user != null && !user.equals("")) {    // If the user 'likes' the title
//                    // Send to db the user's email + title of liked media
                    button.setSelected(!button.isSelected());
                    firebase.setDislike(user, selectedMediaTitle);
//                } else {      // If the user 'undislikes' the title
//                    // Send to db the user's email + title of undisliked media
                } else if (user == null || user.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "Please login to dislike media";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
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

    // Class to query Wikipedia
    class WikipediaData extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private final String API_URL = "https://en.wikipedia.org/w/api.php";

        @Override
        protected void onPreExecute() {
            Log.v("WIKI", "Querying Wikipedia...");
        }

        // Querying Wikipedia
        @Override
        protected String doInBackground(Void... params) {
            // Logging in
            try {
                String formattedForWiki = selectedMediaTitle.replace(" ", "_");

                URL url = new URL
                        ("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + formattedForWiki);
//                https://en.wikipedia.org/w/api.php?action=mobileview&page=Therion_(band)
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String unformatted = stringBuilder.toString();
                    JSONObject json = new JSONObject(unformatted);
                    JSONObject description = new JSONObject(json.getString("query"));
                    JSONObject pages = new JSONObject(description.getString("pages"));

                    JSONArray keySet = pages.names();
                    String pageID = keySet.get(0).toString();

                    JSONObject obj = pages.getJSONObject(pageID);
                    String extract = obj.getString("extract");
                    Log.v("TAGGGGG", extract);

                    return extract;
                }
                finally {
                    urlConnection.disconnect();
                }
            }
            catch (Exception e) {
                Log.v("WIKI", e.getMessage());
                return null;
            }
        }

        // Once the async task is complete
        @Override
        protected void onPostExecute(String response) {
            if (response == null) {
                Log.v("WIKI", "No response from Wikipedia");
            }
            contentDetails = (TextView) findViewById(R.id.contentDetails);
            contentDetails.setText(response);
        }
    }
}
