package edu.uw.nmcgov.recommendme;

import android.provider.MediaStore;

import java.util.Map;

/**
 * Created by iguest on 3/3/16.
 * Media object for objects from firebase
 */
public class MediaObject {
    private String name; // Name of object
    private Map<String, Object> related; //If a user likes object A, B, and C, and this is A
                                         //related is a map to every other letter. The value is
                                         //an integer which represents the number of links
    private int totalUserLikes;//How many users like this object

    public MediaObject() {

    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getRelated() {
        return related;
    }

    public int getTotalUserLikes() { return totalUserLikes; }

    //Right now, just the name and the map to related objects
    public String toString() {
        String map = null;
        if(related != null)
            map = related.toString();
        return name + " " + map;
    }

}
