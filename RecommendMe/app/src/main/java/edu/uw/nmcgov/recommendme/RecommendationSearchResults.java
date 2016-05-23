package edu.uw.nmcgov.recommendme;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

public class RecommendationSearchResults extends AppCompatActivity
        implements RecommendationTileGrid.OnMediaSelectionListener {
    private final String TAG = "RecommendationSearchRes";

    private TextView titleSearchedFor;
    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RecommendationTileGrid tileGridFragment;
    private RCMDFirebase firebase;
    private String user;
    private boolean inFirebase;
    private Context mContext;
    private String titleSearched;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendation_search_results);

        mContext = this;
        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // TextView for header of screen
        titleSearchedFor = (TextView) findViewById(R.id.titleSearchedFor);

        Bundle bundle = getIntent().getExtras();
        titleSearched = bundle.getString("title");

        if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
            user = bundle.getString("user");
        } else {
            user = "";
        }

        // Adds title searched for to header
        titleSearchedFor.setText(titleSearched);
        titleSearchedFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MediaDetails.class);
                intent.putExtra("title", titleSearched);
                intent.putExtra("user", user);
                intent.putExtra("searchTitle", titleSearched);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });

        // Setting on click listener to make button appear selected
        // Later will need to store like/dislike in firebase
        thumbsUpBtn = (ImageButton) findViewById(R.id.thumbsUpBtnSearchRes);
//        thumbsDownBtn = (ImageButton) rootView.findViewById(R.id.thumbsDownBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View button) {
                //Set the button's appearance
                Log.v("INFRRBASE", tileGridFragment.inFirebase() + "");
                if (!button.isSelected()) {    // If the user 'likes' the title
                    // Send to db the user's email + title of liked media

                    if (user != null && !user.equals("")) {
                        if(tileGridFragment.inFirebase()) {
                            firebase.setLike(titleSearched, user);
                            button.setSelected(!button.isSelected());
                        } else {
                            String[] choices = {"Movie", "Music", "Book"};
                            final String[] formattedChoices = {"movie", "music", "book"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                            builder.setTitle("This item isn't in our database, what type of media is it?")
                                    .setItems(choices, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            firebase.setLike(titleSearched, user, formattedChoices[which]);
                                            button.setSelected(!button.isSelected());
                                        }
                                    });
                            builder.create().show();
                        }
                    } else {
                        Context context = getApplicationContext();
                        CharSequence text = "Please login to like media!";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
//                } else {      // If the user 'unlikes' the title
//                    // Send to db the user's email + title of unliked media
                }
            }
        });

        Bundle bundle1 = new Bundle();
        bundle1.putString("user", user);
        bundle1.putString("searchTitle", titleSearched);

        // Fragment to display tile grid
        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        tileGridFragment = new RecommendationTileGrid();
        tileGridFragment.setArguments(bundle1);
        ft.add(R.id.gridContainer, tileGridFragment, "Grid");
        ft.commit();
    }

    // Creates the options menu in the action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_menu, menu);

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
            case R.id.profile:
                Log.v(TAG, "in profile");
                showProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Show search screen
    private void showSearchForRecommendations() {
        Intent intent = new Intent(this, SearchForRecommendations.class);

        String username;
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
            username = bundle.getString("user");
            intent.putExtra("user", username);
        }
        startActivity(intent);
    }

    private void showProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    private void showRecommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        if (user != null && user.length() > 0) {
            intent.putExtra("user", user);
        }
        startActivity(intent);
    }

    // When a tile is selected, move to fragment that gives details about the tile selected
    @Override
    public void onMediaSelected(String mediaTile) {
        // Fragment that contains details about the selected tile
        MediaDetails details = new MediaDetails();

        Intent intent = new Intent(this, MediaDetails.class);
        intent.putExtra("title", mediaTile);
        intent.putExtra("user", user);
        intent.putExtra("searchTitle", titleSearched);
        intent.putExtra("activity", "RecommendationSearchResults");
        startActivity(intent);
    }
}
