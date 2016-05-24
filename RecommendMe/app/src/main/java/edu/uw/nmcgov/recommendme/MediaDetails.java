package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import com.firebase.client.FirebaseError;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Verb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
    private RCMDFirebase myFirebase;

    private String selectedMediaTitle;
    private TextView contentDetails;
    private String user;
    private String activity;
    private String mediaType;
    private String titleSearchedFor;
    private String wikiSuffix;
    private ImageButton saveTitleButton;

    public MediaDetails() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_details);

        Firebase.setAndroidContext(this);
        myFirebase = new RCMDFirebase();

        // Sets header to selected media title
        selectedTitle = (TextView) findViewById(R.id.selectedMediaTitle);

        Bundle bundle = getIntent().getExtras();
        selectedMediaTitle = bundle.getString("title");

        if (bundle.getString("user") != null && bundle.getString("user").length() > 0)
            user = bundle.getString("user");
        else
            user = "";

        if (bundle.getString("activity") != null && bundle.getString("activity").length() > 0)
            activity = bundle.getString("activity");
        else
            activity = "search";

        if (bundle.getString("mediaType") != null && bundle.getString("mediaType").length() > 0)
            mediaType = bundle.getString("mediaType");
        else
            mediaType = "";

        if (bundle.getString("searchTitle") != null && bundle.getString("searchTitle").length() > 0)
            titleSearchedFor = bundle.getString("searchTitle");
        else
            titleSearchedFor = "";

        Log.v("SEARCHED TITLE", titleSearchedFor + " hellrrrrrr");

        // Suffix that will be used if Wikipedia does not return correct description of media title
        if (mediaType.equals("movie"))
            wikiSuffix = "_(film)";
        else if (mediaType.equals("book"))
            wikiSuffix = "_(novel)";
        else if (mediaType.equals("music"))
            wikiSuffix = "_(band)";

        RelativeLayout ratio = (RelativeLayout) findViewById(R.id.contentPercentPrompt);
        if (activity.equals("recommendationsforyou")) {
            ratio.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) findViewById(R.id.searchTerm);
            textView.setText(titleSearchedFor);
        }

        selectedTitle.setText(selectedMediaTitle);

        selectedTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CircleGraphicActivity.class);
                intent.putExtra("title", selectedMediaTitle);
                startActivity(intent);
            }
        });

        // Shows description from Wikipedia of media title selected
        if (mediaType.length() > 0 && !mediaType.equals("")) {
            WikipediaData wikipediaData = new WikipediaData();
            wikipediaData.execute();
        }

        // On click listener for "Save" button on selected tile
        saveTitleButton = (ImageButton) findViewById(R.id.saveMediaTitleDetails);

        // Write title to phone's external storage
        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!v.isSelected()) {
                    v.setSelected(!v.isSelected());
                    saveTitleButton.setImageResource(R.drawable.ic_star);
                    handleSaveMediaTitle(selectedMediaTitle);
                } else {
                    v.setSelected(!v.isSelected());
                    saveTitleButton.setImageResource(R.drawable.ic_star_unselected);
                    handleRemoveTitleFromSaved(selectedMediaTitle);
                }
            }
        });

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) findViewById(R.id.thumbsUpBtn);
        thumbsDownBtn = (ImageButton) findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View button) {
                //Set the button's appearance
                if (!button.isSelected() && user != null && !user.equals("")) {    // If the user 'likes' the title
                    if (thumbsDownBtn.isSelected()) {    // Toggling
                        thumbsDownBtn.setSelected(!thumbsDownBtn.isSelected());
                        thumbsDownBtn.setImageResource(R.drawable.ic_thumbs_down);
                    }
//                    // Send to db the user's email + title of liked media
                    myFirebase.checkLike(selectedMediaTitle, user, mediaType, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inNotLike");
                            myFirebase.setLike(selectedMediaTitle, user);
                            Context context = getApplicationContext();
                            CharSequence text = "Liked : " + selectedMediaTitle;
                            thumbsUpBtn.setImageResource(R.drawable.ic_thumbs_up_tile_selected);
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            button.setSelected(!button.isSelected());
                        }
                    }, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = getApplicationContext();
                            CharSequence text = "You already like this!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = getApplicationContext();
                            CharSequence text = "You said you dislike this!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
//                } else {      // If the user 'unlikes' the title
//                    // Send to db the user's email + title of unliked media
                } else if (button.isSelected() && user != null && !user.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "You already like this!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
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
            public void onClick(final View button) {
                //Set the button's appearance
                Log.v("tag", selectedMediaTitle);

                if (!button.isSelected() && user != null && !user.equals("")) {    // If the user 'likes' the title
//                    // Send to db the user's email + title of liked media
                    myFirebase.checkDislike(selectedMediaTitle, user, mediaType, new Firebase.CompletionListener()
                    {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inNotLike");
                            myFirebase.setDislike(selectedMediaTitle, user);
                            Context context = getApplicationContext();
                            CharSequence text = "Dislike : " + selectedMediaTitle;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            thumbsDownBtn.setImageResource(R.drawable.ic_thumbs_down_tile_selected);
                            toast.show();
                            button.setSelected(!button.isSelected());
                        }
                    }, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = getApplicationContext();
                            CharSequence text = "You already dislike this!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }, new Firebase.CompletionListener() {

                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = getApplicationContext();
                            CharSequence text = "You said you like this!";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
                } else if (button.isSelected() && user != null && !user.equals("")) {
                    Context context = getApplicationContext();
                    CharSequence text = "You already dislike this!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

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
                    Log.v("SAVING", mediaTitle);

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }

                Log.v("tag", "file written: " + mediaTitle);
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }

    public void handleRemoveTitleFromSaved(String mediaTitle) {
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;

                    Log.v("UNSAVING", mediaTitle);

                    FileWriter fw = new FileWriter(file, false);
                    while ((line = reader.readLine()) != null) {
                        Log.v("CHECKING", "" + line.contains(mediaTitle));
                        if (!line.contains(mediaTitle)) {
                            fw.append(line);
                            fw.close();
                        }
                    }

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }
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
        @Override
        protected void onPreExecute() {
            Log.v("WIKI", "Querying Wikipedia...");
        }

        // Querying Wikipedia
        @Override
        protected String doInBackground(Void... params) {
            try {
                String formattedForWiki = "";

                if (selectedMediaTitle.equals("1984")) {
                    formattedForWiki = "Nineteen_Eighty-Four";
                } else {
                    formattedForWiki = selectedMediaTitle.replace(" ", "_");
                }

                String extract = wikiRequest(formattedForWiki);

                if (extract.contains("may refer to")) { // If Wiki is suggesting multiple results
                    // Appending media type
                    formattedForWiki += wikiSuffix;

                    // Making request again with media type appended to end of query
                    extract = wikiRequest(formattedForWiki);

                } else {
                    if (mediaType.equals("movie")) {
                        if (!extract.contains("film") && !extract.contains("movie")) {
                            formattedForWiki += wikiSuffix;
                            // Making request again with media type appended to end of query
                            extract = wikiRequest(formattedForWiki);
                        }
                    } else if (mediaType.equals("book")) {
                        if (!extract.contains("book") && !extract.contains("novel")) {
                            formattedForWiki += wikiSuffix;
                            // Making request again with media type appended to end of query
                            extract = wikiRequest(formattedForWiki);
                        }
                    } else if (mediaType.equals("music")) {
                        if (!extract.contains("music") && !extract.contains("band")) {
                            formattedForWiki += wikiSuffix;
                            // Making request again with media type appended to end of query
                            extract = wikiRequest(formattedForWiki);
                        }
                    }
                }
                return extract;
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

        private String wikiRequest(String formattedForWiki) {
            try {
                URL url = new URL
                        ("https://en.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro=&explaintext=&titles=" + formattedForWiki);

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
                } finally {
                    urlConnection.disconnect();
                }
            }
            catch (Exception e) {
                Log.v("WIKI", e.getMessage());
                return null;
            }
        }
    }
}
