package edu.uw.nmcgov.recommendme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

/**
 * Created by austinweale on 3/3/16.
 */
public class ProfileActivity extends AppCompatActivity implements AccountsFragment.OnProfileSelectedListener {

    private static final String TAG = "profile activity";
    private boolean pick;
    private String user;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");   // User's email

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        if(user == null) user = "";

        Log.v(TAG, user + "test");

        pick = false;

        Button login = (Button) findViewById(R.id.login);
        Button linked = (Button) findViewById(R.id.linked);
        Button manageAccount = (Button) findViewById(R.id.manageAccount);
        Button signUp = (Button) findViewById(R.id.signUp);
        Button logout = (Button) findViewById(R.id.logout);

        if(!user.equals("")) {
            Log.v(TAG, user +"test");

            login.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);

            manageAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(V.getContext(), ManageAccountActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            // For linked accounts
//            linked.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View V) {
//                    Log.v(TAG, "linked");
//                    AccountsFragment accounts = new AccountsFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, accounts)
//                            .commit();
//
//                }
//            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(V.getContext(), SetupPageActivity.class);
                    startActivity(intent);
                }
            });
        } else {
            linked.setVisibility(View.GONE);
            manageAccount.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(V.getContext(), ExistingLogin.class);
                    startActivity(intent);
                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(V.getContext(), CreateProfileActivity.class);
                    startActivity(intent);
                }
            });
        }
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

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);

        MenuItem profile = menu.findItem(R.id.profile);
        profile.setVisible(false);

        if (user == null || user.length() == 0) {
            MenuItem searchMenuOption = menu.findItem(R.id.recommendationsForYou);
            searchMenuOption.setVisible(false);
        }
        return true;
    }

    // Gets action user chooses from menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchRecommendations:
                showSearchForRecommendations();
                return true;
            case R.id.recommendationsForYou:
                showRecommendationsForYou();
                return true;
            case R.id.savedRecommendations:
                showSavedRecommendations();
                return true;
            case R.id.profile:
                showProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRecommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showSearchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showSavedRecommendations() {
        Intent intent = new Intent(this, SavedActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }
}
