package edu.uw.nmcgov.recommendme;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by iguest on 2/29/16.
 */
public class FireBaseActivity extends AppCompatActivity  {

    RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);

        Firebase.setAndroidContext(this);

        firebase = new RCMDFirebase();

        Map<String, String> tyler = new HashMap<String, String>();

        tyler.put("name", "tyler");

        //firebase.createUser(tyler);
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("movies.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Log.v("Tag", scanner.nextLine());

        firebase.setLike("Rat King", "tyler");



        Button button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String movieOne = ((EditText) findViewById(R.id.editText)).getText().toString();
                String movieTwo = ((EditText) findViewById(R.id.editText2)).getText().toString();
                firebase.createConnection(movieOne, movieTwo);


            }
        });
    }
}