package edu.uw.nmcgov.recommendme;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PhoneCapability extends Fragment
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MAIN";


    // constants for the location services
    private static final int MY_PERMISSIONS_REQUEST = 1;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // location stuff
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;


    // used to store nearby businesses
    public Map<String, List<JSONObject>> mapOfNearbyBusinesses;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setContentView(R.layout.phone_capability_buttons);

        View rootView = inflater.inflate(R.layout.fragment_media_details, container, false);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mapOfNearbyBusinesses = new HashMap<String, List<JSONObject>>();

        // button for sending out an intent for music
        Button musicButtonIntent = (Button) getActivity().findViewById(R.id.goToMusic);
        musicButtonIntent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToMusic();
            }
        });

        // button for sending out the netflix intent
        Button netflixButtonIntent = (Button) getActivity().findViewById(R.id.goToMusic);
        netflixButtonIntent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                goToNetflix();
            }
        });

        // listening for the bookstore button click. Starts API call to yelp for bookstores
        Button bookstoreButton = (Button) getActivity().findViewById(R.id.findBooks);
        bookstoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Log.v(TAG, "bookstore button clicked");

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Log.v(TAG, "loading yelp");
                        return getYelpResults("books");
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        onLoadFinished("books " + result);

                        getNearbyBusinesses("books");
                    }
                }.execute();

            }
        });

        // listening for the music button click. Starts API call to yelp for music
        Button musicButton = (Button) getActivity().findViewById(R.id.findMusic);
        musicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.v(TAG, "music store button clicked");
                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Log.v(TAG, "loading yelp");
                        return getYelpResults("music");
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        onLoadFinished("music " + result);

                        getNearbyBusinesses("music");
                    }
                }.execute();
            }
        });

        // listening for the movie button click. Starts API call to yelp for movie theaters
        Button movieButton = (Button) getActivity().findViewById(R.id.findMovies);
        movieButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v(TAG, "movie theater button clicked");

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... params) {
                        Log.v(TAG, "loading yelp");
                       return getYelpResults("movies");
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        onLoadFinished(result);

                        getNearbyBusinesses("movies");
                    }
                }.execute();
            }
        });
        return rootView;
    }

    // get the yelp results
    private String getYelpResults(String searchTerm) {
        YelpAPIAuth api_keys = new YelpAPIAuth();
        // these are kept secret
        String consumerKey = api_keys.getYelpConsumerKey();
        String consumerSecret = api_keys.getYelpConsumerSecret();
        String token = api_keys.getYelpToken();
        String tokenSecret = api_keys.getYelpTokenSecret();

        OAuthService service = new ServiceBuilder().provider(YelpApi2.class)
                .apiKey(consumerKey).apiSecret(consumerSecret).build();
        Token accessToken = new Token(token, tokenSecret);

        // uses OAuth to query yelp
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", searchTerm);
        request.addQuerystringParameter("ll", (float) mLastLocation.getLatitude() + "," + (float) mLastLocation.getLongitude());
        request.addQuerystringParameter("limit", "2");

        service.signRequest(accessToken, request);
        Response response = request.send();
        return searchTerm + " " + response.getBody();
    }

    // pass in either 'books', 'music', or 'movies', to search for
    // nearby businesses that relate to these terms
    public void getNearbyBusinesses(String businessType) {
        if (mLastLocation == null) {
            Log.v(TAG, "there's no location, so get one");
            onConnected(null);
        }

        Log.v(TAG, "displaying results into the UI");
        try {

            //********** HARD CODED BUSINESS DISPLAY**************//
            List<JSONObject> list = mapOfNearbyBusinesses.get(businessType);

            // business one
            JSONObject keyToBusinessOne = list.get(0);
            YelpData businessData = new YelpData(keyToBusinessOne);
            Log.v(TAG, businessData.toString());

            TextView businessOneName = (TextView) getActivity().findViewById(R.id.businessNameOne);
            businessOneName.setClickable(true);
            businessOneName.setMovementMethod(LinkMovementMethod.getInstance());
            String text = "<a href='" + businessData.getMobileUrl() + "'>" + businessData.getName() + "</a>";
            businessOneName.setText(Html.fromHtml(text));
            //businessOneName.setText(businessData.getName());

            TextView businessOneInfo = (TextView) getActivity().findViewById(R.id.businessInfoOne);
            businessOneInfo.setText(businessData.toString());

            // business two
            JSONObject keyToBusinessTwo = list.get(1);
            YelpData businessDataTwo = new YelpData(keyToBusinessTwo);
            Log.v(TAG, businessDataTwo.toString());

            TextView businessTwoName = (TextView) getActivity().findViewById(R.id.businessNameTwo);
            businessTwoName.setClickable(true);
            businessTwoName.setMovementMethod(LinkMovementMethod.getInstance());
            String textTwo = "<a href='" + businessDataTwo.getMobileUrl() + "'>" + businessDataTwo.getName() + "</a>";
            businessTwoName.setText(Html.fromHtml(textTwo));
            //businessOneName.setText(businessData.getName());

            TextView businessTwoInfo = (TextView) getActivity().findViewById(R.id.businessInfoTwo);
            businessTwoInfo.setText(businessDataTwo.toString());
        } catch (Exception ex) {
            Log.v(TAG, ex.getMessage());
        }
    }


    // once the onCreateLoader is done, onLoadFinished is called to handle the data returned
    public void onLoadFinished(String dataHacked) {
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


    // set an alert to ask them to enable their gps
    public void turnOnLocation() {

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setMessage(R.string.enable_gps_message);
            dialog.setPositiveButton(R.string.enable, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // sends them to enable their location.
                    // they have to press "back" on their phone to get back to their location
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);

                    // get gps
                }
            });
            dialog.setNegativeButton(R.string.enable_denied, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                }
            });
            dialog.show();
        }
    }


    // intent shows the options the user can go to in order to listen to music
    // currently if they click on a button in the ui, different options will pop up
    public void goToMusic() {
        Log.v(TAG, "sending spotify intent");

        EditText movieEntered = (EditText) getActivity().findViewById(R.id.musicSearchText);
        String artist = movieEntered.getText().toString();


        Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        //intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, "vnd.android.cursor.item/*");
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        intent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        intent.putExtra(SearchManager.QUERY, artist);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    // currently if they press a button in the ui, then they will go to netflix for a show that doesn't exist
    public void goToNetflix() {
        Log.v(TAG, "sending netflix intent");


        EditText movieEntered = (EditText) getActivity().findViewById(R.id.movieSearchText);
        String movieTitle = movieEntered.getText().toString();

        String watchUrl = "http://www.netflix.com/watch/" + movieTitle;

        try {
            Intent intent = new Intent(Intent.ACTION_SEARCH);
            intent.setClassName("com.netflix.mediaclient", "com.netflix.mediaclient.ui.search.SearchActivity");
            intent.putExtra("query", movieTitle);
            startActivity(intent);
        } catch(Exception ex) {
            // netflix app isn't installed, send to website instead.
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(watchUrl));
            startActivity(intent);
            Toast.makeText(getActivity(), "Please install the Netflix App!", Toast.LENGTH_SHORT).show();
        }
    }

    //**********ACCESSING LOCATION STUFF***********//
    // requests permission to get the last location.
    @Override
    public void onConnected(Bundle bundle) {

        Log.v(TAG, "location services connected");
        // Create the LocationRequest object
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // go to onRequestPermissionsResult to handle if the person says to allow or not
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST);
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


    // whenever the location of the phone changes, this method is called
    @Override
    public void onLocationChanged(Location location) {
        Log.v(TAG, "location changed!! " + location.toString());
        mLastLocation = location;
        mapOfNearbyBusinesses = new HashMap<>();
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
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }


    //***********GOOGLE CLIENT STUFF FOR LOCATION********//
    // connects the google client when the app starts
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    // stops the google client when the app ends
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

}
