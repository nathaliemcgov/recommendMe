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
        private OAuthService service;
        private Token accessToken;

        public Yelp(Context context) {
            super(context);
        }

        @Override
        public String loadInBackground() {
            YelpAPIAuth api_keys = new YelpAPIAuth();

            String consumerKey = api_keys.getYelpConsumerKey();
            String consumerSecret = api_keys.getYelpConsumerSecret();
            String token = api_keys.getYelpToken();
            String tokenSecret = api_keys.getYelpTokenSecret();

            this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
            this.accessToken = new Token(token, tokenSecret);


            OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
            String term = "burrito";
            float latitude = 47.6097f;
            float longitude = -122.3331f;
            request.addQuerystringParameter("term", term);
            request.addQuerystringParameter("ll", latitude + "," + longitude);
            this.service.signRequest(this.accessToken, request);
            Response response = request.send();
            String s = response.toString();
            Log.v("YELP", s);
           return response.getBody();
        }
    }
