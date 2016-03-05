package edu.uw.nmcgov.recommendme;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuth10aServiceImpl;
import org.scribe.oauth.OAuthService;

/**
 * Created by madis on 3/3/2016.
 */
public class Yelp extends AsyncTaskLoader<String> {

    private static final String TAG = "YELP";

    private OAuthService service;
    private Token accessToken;


    private String searchTerm; // what's going to be searched for on yelp
    private float lat;  // current latitude
    private float lng; // current longitude


    // if there aren't search terms for some reason
    public Yelp(Context context) {
        super(context);
        Log.v(TAG, "blank constructor");
    }

    // initializes the search parameters
    public Yelp(Context context, String searchTerm, float lat, float lng) {
        super(context);
        this.searchTerm = searchTerm;
        this.lat = lat;
        this.lng = lng;
        Log.v(TAG, "constructor");
    }

    // forces loadInBackground to run. If not implemented, it will never load
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        Log.v(TAG, "loading yelp");
        if (searchTerm == null) {
            return null;
        }
        YelpAPIAuth api_keys = new YelpAPIAuth();

        // these are kept secret
        String consumerKey = api_keys.getYelpConsumerKey();
        String consumerSecret = api_keys.getYelpConsumerSecret();
        String token = api_keys.getYelpToken();
        String tokenSecret = api_keys.getYelpTokenSecret();

        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);


        // uses OAuth to query yelp
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", searchTerm);
        request.addQuerystringParameter("limit", "2");
        request.addQuerystringParameter("ll", lat + "," + lng);
        //request.addQuerystringParameter("bounds", );

        Log.v(TAG, request.toString());

        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        String s = response.toString();
        Log.v(TAG, s);
        return response.getBody();

    }
}
