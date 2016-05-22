package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by austinweale on 3/3/16.
 */
public class AccountsActivity extends AppCompatActivity {

    private static final String TAG = "linked";
    RCMDFirebase firebase;
    Context mContext;
    String user;

    //so we can communicate with the profile activity
    public interface OnProfileSelectedListener {
        void onProfileSelected();
//        void onPickSelected(String type);
    }

    public AccountsActivity(){

    }

    public void onCreate(Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_fragment);

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");   // User's email

        //back button
        Button button = (Button) findViewById(R.id.linked_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "back");
                String username = ((EditText) findViewById(R.id.lastFMUserName)).getText().toString();
                if(username == null || username.length() < 1 || username.equals("LastFM username")) {
                    CharSequence text = "Please enter a username";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                } else {
                    LastFMData data = new LastFMData(username);
                    data.execute();
                }

            }
        });


    }

    class LastFMData extends AsyncTask<Void, Void, String> {

        private Exception exception;
        private final String API_URL_FIRST = "https://ws.audioscrobbler.com/2.0/?method=user.gettopartists&user=";
        private final String API_URL_SECOND = "&api_key=5e2801138b4ef76aeb794a9469cb3687&format=json";
        private String API_URL;

        private List<String> artistList;
        private String lastFMUser;

        public LastFMData(String lastFMUser) {
            super();
            this.lastFMUser = lastFMUser;
            API_URL = API_URL_FIRST + lastFMUser + API_URL_SECOND;
        }

        @Override
        protected void onPreExecute() {
            Log.v("LastFM", "Querying LastFM...");
            artistList = new ArrayList<String>();
        }

        // Querying Wikipedia
        @Override
        protected String doInBackground(Void... params) {
            // Logging in
            try {


                URL url = new URL
                        (API_URL);
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
                    JSONObject top = json.getJSONObject("topartists");
                    JSONArray artists = top.getJSONArray("artist");


                    for(int i = 0; i < 5; i++) {
                        JSONObject artist = artists.getJSONObject(i);
                        if(!artist.getString("name").contains("/") &&
                                !artist.getString("name").contains(".") &&
                                !artist.getString("name").contains("#") &&
                                !artist.getString("name").contains("[") &&
                                !artist.getString("name").contains("]") &&
                                !artist.getString("name").contains("$"))
                            artistList.add(artist.getString("name"));

                    }

                    return "";
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

            firebase.setManyLikes(artistList, user, "music");

        }
    }


}
