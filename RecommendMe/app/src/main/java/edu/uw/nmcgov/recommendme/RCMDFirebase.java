package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.client.core.Context;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by iguest on 3/3/16.
 */
public class RCMDFirebase {

    private static final String TAG = "FIREBASE";
    final Firebase myFirebaseMoviesRef = new Firebase("https://rcmd.firebaseio.com/Movies");
    final Firebase myFirebaseUserRef = new Firebase("https://rcmd.firebaseio.com/Users");

    public RCMDFirebase() {

    }

    // Creates a connection in firebase between the two objects
    // Calls the pushToFirebase method
    private boolean createConnection(String one, String two) {
        final String mediaOne = makeStringFirebaseSafe(one.trim());
        final String mediaTwo = makeStringFirebaseSafe(two.trim());

        final Query mediaQuery1 = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(mediaOne.toLowerCase());
        final Query mediaQuery2 = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(mediaTwo.toLowerCase());

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
            newPostRef.child("nameLowerCase").setValue(movieOne.toLowerCase());
            Map<String, Integer> map = new HashMap<String, Integer>();
            map.put(movieTwo, 1);
            newPostRef.child("related").setValue(map);
            newPostRef.child("totalUserLikes").setValue(1);
        } else { //Create or add a relationship for movieOne and movieTwo if movieOne exists
            int i = 0;
            for (DataSnapshot singleObject : dataSnapshot.getChildren()) { // this should really only loop once
                final Firebase postRef = singleObject.getRef();
                i++;
                MediaObject object = singleObject.getValue(MediaObject.class);
                Map<String, Object> map = object.getRelated();
                Log.v(TAG, movieOne + " " + movieTwo);
                if(map == null)
                    map = new HashMap<String, Object>();
                if (map.get(movieTwo) != null) {
                    Log.v(TAG, "inside if");
                    map.put(movieTwo, Integer.parseInt(map.get(movieTwo).toString()) + 1);
                    postRef.child("related").updateChildren(map);
                } else {
                    Log.v(TAG, "inside else");
                    //Search for upper case name of movieTwo
                    Query mediaQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(movieTwo.toLowerCase());
                    mediaQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot != null) {
                                for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                                    MediaObject media = singleObject.getValue(MediaObject.class);
                                    Map<String, Object> newMap = new HashMap<String, Object>();
                                    newMap.put(media.getName(), 1);
                                    postRef.child("related").updateChildren(newMap);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
                //postRef.child("totalUserLikes").setValue(object.getTotalUserLikes() + 1);
            }
        }
    }

    //Creates a user given the map
    //if map is "name" -> "tyler", "email" -> "tylerj11@uw.edu", firebase reflects this
    public void createUser(Map<String, Object> map) {
        Log.v("USER", "created user!");
        map.put("name", makeStringFirebaseSafe(map.get("name").toString().trim().toLowerCase()));
        Firebase userRef = myFirebaseUserRef.push();
        userRef.setValue(map);
    }

