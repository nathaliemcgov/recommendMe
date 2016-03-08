package edu.uw.nmcgov.recommendme;

import android.provider.MediaStore;

import java.util.Map;

/**
 * Created by iguest on 3/3/16.
 */
public class MediaObject {
    private String name;
    private Map<String, Object> related;
    private int totalUserLikes;

    public MediaObject() {

    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getRelated() {
        return related;
    }

    public int getTotalUserLikes() { return totalUserLikes; }

    public String toString() {
        String map = null;
        if(related != null)
            map = related.toString();
        return name + " " + map;
    }

}
