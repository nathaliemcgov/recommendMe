package edu.uw.nmcgov.recommendme;

import android.provider.MediaStore;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iguest on 3/3/16.
 */
public class RCMDFirebase {

    final Firebase myFirebaseMoviesRef = new Firebase("https://rcmd.firebaseio.com/Movies");
    final Firebase myFirebaseUserRef = new Firebase("https://rcmd.firebaseio.com/Users");

    public RCMDFirebase() {

    }

    public boolean createConnection(String one, String two) {
        final String mediaOne = one.trim();
        final String mediaTwo = two.trim();

        final Query mediaQuery1 = myFirebaseMoviesRef.orderByChild("name").equalTo(mediaOne);
        final Query mediaQuery2 = myFirebaseMoviesRef.orderByChild("name").equalTo(mediaTwo);

        if (mediaOne.length() > 0 && mediaTwo.length() > 0) {
            mediaQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pushToFirebase(dataSnapshot, mediaOne, mediaTwo);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            mediaQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pushToFirebase(dataSnapshot, mediaTwo, mediaOne);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
        return true;
    }

    //Creates/updates the given objects in firebase, based on the snapshot from the query
    private void pushToFirebase(DataSnapshot dataSnapshot, String movieOne, String movieTwo) {
        //Create a new movie object if the movieTwo doesn't exist, and create a relationship to movie one
        if (dataSnapshot.getValue() == null) {
            //Pushes a new link from MovieOne to MovieTwo
            Firebase newPostRef = myFirebaseMoviesRef.push();
            newPostRef.child("name").setValue(movieOne);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put(movieTwo, 1);
            newPostRef.child("related").setValue(map);
            newPostRef.child("totalUserLikes").setValue(1);
        } else { //Create or add a relationship for movieOne and movieTwo if movieOne exists
            int i = 0;
            for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                Firebase postRef = singleObject.getRef();
                i++;
                MediaObject object = singleObject.getValue(MediaObject.class);
                Log.v("jkljas", object.toString());
                Map<String, Object> map = object.getRelated();
                if(map == null)
                    map = new HashMap<String, Object>();
                if (map.get(movieTwo) != null) {
                    map.put(movieTwo, map.get(Integer.parseInt(movieTwo) + 1));
                    postRef.child("related").updateChildren(map);
                } else {
                    map.put(movieTwo, 1);
                    postRef.child("related").updateChildren(map);
                }
                Log.v("TAG", map.toString() + " " + movieOne + " " + movieTwo + " " + i);
                //postRef.child("totalUserLikes").setValue(object.getTotalUserLikes() + 1);
            }
        }
    }


    public void createUser(Map<String, String> map) {
        Firebase userRef = myFirebaseUserRef.push();
        userRef.setValue(map);
    }

    public void queryTitle(String title, final ArrayAdapter<String> array) {
        Query userQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(title);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        MediaObject object = singleObject.getValue(MediaObject.class);
                        Map<String, Object> map = object.getRelated();
                        List<RelatedObject> list = new ArrayList<RelatedObject>();

                        for (String key : map.keySet()) {
                            list.add(new RelatedObject(key, Integer.parseInt(map.get(key).toString()), object.getTotalUserLikes()));
                        }
                        Collections.sort(list);
                        Log.v("sorted", list.toString());

                        for (RelatedObject related : list) {
                            array.add(related.name);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private class RelatedObject implements Comparable<RelatedObject> {

        private String name;
        private int likes;
        private int totalLikes;

        public RelatedObject(String name, int likes, int totalLikes) {
            this.name = name;
            this.likes = likes;
            this.totalLikes = totalLikes;
        }

        @Override
        public int compareTo(RelatedObject another) {
            Log.v("tag", "here");
            double thisPercentage = this.likes * 1.0 / totalLikes;
            double otherPercentage = another.likes * 1.0 / another.totalLikes;
            if(thisPercentage > otherPercentage) {
                return -1;
            } else if( thisPercentage < otherPercentage) {
                return 1;
            } else {
                return 0;
            }
        }

        public String toString() {
            return name;
        }
    }



    public void setLike(final String liked, String user) {
        //Get user
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleObject : dataSnapshot.getChildren()) { //Theoretically one loop
                    UserObject object = singleObject.getValue(UserObject.class);
                    //If user hasn't liked anything yet, create the liked map
                    Map<String, Boolean> userLikes = object.getLiked();
                    if(userLikes == null) {
                        userLikes = new HashMap<String, Boolean>();
                    } else { //Update everything in the map to have a relationship to the new object
                        Query userQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(liked);
                        final Map<String, Boolean> finalUserLikes = userLikes;
                        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Create the media object in firebase
                                //If movie doesn't exist
                                if(dataSnapshot.getValue() == null) {
                                    Firebase newPostRef = myFirebaseMoviesRef.push();
                                    newPostRef.child("name").setValue(liked);
                                    newPostRef.child("totalUserLikes").setValue(1);
                                } else { //If movie does exist
                                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                                        MediaObject object = singleObject.getValue(MediaObject.class);
                                        int totalUserLikes = object.getTotalUserLikes();
                                        Firebase ref = singleObject.getRef();
                                        ref.child("totalUserLikes").setValue(1 + totalUserLikes);
                                    }
                                }

                                for(String key : finalUserLikes.keySet()) {
                                    if(!liked.equals(key))
                                        createConnection(liked, key);

                                    //It looks like what I'm going to have to do here is
                                    //get a big list of all the connections, then set up the map connections
                                    //so that I don't have to create a lot of little connections.
                                    //

                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    userLikes.put(liked, true);
                    Firebase postRef = singleObject.getRef();
                    postRef.child("liked").setValue(userLikes);


                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
