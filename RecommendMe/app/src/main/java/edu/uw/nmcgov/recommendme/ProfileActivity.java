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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);


        ProfileFragment profile = new ProfileFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, profile)
                .commit();
        pick = false;
    }

    @Override
    public void onProfileSelected() {
        ProfileFragment profile = new ProfileFragment();
        getFragmentManager().beginTransaction()
                .replace(R.id.mainContainer, profile)
                .commit();

        if(pick) {
            getFragmentManager().beginTransaction().remove(getFragmentManager()
                    .findFragmentById(R.id.edit_container)).commit();
            pick = false;
        }
    }


    @Override
    public void onPickSelected(String type) {
        pick = true;
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        Fragment edit = new EditFragment();
        edit.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.edit_container, edit)
                .commit();
    }
}
