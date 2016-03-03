package edu.uw.nmcgov.recommendme;

import android.provider.MediaStore;

import java.util.Map;

/**
 * Created by iguest on 3/3/16.
 */
public class MediaObject {
    private String name;
    private Map<String, Integer> related;

    public MediaObject() {

    }

    public String getName() {
        return name;
    }

    public Map<String, Integer> getRelated() {
        return related;
    }

}
