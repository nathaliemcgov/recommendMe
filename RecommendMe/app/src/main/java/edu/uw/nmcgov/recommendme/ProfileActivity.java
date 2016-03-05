package edu.uw.nmcgov.recommendme;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);


        ProfileFragment profile = new ProfileFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, profile)
                .commit();
    }

    @Override
    public void onProfileSelected() {
        ProfileFragment profile = new ProfileFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, profile)
                .commit();

        
    }

    @Override
    public void onPickSelected() {
        EditFragment edit = new EditFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.edit_container, edit)
                .commit();
    }
}
