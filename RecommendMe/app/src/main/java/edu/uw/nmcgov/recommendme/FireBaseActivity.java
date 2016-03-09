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
        firebase.createUser(tyler);
        tyler.put("name", "chris");
        firebase.createUser(tyler);
        tyler.put("name", "austin");
        firebase.createUser(tyler);
        tyler.put("name", "nathalie");
        firebase.createUser(tyler);
        tyler.put("name", "allison");
        firebase.createUser(tyler);

        firebase.setLike("pitch perfect", "allison");
        firebase.setLike("taylor swift", "allison");
        firebase.setLike("frozen", "allison");
        firebase.setLike("toy story", "allison");

        firebase.setLike("elliot smith", "chris");
        firebase.setLike("american beauty", "chris");
        firebase.setLike("orange clockwork", "chris");
        firebase.setLike("slaughterhouse 5", "chris");

        firebase.setLike("speedy ortiz", "austin");
        firebase.setLike("orange clockwork", "austin");
        firebase.setLike("toy story", "austin");
        firebase.setLike("modest mouse", "austin");

        firebase.setLike("radiohead", "nathalie");

        firebase.setLike("toy story", "tyler");
        firebase.setLike("death cab for cutie", "tyler");
        firebase.setLike("slaughterhouse 5", "tyler");
        firebase.setLike("modest mouse", "tyler");

        

    }
}