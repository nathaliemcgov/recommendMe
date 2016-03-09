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
import java.util.Set;
import java.util.TreeSet;

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
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        MediaObject object = singleObject.getValue(MediaObject.class);
                        Map<String, Object> map = object.getRelated();
                        Set<RelatedObject> relatedObjects = new TreeSet<RelatedObject>();

                        for (String key : map.keySet()) {
                            relatedObjects.add(new RelatedObject(key, Integer.parseInt(map.get(key).toString()), object.getTotalUserLikes()));
                        }

                        for (RelatedObject related : relatedObjects) {
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


    public void recommendationsForUser(String user, final ArrayAdapter<RelatedObject> array, final List<RelatedObject> list) {
        final Map<String, RelatedObject> overAllMap = new HashMap<String, RelatedObject>();
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);

                        for(String liked: object.getLiked().keySet()) {
                            Query singleMediaQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(liked);
                            singleMediaQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                                            MediaObject object = singleObject.getValue(MediaObject.class);
                                            Map<String, Object> map = object.getRelated();
                                            int totalLikes = object.getTotalUserLikes();
                                            Set<RelatedObject> relatedObjects = new TreeSet<RelatedObject>();
                                            for(String key : map.keySet()) {
                                                if(map.containsKey(key)) {
                                                    overAllMap.get(key).ratio += Integer.parseInt(map.get(key).toString()) / totalLikes;
                                                } else {
                                                    RelatedObject related = new RelatedObject(key, Integer.parseInt(map.get(key).toString()), totalLikes);
                                                    overAllMap.put(key, related);
                                                    list.add(related);
                                                }
                                            }
                                            Collections.sort(list);
                                            array.notifyDataSetChanged();
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
