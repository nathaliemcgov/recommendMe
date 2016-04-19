package edu.uw.nmcgov.recommendme;

/**
 * Created by iguest on 3/8/16.
 */
public class RelatedObject implements Comparable<RelatedObject> {

    public String name;
    public int likes;
    public int totalLikes;
    public double ratio; //Ratio of likes to totalLikes

    public RelatedObject(String name) {
        this.name = name;
        this.likes = 0;
        this.totalLikes = 0;
        this.ratio = 0;
    }

    public RelatedObject(String name, int likes, int totalLikes) {
        this.name = name;
        this.likes = likes;
        this.totalLikes = totalLikes;
        this.ratio = likes * 1.0 / totalLikes;
    }

    @Override
    public int compareTo(RelatedObject another) {
        if(ratio > another.ratio) {
            return -1;
        } else if( ratio < another.ratio) {
            return 1;
        } else {
            return name.compareTo(another.name);
        }
    }

    public String toString() {
        return name;
    }

    public double getRatio() {
        return ratio;
    }
}