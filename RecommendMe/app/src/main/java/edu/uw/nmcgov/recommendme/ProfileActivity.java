package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
        Button saved = (Button) findViewById(R.id.saved);
        Button picked = (Button) findViewById(R.id.picked);
        Button signUp = (Button) findViewById(R.id.signUp);
        Button logout = (Button) findViewById(R.id.logout);
        Button delete = (Button) findViewById(R.id.deleteProfile);

        if(!user.equals("")) {
            Log.v(TAG, user +"test");

            login.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    new AlertDialog.Builder(v.getContext())
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Delete Profile")
                            .setMessage("Are you sure you want to delete the profile?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    firebase.deleteUser(user);
                                    Intent intent = new Intent(v.getContext(), StartPage.class);
                                    startActivity(intent);
                                }

                            })
                            .setNegativeButton("No", null)
                            .show();
                }
            });

            linked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
//                    Log.v(TAG, "linked");
//                    AccountsFragment accounts = new AccountsFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, accounts)
//                            .commit();

                }
            });

            saved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Log.v(TAG, "saved");
                    Intent intent = new Intent(V.getContext(), SavedActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                }
            });

            picked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
//                    Log.v(TAG, "picked");
//                    PicksFragment picks = new PicksFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, picks)
//                            .commit();

                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(V.getContext(), StartPage.class);
                    startActivity(intent);
                }
            });
        } else {

            linked.setVisibility(View.GONE);
            saved.setVisibility(View.GONE);
            picked.setVisibility(View.GONE);
            logout.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);

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
            // Show search screen
            case R.id.searchRecommendations:
                showSearchForRecommendations();
                return true;
            case R.id.recommendationsForYou:
                showRecommendationsForYou();
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
}