    //Checks firebase for the given password for the given user
    //First callback is for successful password for user
    //Second is if the username exists but password fails
    //Third is if the username doesn't exist
    public void checkPass(String user, final int password, final Firebase.CompletionListener complete,
                          final Firebase.CompletionListener failPass, final Firebase.CompletionListener failUser) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());

        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    if(dataSnapshot.getValue() == null) failUser.onComplete(null, null);
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject userObject = singleObject.getValue(UserObject.class);
                        if(userObject.getPassword() == password)
                            complete.onComplete(null, null);
                        else
                            failPass.onComplete(null, null);
                    }
                } else {
                    failUser.onComplete(null, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void queryTitle(String title, String username, final List<RelatedObject> titleArray, final CustomTileAdapter adapter) {
        List<String> types = new ArrayList<String>();
        username = makeStringFirebaseSafe(username.trim().toLowerCase());
        types.add("movie");
        types.add("music");
        types.add("book");
        queryTitle(title, username, titleArray, adapter, types);
    }

    //Given a title, list, and adapter that MUST be connected to that list, will
    //query and sort related titles based on relevance
    public void queryTitle(String title, String username, final List<RelatedObject> titleArray, final CustomTileAdapter adapter, final List<String> types) {
        String userHold = "";
        if (username != null) userHold = makeStringFirebaseSafe(userHold.trim().toLowerCase());
        final String user = userHold;
        Log.v("Query title", user + " is the user");
        title = title.trim();
        Query userQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(title.trim().toLowerCase());

        final Set<String> dislikedTitles = new HashSet<String>();
        if (!user.equals("")) {
            // Finds the user in firebase
            Query getUser = myFirebaseUserRef.orderByChild("name").equalTo(user);
            getUser.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                            UserObject userObject = singleObject.getValue(UserObject.class);
                            Map<String, Object> map = userObject.getDisliked();
                            if (map == null) map = new HashMap<String, Object>();
                            for (String key : map.keySet()) {
                                dislikedTitles.add(key);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    //this should run once
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        MediaObject object = singleObject.getValue(MediaObject.class);
                        Map<String, Object> map = object.getRelated();
                        Set<RelatedObject> relatedObjects = new TreeSet<RelatedObject>();
                        if(map == null) map = new HashMap<String, Object>();

                        for (String key : map.keySet()) {
                            relatedObjects.add(new RelatedObject(key, Integer.parseInt(map.get(key).toString()), object.getTotalUserLikes()));
                        }

                        for (final RelatedObject related : relatedObjects) {
                            Log.v("relateddd", "" + !dislikedTitles.contains(related.name));
                            if (!dislikedTitles.contains(related.name)) {
//                                Log.v("objects", related.name + " related " + dislikedTitles);
                                Query typeQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(related.name.trim().toLowerCase());
                                typeQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                                                MediaObject object = singleObject.getValue(MediaObject.class);
                                                RelatedObject toAdd = related;
                                                toAdd.type = object.getType();
                                                if(types.contains(object.getType())) {
                                                    Log.v(TAG, related.toString());
                                                    titleArray.add(toAdd);
                                                    adapter.notifyDataSetChanged();
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
    public void setManyLikes(List<String> toLike, String user, String type) {
        Log.v("SETMANY", "SET SET");
        setManyLikes(toLike, makeStringFirebaseSafe(user.trim().toLowerCase()), 0, type, null);
    }

    public void setManyLikes(List<String> toLike, String user, String type, Firebase.CompletionListener complete) {
        setManyLikes(toLike, makeStringFirebaseSafe(user.trim().toLowerCase()), 0, type, complete);
    }

    //Used by the public set many likes method. Recursive :)
    private void setManyLikes(final List<String> toLike, String tempUser, final int pos, final String type, final Firebase.CompletionListener complete) {
        if(pos < toLike.size()) {
            final String liked = makeStringFirebaseSafe(toLike.get(pos));
            final String user = makeStringFirebaseSafe(tempUser.trim().toLowerCase());
            Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) { //Theoretically one loop
                        UserObject object = singleObject.getValue(UserObject.class);
                        //If user hasn't liked anything yet, create the liked map
                        Map<String, Object> userLikes = object.getLiked();
                        if (userLikes == null)
                            userLikes = new HashMap<String, Object>();
                        //Update everything in the map to have a relationship to the new object
                        Query movieQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(liked.toLowerCase());
                        final Map<String, Object> finalUserLikes = userLikes;
                        movieQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                //Create the media object in firebase
                                //If movie doesn't exist
                                if (dataSnapshot.getValue() == null) {
                                    Log.v("DOUBLETOYSTORYERRORIF", user + " " + liked + " " + type);
                                    Firebase newPostRef = myFirebaseMoviesRef.push();
                                    newPostRef.child("name").setValue(liked);
                                    newPostRef.child("nameLowerCase").setValue(liked.toLowerCase());
                                    newPostRef.child("totalUserLikes").setValue(1);
                                    newPostRef.child("type").setValue(type);
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

                        userLikes.put(liked, true);
                        Firebase postRef = singleObject.getRef();
                        if (userLikes.size() == 1)
                            postRef.child("liked").updateChildren(userLikes, new Firebase.CompletionListener() {

                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    setManyLikes(toLike, user, pos + 1, type, complete);
                                }
                            });
                        else {
                            Log.v(TAG, userLikes.toString());
                            postRef.child("liked").setValue(userLikes, new Firebase.CompletionListener() {
                                @Override
                                public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                                    setManyLikes(toLike, user, pos + 1, type, complete);
                                }
                            });
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        } else {
            if(complete != null)
                complete.onComplete(null, null);
        }
    }

    public void setLike(String likedUnformatted, String user) {
        likedUnformatted = makeStringFirebaseSafe(likedUnformatted);
        setLike(likedUnformatted, makeStringFirebaseSafe(user.trim().toLowerCase()), "movie");
    }

    //Sets a single like given a username
    public void setLike(String likedUnformatted, String user, final String type) {
        likedUnformatted = makeStringFirebaseSafe(likedUnformatted).trim();
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        if (user != null && !user.equals("")) {
            Log.v("tag", "tagtagtag");
            //Get user
            final String liked = likedUnformatted;
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
                            Query userQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(liked.toLowerCase());
                            final Map<String, Object> finalUserLikes = userLikes;
                            userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    //Create the media object in firebase
                                    //If movie doesn't exist
                                    if (dataSnapshot.getValue() == null) {
                                        Firebase newPostRef = myFirebaseMoviesRef.push();
                                        newPostRef.child("name").setValue(liked);
                                        newPostRef.child("nameLowerCase").setValue(liked.toLowerCase());
                                        newPostRef.child("totalUserLikes").setValue(1);
                                        newPostRef.child("type").setValue(type);
                                    } else { //If movie does exist
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
                        if (userLikes.size() == 1)
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
    }

    // Given a username - sets a dislike for the selected media title
    public void setDislike(String tempUser, String dislikedTitle) {
        final String user = makeStringFirebaseSafe(tempUser.trim().toLowerCase());
        if (!user.equals("") && user != null) {
            Log.v("FIREBASE", "Reached dislike");
            final String disliked = dislikedTitle;
            Query getUser = myFirebaseUserRef.orderByChild("name").equalTo(user);

            getUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v(TAG, "Inside user outside loop" + user);
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Log.v(TAG, "Inside user");
                        UserObject object = child.getValue(UserObject.class);

                        Map<String, Object> dislikes = object.getDisliked();

                        // If the user has not disliked yet - create a map for dislikes
                        if (dislikes == null) {
                            dislikes = new HashMap<String, Object>();
                        // The user has disliked at least one title
                        } else {
                            final Map<String, Object> userDislikes = dislikes;
                            Query getTitle = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(disliked.toLowerCase());

                            getTitle.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // If title does not exist - create media obj in firebase
                                    if (dataSnapshot.getValue() == null) {
                                        Log.v(TAG, "In dislike, create new");
                                        // Pushes media titles and sets total # of dislikes of title to 1
                                        Firebase newEntryRef = myFirebaseMoviesRef.push();
                                        newEntryRef.child("name").setValue(disliked);
                                        newEntryRef.child("nameLowerCase").setValue(disliked.toLowerCase());
                                        newEntryRef.child("totalUserDislikes").setValue(1);
                                    // If title exists
                                    } else {
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Log.v(TAG, "In dislike, create new");
                                            // Finds the media title and increments the total # of dislikes
                                            MediaObject object = child.getValue(MediaObject.class);
                                            int totalUserDislikes = object.getTotalUserDislikes();
                                            Firebase ref = child.getRef();
                                            ref.child("totalUserDislikes").setValue(1 + totalUserDislikes);
                                        }
                                    }
                                    // Makes connections between disliked titles
                                    for (String key : userDislikes.keySet()) {
                                        if (!disliked.equals(key)) createConnection(disliked, key);
                                    }
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {}
                            });
                        }
                        // Adds the dislike to the user
                        dislikes.put(disliked, true);
                        Firebase putRef = child.getRef();
                        Log.v("LOGGG", dislikes + "");
                        if (dislikes.size() == 1) putRef.child("disliked").updateChildren(dislikes);
                        else putRef.child("disliked").setValue(dislikes);
                    }
                }
                @Override
                public void onCancelled(FirebaseError firebaseError) {}
            });
        }
    }

    public void checkUserExists(String user, String password, final Intent success, final Activity activity, final Toast toast) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if (dataSnapshot.getValue() != null) {

                        startActivity(activity, success, null);
                    } else {
                        toast.show();
                    }
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
                                       final CustomTileAdapter adapter, final List<String> types) {
        Log.v("rcmdsForUser", user);
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        final Map<String, RelatedObject> overAllMap = new HashMap<String, RelatedObject>();
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        Map<String, Object> tempUserLikes = object.getLiked();
                        Map<String, Object> tempUserDislikes = object.getDisliked();
                        if(tempUserDislikes == null) {
                            tempUserDislikes = new HashMap<String, Object>();
                        }
                        final Set<String> dislikes = tempUserDislikes.keySet();
                        if(tempUserLikes == null) tempUserLikes = new HashMap<String, Object>();
                        final Map<String, Object> userLikes = tempUserLikes;
                        for(String liked : userLikes.keySet()) {
                            Query singleMediaQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(liked.toLowerCase());
                            singleMediaQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot != null) {
                                        for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                                            MediaObject object = singleObject.getValue(MediaObject.class);
                                            Map<String, Object> map = object.getRelated();
                                            if(map == null) map = new HashMap<String, Object>();
                                            int totalLikes = object.getTotalUserLikes();
                                            Set<RelatedObject> relatedObjects = new TreeSet<RelatedObject>();
                                            for(String key : map.keySet()) {
                                                if(!userLikes.containsKey(key)) {
                                                    if (overAllMap.containsKey(key)) {
                                                        overAllMap.get(key).ratio += Integer.parseInt(map.get(key).toString()) / totalLikes;
                                                    } else {
                                                        final RelatedObject related = new RelatedObject(key, Integer.parseInt(map.get(key).toString()), totalLikes);
                                                        overAllMap.put(key, related);

                                                        Query typeQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(related.name.trim().toLowerCase());
                                                        typeQuery.addListenerForSingleValueEvent(new ValueEventListener() {

                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                if (dataSnapshot != null) {
                                                                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                                                                        MediaObject object = singleObject.getValue(MediaObject.class);
                                                                        related.type = object.getType();
                                                                        if(types.contains(object.getType()) && !dislikes.contains(related.name)) {
                                                                            Log.v(TAG, related.toString());
                                                                            list.add(related);
                                                                            adapter.notifyDataSetChanged();
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
                                            }
                                            Log.v(TAG, "Notify Change");
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

    //Like a given media (likedUnformatted) of type (type) for user (user). ifNotLiked runs the code if the object is not liked
    //by the user. ifLiked runs the code if the object is liked
    public void checkLike(final String likedUnformatted, String user, final String type, final Firebase.CompletionListener ifNotLiked, final Firebase.CompletionListener ifLiked, final Firebase.CompletionListener ifDisliked) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        Map<String, Object> liked = object.getLiked();
                        Map<String, Object> disliked = object.getDisliked();
                        if(disliked == null) disliked = new HashMap<String, Object>();
                        if(disliked.containsKey(likedUnformatted))
                            ifDisliked.onComplete(null, null);
                        else if(liked.containsKey(likedUnformatted)) {
                            ifLiked.onComplete(null, null);
                        } else {
                            ifNotLiked.onComplete(null, null);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkDislike(final String dislike, String user, final String type, final Firebase.CompletionListener ifNotDisliked, final Firebase.CompletionListener ifDisliked, final Firebase.CompletionListener ifLiked) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        Map<String, Object> liked = object.getLiked();
                        Map<String, Object> disliked = object.getDisliked();
                        if(disliked == null) disliked = new HashMap<String, Object>();
                        if(liked.containsKey(dislike))
                            ifLiked.onComplete(null, null);
                        else if(disliked.containsKey(dislike)) {
                            ifDisliked.onComplete(null, null);
                        } else {
                            ifNotDisliked.onComplete(null, null);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void deleteUser(String user) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        Log.v(TAG, "indelete");
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        singleObject.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void autoComplete(final String input, final ArrayAdapter<String> adapter) {
        Log.v(TAG, input);
        Query mediaQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").startAt(input.toLowerCase()).limitToFirst(3);
        mediaQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        MediaObject object = singleObject.getValue(MediaObject.class);
                        if(object.getNameLowerCase().startsWith(input.toLowerCase()))
                            addIfDoesntContain(adapter, object.getName().trim());
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void addIfDoesntContain(final ArrayAdapter<String> adapter, final String name) {
        for(int i = 0; i < adapter.getCount(); i++) {
            Log.v(TAG, name + adapter.getItem(i) + adapter.getCount());
            if(name.equals(adapter.getItem(i)))
                return;
        }
        Log.v(TAG, name + adapter.getCount());
        adapter.add(name);
    }

    private void removeDuplicates(final ArrayAdapter<String> adapter) {
        Set<String> set = new HashSet<String>();
        int size = adapter.getCount();
        Log.v(TAG, size + "");
        for(int i = 0; i < size; i++) {
            String item = adapter.getItem(i);
            if(set.contains(item)) {
                adapter.remove(item);
                i--;
            } else {
                set.add(item);
            }
        }
    }

    public void changeEmail(String currUser, final String newUser) {
        currUser = makeStringFirebaseSafe(currUser.trim().toLowerCase());
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(currUser);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        Firebase userName = singleObject.getRef();
                        userName.child("name").setValue(newUser);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public String makeStringFirebaseSafe(String input) {
        String[] theBadThings = {"/" , ".", "#", "[", "]", "$"};

        for(String badBoy : theBadThings) {
            input = input.replace(badBoy, "");
        }

        return input;
    }

    public void getLikesDislikes(String user, final Set<String> likes, final Set<String> dislikes) {
        user = makeStringFirebaseSafe(user.trim().toLowerCase());
        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        UserObject object = singleObject.getValue(UserObject.class);
                        Map<String, Object> liked = object.getLiked();
                        Map<String, Object> disliked = object.getDisliked();
                        if(liked == null) liked = new HashMap<String, Object>();
                        if(disliked == null) disliked = new HashMap<String, Object>();
                        for(String key : liked.keySet()) {
                            likes.add(key);
                        }
                        for(String key : disliked.keySet()) {
                            dislikes.add(key);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkUniqueUser(String user, final Firebase.CompletionListener unique, final Firebase.CompletionListener notUnique) {
        user = makeStringFirebaseSafe(user.toLowerCase().trim());

        Query userQuery = myFirebaseUserRef.orderByChild("name").equalTo(user);
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    if(dataSnapshot.getValue() == null) unique.onComplete(null, null);
                    else notUnique.onComplete(null, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void checkExists(String toCheck, final Firebase.CompletionListener exists,
                          final Firebase.CompletionListener doesntExist) {
        toCheck = makeStringFirebaseSafe(toCheck.trim());

        Query mediaQuery = myFirebaseMoviesRef.orderByChild("nameLowerCase").equalTo(toCheck.toLowerCase());
        mediaQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot != null) {
                    if(dataSnapshot.getValue() == null) doesntExist.onComplete(null, null);
                    for (DataSnapshot singleObject : dataSnapshot.getChildren()) {
                        MediaObject media = singleObject.getValue(MediaObject.class);
                        exists.onComplete(null, null);
                    }
                } else {
                    doesntExist.onComplete(null, null);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


}
