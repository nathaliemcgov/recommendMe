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
import java.util.Objects;
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


    //Creates a connection in firebase between the two objects
    //Calls the pushToFirebase method
    private boolean createConnection(String one, String two) {
        final String mediaOne = one.trim().toLowerCase();
        final String mediaTwo = two.trim().toLowerCase();

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
                Map<String, Object> map = object.getRelated();
                if(map == null)
                    map = new HashMap<String, Object>();
                if (map.get(movieTwo) != null) {
                    Log.v("modest mouse bug", map.toString() + ' ' + object.getName() + movieOne + movieTwo);
                    map.put(movieTwo, Integer.parseInt(map.get(movieTwo).toString()) + 1);
                    Log.v("modest", map.toString() + movieOne + movieTwo);
                    postRef.child("related").updateChildren(map);
                } else {
                    Log.v("modest mouse bugCHECK", map.toString() + ' ' + object.getName() + movieOne + movieTwo);
                    Map<String, Object> newMap = new HashMap<String, Object>();
                    newMap.put(movieTwo, 1);
                    postRef.child("related").updateChildren(newMap);
                }
                //postRef.child("totalUserLikes").setValue(object.getTotalUserLikes() + 1);
            }
        }
    }

    //Creates a user given the map
    //if map is "name" -> "tyler", "email" -> "tylerj11@uw.edu", firebase reflects this
    public void createUser(Map<String, String> map) {
        Firebase userRef = myFirebaseUserRef.push();
        userRef.setValue(map);
    }

    //Given a title, list, and adapter that MUST be connected to that list, will
    //query and sort related titles based on relevance
    public void queryTitle(String title, final List<RelatedObject> titleArray, final CustomTileAdapter adapter) {
        Query userQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(title.trim().toLowerCase());
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
                            titleArray.add(related);
                        }


                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //Used if you want to set more than one like at once
    //String user must be the username for now
    //Adds likes to a user. Will also update connections to other objects
    public void setManyLikes(List<String> toLike, String user) {
        setManyLikes(toLike, user, 0);
    }

    //Used by the public set many likes method. Recursive :)
    private void setManyLikes(final List<String> toLike, final String user, final int pos) {
        if(pos < toLike.size()) {
            Log.v("tag", toLike.get(pos) + ' ' + user);
            final String liked = toLike.get(pos).toLowerCase();
            Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) { //Theoretically one loop
                        UserObject object = singleObject.getValue(UserObject.class);
                        //If user hasn't liked anything yet, create the liked map
                        Map<String, Object> userLikes = object.getLiked();
                        if (userLikes == null) {
                            userLikes = new HashMap<String, Object>();
                        } else { //Update everything in the map to have a relationship to the new object
                            Query userQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(liked);
                            final Map<String, Object> finalUserLikes = userLikes;
                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //Create the media object in firebase
                                    //If movie doesn't exist
                                    if (dataSnapshot.getValue() == null) {
                                        Log.v("DOUBLETOYSTORYERRORIF", user + " " + liked);
                                        Firebase newPostRef = myFirebaseMoviesRef.push();
                                        newPostRef.child("name").setValue(liked);
                                        newPostRef.child("totalUserLikes").setValue(1);
                                    } else { //If movie does exist
                                        Log.v("DOUBLETOYSTORYERRORELSE", user + " " + liked);
                                        for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                                            MediaObject object = singleObject.getValue(MediaObject.class);
                                            int totalUserLikes = object.getTotalUserLikes();
                                            Firebase ref = singleObject.getRef();
                                            ref.child("totalUserLikes").setValue(1 + totalUserLikes);
                                        }
                                    }

                                    for (String key : finalUserLikes.keySet()) {
                                        if (!liked.equals(key))
                                            createConnection(liked, key);
                                    }
                                }

                                @Override
                                public void onCancelled(FirebaseError firebaseError) {

                                }
                            });
                        }

                        userLikes.put(liked, true);
                        Firebase postRef = singleObject.getRef();
                        if (userLikes.size() == 1)
                            postRef.child("liked").updateChildren(userLikes, new Firebase.CompletionListener() {

                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    setManyLikes(toLike, user, pos + 1);
                                }
                            });
                        else
                            postRef.child("liked").setValue(userLikes, new Firebase.CompletionListener() {

                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    setManyLikes(toLike, user, pos + 1);
                                }
                            });
                    }

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    //Sets a single like given a username
    public void setLike(String likedUnformatted, String user) {
        //Get user
        final String liked = likedUnformatted.toLowerCase();
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleObject : dataSnapshot.getChildren()) { //Theoretically one loop
                    UserObject object = singleObject.getValue(UserObject.class);
                    //If user hasn't liked anything yet, create the liked map
                    Map<String, Object> userLikes = object.getLiked();
                    if(userLikes == null) {
                        userLikes = new HashMap<String, Object>();
                    } else { //Update everything in the map to have a relationship to the new object
                        Query userQuery = myFirebaseMoviesRef.orderByChild("name").equalTo(liked);
                        final Map<String, Object> finalUserLikes = userLikes;
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
                    if(userLikes.size() == 1)
                        postRef.child("liked").updateChildren(userLikes);
                    else
                        postRef.child("liked").setValue(userLikes);
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    //Given a user (name), list, and adapter that MUST be connected to that list, will
    //fill up the list/adapter with sorted recomendations for that user.
    public void recommendationsForUser(String user, final List<RelatedObject> list,
                                       final CustomTileAdapter adapter) {
        final Map<String, RelatedObject> overAllMap = new HashMap<String, RelatedObject>();
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        final Map<String, Object> userLikes = object.getLiked();

                        for(String liked: userLikes.keySet()) {
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
                                                if(!userLikes.containsKey(key)) {
                                                    if (overAllMap.containsKey(key)) {
                                                        overAllMap.get(key).ratio += Integer.parseInt(map.get(key).toString()) / totalLikes;
                                                    } else {
                                                        RelatedObject related = new RelatedObject(key, Integer.parseInt(map.get(key).toString()), totalLikes);
                                                        overAllMap.put(key, related);
                                                        list.add(related);
                                                    }
                                                }
                                            }
                                            Collections.sort(list);
                                            adapter.notifyDataSetChanged();
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
