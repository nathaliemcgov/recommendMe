package edu.uw.nmcgov.recommendme;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iguest on 2/29/16.
 */
public class FireBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Firebase.setAndroidContext(this);

        final Firebase myFirebaseRef = new Firebase("https://rcmd.firebaseio.com/Movies");



        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String movieOne = ((EditText) findViewById(R.id.editText)).getText().toString();
                final String movieTwo = ((EditText) findViewById(R.id.editText2)).getText().toString();

                final Query movieQuery1 = myFirebaseRef.orderByChild("name").equalTo(movieOne);
                final Query movieQuery2 = myFirebaseRef.orderByChild("name").equalTo(movieTwo);

                if (movieOne.length() > 0 && movieTwo.length() > 0) {
                    movieQuery1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                Log.v("TAG", "HERE");
                                Firebase newPostRef = myFirebaseRef.push();
                                newPostRef.child("name").setValue(movieOne);
                                Map<String, Integer> map = new HashMap<String, Integer>();
                                map.put(movieTwo, 1);
                                newPostRef.child("related").setValue(map);
                            } else {
                                Log.v("movieONe", dataSnapshot.getValue().toString());
                                Map<String, Integer> map = (Map<String, Integer>) dataSnapshot.child("related").getValue();
                                map.put(movieOne, map.get(movieTwo) + 1);
                                Firebase postRef = dataSnapshot.getRef();
                                postRef.child("related").setValue(map);
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    movieQuery2.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                Firebase newPostRef = myFirebaseRef.push();
                                newPostRef.child("name").setValue(movieTwo);
                                Map<String, Integer> map = new HashMap<String, Integer>();
                                map.put(movieOne, 1);
                                newPostRef.child("related").setValue(map);
                            } else {
                                Log.v("movieTwo", dataSnapshot.getValue().toString());
                                Map<String, Integer> map = (Map<String, Integer>) dataSnapshot.child("related").getValue();
                                map.put(movieTwo, map.get(movieOne) + 1);
                                Firebase postRef = dataSnapshot.getRef();
                                postRef.child("related").setValue(map);
                            }

                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }

            }
        });
    }
}