package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Created by austinweale on 3/3/16.
 */
public class ProfileActivity extends AppCompatActivity implements AccountsFragment.OnProfileSelectedListener {

    private static final String TAG = "profile activity";
    private boolean pick;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");   // User's email

        if(user == null) user = "";

        Log.v(TAG, user + "test");

        //main profile fragment
        ProfileFragment profile = new ProfileFragment();

        Bundle fragBundle = new Bundle();
        fragBundle.putString("user", user);
        profile.setArguments(fragBundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, profile)
                .commit();
        pick = false;
    }

    @Override
    public void onProfileSelected() {
//        //main profile fragment
//        ProfileFragment profile = new ProfileFragment();
//        getFragmentManager().beginTransaction()
//                .replace(R.id.mainContainer, profile)
//                .commit();
//
//        //if the bottom fragment is there, delete it
//        if(pick) {
//            getFragmentManager().beginTransaction().remove(getFragmentManager()
//                    .findFragmentById(R.id.edit_container)).commit();
//            pick = false;
//        }
    }

    @Override
    public void onPickSelected(String type) {
        //bottom fragment for the editTexts
//        pick = true;
//        Bundle bundle = new Bundle();
//        bundle.putString("type", type);
//        Fragment edit = new EditFragment();
//        edit.setArguments(bundle);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.edit_container, edit)
//                .commit();
    }
}
