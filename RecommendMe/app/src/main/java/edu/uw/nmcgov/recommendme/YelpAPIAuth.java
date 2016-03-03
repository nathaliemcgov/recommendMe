package edu.uw.nmcgov.recommendme;

/**
 * Created by madis on 3/3/2016.
 */
/**
 * This class is used primarily for API keys and secrets to create a central repository.
 * This is useful for having one place to manage several API keys.
 *
 * @author ograycoding.wordpress.com
 */
public class YelpAPIAuth {

    private final String YELP_CONSUMER_KEY ="uMea7WzCEhGKmKpTNbAllQ";
    private final String YELP_CONSUMER_SECRET = "25mfVMm0Kf14GXjfMXsHsJbx68g";
    private final String YELP_TOKEN = "BrC365x0a0YlUJC-ZGW93fkQyM7Rch8J";
    private final String YELP_TOKEN_SECRET = "jkTNKstNuSHV3sj8u4-OSwwhJTU";


    public String getYelpConsumerKey(){
        return YELP_CONSUMER_KEY;
    }

    public String getYelpConsumerSecret(){
        return YELP_CONSUMER_SECRET;
    }

    public String getYelpToken(){
        return YELP_TOKEN;
    }

    public String getYelpTokenSecret(){
        return YELP_TOKEN_SECRET;
    }

}