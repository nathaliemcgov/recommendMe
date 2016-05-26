package edu.uw.nmcgov.recommendme;

import java.util.Map;
import java.util.Objects;

/**
 * Created by iguest on 3/3/16.
 */
public class UserObject {
    public String name;
    private int password;
    public Map<String, Object> liked;
    public Map<String, Object> disliked;

    public UserObject() {}

    public String getName() {
        return name;
    }

    public int getPassword() { return password; }

    public Map<String, Object> getLiked() {
            return liked;
        }

    public Map<String, Object> getDisliked() {
        return disliked;
    }
}
