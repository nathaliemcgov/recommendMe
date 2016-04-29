package edu.uw.nmcgov.recommendme;

import java.util.Map;
import java.util.Objects;

/**
 * Created by iguest on 3/3/16.
 */
public class UserObject {
    private String name;
    private Map<String, Object> liked;
    private Map<String, Object> disliked;

    public UserObject() {}

    public String getName() {
        return name;
    }

    public Map<String, Object> getLiked() {
            return liked;
        }

    public Map<String, Object> getDisliked() {
        return disliked;
    }
}
