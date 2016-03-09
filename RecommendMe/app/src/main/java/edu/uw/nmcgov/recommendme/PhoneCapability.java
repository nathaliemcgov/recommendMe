package edu.uw.nmcgov.recommendme;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import android.Manifest;
import android.app.LoaderManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class PhoneCapability extends FragmentActivity
        implements LoaderManager.LoaderCallbacks<String>, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MAIN";

    // different IDs for the API calls
    private static final int BOOKS_ID = 1;
    private static final int MUSIC_ID = 2;
    private static final int MOVIES_ID = 3;

    // constants for the location services
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // location stuff
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private float latitude;
    private float longitude;

    // used to store nearby businesses
    private Map<String, List<JSONObject>> mapOfNearbyBusinesses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_capability_buttons);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapOfNearbyBusinesses = new HashMap<String, List<JSONObject>>();


        // listening for the bookstore button click. Starts API call to yelp for bookstores
        Button bookstoreButton = (Button) findViewById(R.id.findBooks);
        bookstoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.v(TAG, "bookstore button clicked");
                getNearbyBusinesses("books");
            }
        });

        // listening for the music button click. Starts API call to yelp for music
        Button musicButton = (Button) findViewById(R.id.findMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "music store button clicked");
                getNearbyBusinesses("music");
            }
        });

        // listening for the movie button click. Starts API call to yelp for movie theaters
        Button movieButton = (Button) findViewById(R.id.findMovies);
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "movie theater button clicked");
                getNearbyBusinesses("movies");
            }
        });
    }

    // pass in either 'books', 'music', or 'movies', to search for
    // nearby businesses that relate to these terms
    public void getNearbyBusinesses(String businessType) {
        if (mLastLocation == null) {
            Log.v(TAG, "there's no location, so get one");
            onConnected(null);
            loadAPIResults();
        }

        Log.v(TAG, "displaying results into the UI");
        try {
//            Set<JSONObject> set = mapOfNearbyBusinesses.get(businessType);
//            for (JSONObject keyToBusiness : set) {


            //********** HARD CODED BUSINESS DISPLAY**************//
            List<JSONObject> list = mapOfNearbyBusinesses.get(businessType);

            JSONObject keyToBusinessOne = list.get(0);
            YelpData businessData = new YelpData(keyToBusinessOne);
            Log.v(TAG, businessData.toString());

            TextView businessOneName = (TextView) findViewById(R.id.businessNameOne);
            businessOneName.setClickable(true);
            businessOneName.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='" + businessData.getMobileUrl() + "'>" + businessData.getName() + "</a>";
            businessOneName.setText(Html.fromHtml(text));
            //businessOneName.setText(businessData.getName());

            TextView businessOneInfo = (TextView) findViewById(R.id.businessInfoOne);
            businessOneInfo.setText(businessData.toString());



            JSONObject keyToBusinessTwo = list.get(1);
            YelpData businessDataTwo = new YelpData(keyToBusinessTwo);
            Log.v(TAG, businessDataTwo.toString());

            TextView businessTwoName = (TextView) findViewById(R.id.businessNameTwo);
            businessTwoName.setClickable(true);
            businessTwoName.setMovementMethod(LinkMovementMethod.getInstance());
            String textTwo = "<a href='" + businessDataTwo.getMobileUrl() + "'>" + businessDataTwo.getName() + "</a>";
            businessTwoName.setText(Html.fromHtml(textTwo));
            //businessOneName.setText(businessData.getName());

            TextView businessTwoInfo = (TextView) findViewById(R.id.businessInfoTwo);
            businessTwoInfo.setText(businessDataTwo.toString());

//            }
        } catch (Exception ex) {
            Log.v(TAG, ex.getMessage());
        }
    }


    // once the loader is complete, it will call the Yelp class to query for either a
    // bookstore, latitude or longtitude
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        Log.v(TAG, "creating loader");
        switch (id) {
            case BOOKS_ID:
                return new Yelp(this, "books", latitude, longitude);
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
    public void onLoadFinished(Loader<String> loader, String dataHacked) {
        if (dataHacked == null) {
            Log.v(TAG, "give me a search term");
        } else {
            Log.v(TAG, "storing the businesses to the map");

            // separates the search term from the json data
            String searchTerm = dataHacked.substring(0, dataHacked.indexOf(' '));
            String data = dataHacked.substring(dataHacked.indexOf(' ') + 1);

            Log.v(TAG, searchTerm);
            Log.v(TAG, data);
            try {

                JSONObject jsonObject = new JSONObject(data);

                // array of the businesses that we were returned by the query
                JSONArray jsonArray = jsonObject.getJSONArray("businesses");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    JSONObject tempData = jsonArray.getJSONObject(i);
                    // this doesn't work for some reason....
//                    YelpData yelpBusiness = new YelpData(tempData};
//                    Log.v(TAG, key);
                    if (!mapOfNearbyBusinesses.containsKey(searchTerm)) {
//                        Set<JSONObject> set = new HashSet<>();
                        List<JSONObject> businesses = new ArrayList<JSONObject>();
                        mapOfNearbyBusinesses.put(searchTerm, businesses);
                    }
                    mapOfNearbyBusinesses.get(searchTerm).add(tempData);
                }


            } catch (Exception ex) {
                Log.v(TAG, ex.getMessage());
            }
        }
    }


    // not implemented
    @Override
    public void onLoaderReset(Loader<String> loader) {}

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


        EditText movieEntered = (EditText) findViewById(R.id.movieSearchText);
        String movieTitle = movieEntered.getText().toString();

        String watchUrl = "http://www.netflix.com/watch/" + movieTitle;

        try {
            // this is if you have the movie ID
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.launch.UIWebViewActivity");
//            intent.setData(Uri.parse(watchUrl));
//            startActivity(intent);

            // I think this will work if the user types in a movie
            //String movieTitle = "Scandal";


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
            Toast.makeText(this, "Please install the Netflix App!", Toast.LENGTH_SHORT).show();
        }
    }



    //**********ACCESSING LOCATION STUFF***********//
    // requests permission to get the last location. If connected and there is a last location, handleNewLocation() is called
    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "location services connected");
        // Create the LocationRequest object
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // go to onRequestPermissionsResult to handle if the person says to allow or not
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
            Log.v(TAG, "asking for permission");
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.v(TAG, "last loc = true, getting new location");
                // if there is a last location
                onLocationChanged(mLastLocation);
            } else {
                // not a last location, so ask for one
                Log.v(TAG, "udpate location");
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }
        }
    }

    // load in yelp api results
    public void loadAPIResults() {
        Log.v(TAG, "loading api results");
        // loads the different api calls
        getLoaderManager().restartLoader(BOOKS_ID, null, PhoneCapability.this);
        getLoaderManager().restartLoader(MUSIC_ID, null, PhoneCapability.this);
        getLoaderManager().restartLoader(MOVIES_ID, null, PhoneCapability.this);
    }

    // whenever the location of the phone changes, this method is called
    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "location changed!! " + location.toString());
        mLastLocation = location;
        handleNewLocation();

    }

    // updates yelp api calls
    private void handleNewLocation() {
        Log.v(TAG, "handling new location " + mLastLocation);

        // empty old locations and get the new results
        mapOfNearbyBusinesses = new HashMap<>();

        if (mLastLocation != null) {
            latitude = (float) mLastLocation.getLatitude();
            longitude = (float) mLastLocation.getLongitude();
            loadAPIResults();

            Log.v(TAG, "location = " + latitude + " " + longitude);
        }

    }


    //************LOCATION STUFF, PERMISSIONS AND CONNECTIONS************//
    // if the permission is granted, then try getting the last location
    // else the permission is dendied.
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.v(TAG, "permission granted");
                    onConnected(null);
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Log.v(TAG, "permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }

    // the connection was suspended
    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "Location services suspended");
    }

    // if the connection fails, try to resolve the error
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    //***********GOOGLE CLIENT STUFF FOR LOCATION********//
    // connects the google client when the app starts
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // stops the google client when the app ends
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
