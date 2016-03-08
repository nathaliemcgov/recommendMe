package edu.uw.nmcgov.recommendme;

import java.util.Map;

/**
 * Created by iguest on 3/3/16.
 */
public class UserObject {
    private String name;
    private Map<String, Boolean> liked;

    public UserObject() {

    }

    public String getName() {
        return name;
    }

    public Map<String, Boolean> getLiked() {
            return liked;
        }
}
