package edu.uw.nmcgov.recommendme;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.firebase.client.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by iguest on 2/29/16.
 */
public class CircleGraphicActivity extends AppCompatActivity  {

    private String title;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            title = bundle.getString("title");
            if (title == null) title = "Pulp Fiction";
            if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
                user = bundle.getString("user");
            }
        } else {
            title = "Pulp Fiction";
        }


        setContentView(R.layout.drawing_view);
    }

    public String getFireBaseTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }
}