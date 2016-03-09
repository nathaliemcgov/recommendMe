package edu.uw.nmcgov.recommendme;

import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.firebase.client.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

//        tyler.put("name", "tyler");
//        tyler.put("name", "josh");
//
//        new AutoComplete().testWeb("Pulp");
        final List<String> movies = new ArrayList<String>();
//        movies.add("gainer house");
//        movies.add("The Departed");
//        movies.add("sup");
//        movies.add("waterwaterwater");
//        movies.add("Vikram");
//        movies.add("Boris");

        final int[] i = {0};
        Button button = (Button) findViewById(R.id.button);
        Button button2 = (Button) findViewById(R.id.button2);

        EditText edit1 = (EditText) findViewById(R.id.editText);
        EditText edit2 = (EditText) findViewById(R.id.editText2);
        final AutoComplete auto = new AutoComplete();


        edit1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                auto.testWeb(s.toString());
                Log.v("TAG", s.toString());

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> tyler = new HashMap<String, String>();
                tyler.put("name", "tyler");
                i[0]++;
                if (i[0] == 1)
                    firebase.createUser(tyler);
                firebase.setLike(movies.get(i[0]), "tyler");
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, String> tyler = new HashMap<String, String>();
                tyler.put("name", "josh");
                if (i[0] == 1)
                    firebase.createUser(tyler);

                firebase.setLike(movies.get(i[0]), "josh");

            }
        });
    }
}