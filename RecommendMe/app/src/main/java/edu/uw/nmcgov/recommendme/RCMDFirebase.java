package edu.uw.nmcgov.recommendme;

import android.util.Log;
import android.widget.EditText;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
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
            Log.v("LIKEBUG", "Inside create new object" + movieOne.toString() + movieTwo.toString());
            //Pushes a new link from MovieOne to MovieTwo
            Firebase newPostRef = myFirebaseMoviesRef.push();
            newPostRef.child("name").setValue(movieOne);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put(movieTwo, 1);
            newPostRef.child("related").setValue(map);
        } else { //Create or add a relationship for movieOne and movieTwo if movieOne exists
            Log.v("movieOne", dataSnapshot.getValue().toString());
            for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                MediaObject object = singleObject.getValue(MediaObject.class);
                Map<String, Integer> map = object.getRelated();
                if(map == null)
                    map = new HashMap<String, Integer>();
                if (map.get(movieTwo) != null)
                    map.put(movieTwo, map.get(movieTwo) + 1);
                else
                    map.put(movieTwo, 1);
                Firebase postRef = singleObject.getRef();
                postRef.child("related").setValue(map);
            }

        }
    }


    public void createUser(Map<String, String> map) {
        Firebase userRef = myFirebaseUserRef.push();
        userRef.setValue(map);
    }


    public void setLike(final String liked, String user) {
        //Get user
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                    UserObject object = singleObject.getValue(UserObject.class);
                    //If user hasn't liked anything yet, create the liked map
                    Map<String, Boolean> userLikes = object.getLiked();
                    if(userLikes == null) {
                        userLikes = new HashMap<String, Boolean>();
                    } else { //Update everything in the map to have a relationship to the new object
                        //Create the media object in firebase

                        Firebase newPostRef = myFirebaseMoviesRef.push();
                        newPostRef.child("name").setValue(liked);

                        for(String key : userLikes.keySet()) {
                            Log.v("Liked", key);
                            createConnection(liked, key);

                        }
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
