package edu.uw.nmcgov.recommendme;


import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.Loader;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONObject;


public class PhoneCapability extends FragmentActivity implements LoaderManager.LoaderCallbacks<String> {

    private static final String TAG = "MAIN";

    // different IDs for the API calls
    private static final int BOOKS_ID = 1;
    private static final int MUSIC_ID = 2;
    private static final int MOVIES_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_capability_buttons);

        // listening for the bookstore button click. Starts API call to yelp for bookstores
        Button bookstoreButton = (Button) findViewById(R.id.findBookstores);
        bookstoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "bookstore button clicked");
                // getLoaderManager().initLoader(1, null, PhoneCapability.this).forceLoad();

                Loader loader = getLoaderManager().getLoader(0);
                if (loader != null && loader.isReset()) {
                    getLoaderManager().restartLoader(BOOKS_ID, null, PhoneCapability.this);
                } else {
                    getLoaderManager().restartLoader(BOOKS_ID, null, PhoneCapability.this);
                }
            }
        });

        // listening for the music button click. Starts API call to yelp for music
        Button musicButton = (Button) findViewById(R.id.findMusicStores);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "music store button clicked");

                Loader loader = getLoaderManager().getLoader(1);
                if (loader != null && loader.isReset()) {
                    getLoaderManager().restartLoader(MUSIC_ID, null, PhoneCapability.this);
                } else {
                    getLoaderManager().restartLoader(MUSIC_ID, null, PhoneCapability.this);
                }
            }
        });

        // listening for the movie button click. Starts API call to yelp for movie theaters
        Button movieButton = (Button) findViewById(R.id.findMovieTheaters);
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "movie theater button clicked");

                Loader loader = getLoaderManager().getLoader(2);
                if (loader != null && loader.isReset()) {
                    getLoaderManager().restartLoader(MOVIES_ID, null, PhoneCapability.this);
                } else {
                    getLoaderManager().restartLoader(MOVIES_ID, null, PhoneCapability.this);
                }
            }
        });
    }



    // once the loader is complete, it will call the Yelp class to query for either a
    // bookstore, latitude or longtitude
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "creating loader");
        float latitude = 47.6097f;
        float longitude = -122.3331f;
        switch (id) {
            case BOOKS_ID:
                return new Yelp(this, "bookstores", latitude, longitude);
            case MUSIC_ID:
                return new Yelp(this, "music", latitude, longitude);
            case MOVIES_ID:
                return new Yelp(this, "movies", latitude, longitude);
            default:
                return new Yelp(this);
        }
    }

    // once the onCreateLoader is done, onLoadFinished is called to handle the data returned
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data == null) {
            Log.v(TAG, "give me a search term");
        } else {
            Log.v(TAG, data);

            try {
                JSONObject jsonObject = new JSONObject(data);
                JSONObject results = jsonObject.getJSONArray("businesses").getJSONObject(0);
                Log.v(TAG, results.toString());
            } catch (Exception ex) {
                Log.v(TAG, ex.getMessage());
            }
        }
    }


    // intent shows the options the user can go to in order to listen to music
    // currently if they click on a button in the ui, different options will pop up
    public void goToMusic(View v) {
        Log.v(TAG, "sending spotify intent");

        EditText movieEntered = (EditText) findViewById(R.id.musicSearchText);
        String artist = movieEntered.getText().toString();


        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        //intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*");
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // currently if they press a button in the ui, then they will go to netflix for a show that doesn't exist
    public void goToNetflix(View v) {
        Log.v(TAG, "sending netflix intent");
        String netFlixId = "43598743"; // <== isn't a real movie id
        String watchUrl = "http://www.netflix.com/watch/"+netFlixId;

        try {
            // this is if you have the movie ID
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
//            intent.setData(Uri.parse(watchUrl));
//            startActivity(intent);

            // I think this will work if the user types in a movie
            //String movieTitle = "Scandal";

            EditText movieEntered = (EditText) findViewById(R.id.movieSearchText);
            String movieTitle = movieEntered.getText().toString();

            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.search.SearchActivity");
            intent.putExtra("query", movieTitle);
            startActivity(intent);
        }
        catch(Exception e)
        {
            // netflix app isn't installed, send to website instead.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(watchUrl));
            startActivity(intent);
            //Toast.makeText(this, "Please install the NetFlix App!", Toast.LENGTH_SHORT).show();

        }
    }


    // not implemented
    @Override
    public void onLoaderReset(Loader<String> loader) {}
}
