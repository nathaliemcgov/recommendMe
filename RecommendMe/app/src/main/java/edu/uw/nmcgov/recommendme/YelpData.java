package edu.uw.nmcgov.recommendme;

import android.util.Log;

import org.json.JSONObject;

import java.util.Dictionary;

/**
 * Created by madis on 3/6/2016.
 */
public class YelpData {

    private String name;
    private String mobileUrl;
    private double rating;
    private String phoneNumber;

    public YelpData(JSONObject business) {
        try {
            name = business.getString("name");
            rating = business.getDouble("rating");
            mobileUrl = business.getString("mobile_url");
            phoneNumber = business.getString("phone");
        } catch (Exception ex) {
            Log.v("YELP DATA", ex.getMessage());
        }
    }

    public double getRating() {
        return rating;
    }

    public String getMobileUrl() {
        return mobileUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "Rating " + rating + ", Phone: " + phoneNumber;
    }
}
