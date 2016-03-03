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

    final Firebase myFirebaseRef = new Firebase("https://rcmd.firebaseio.com/Movies");

    public RCMDFirebase() {

    }

    public boolean createConnection(String one, String two) {
        final String movieOne = one.trim();
        final String movieTwo = two.trim();

        final Query movieQuery1 = myFirebaseRef.orderByChild("name").equalTo(movieOne);
        final Query movieQuery2 = myFirebaseRef.orderByChild("name").equalTo(movieTwo);

        if (movieOne.length() > 0 && movieTwo.length() > 0) {
            movieQuery1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pushToFirebase(dataSnapshot, movieTwo, movieOne);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });

            movieQuery2.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    pushToFirebase(dataSnapshot, movieOne, movieTwo);

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
            Firebase newPostRef = myFirebaseRef.push();
            newPostRef.child("name").setValue(movieOne);
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put(movieTwo, 1);
            newPostRef.child("related").setValue(map);
        } else { //Create or add a relationship for movieOne and movieTwo if movieOne exists
            Log.v("movieOne", dataSnapshot.getValue().toString());
            for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                MediaObject object = singleObject.getValue(MediaObject.class);
                Map<String, Integer> map = object.getRelated();
                if (map.get(movieTwo) != null)
                    map.put(movieTwo, map.get(movieTwo) + 1);
                else
                    map.put(movieTwo, 1);
                Firebase postRef = singleObject.getRef();
                postRef.child("related").setValue(map);
            }

        }
    }
}
