package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.app.PendingIntent.getActivity;

/**
 * Created by iguest on 3/8/16.
 */
public class CustomTileAdapter extends ArrayAdapter<RelatedObject> {

    //private RecommendationTileGrid.BtnClickListener mClickListener = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<RelatedObject> recommendationList;
    private RCMDFirebase myFirebase;
    private String user;
    private String titleSearchedFor;
    private Set<String> myLikes;
    private Set<String> myDislikes;
    private int liked;
    private int disliked;

    public CustomTileAdapter(Context context, List<RelatedObject> titles, String user, String titleSearchedFor) {
        super(context, 0, titles);
        mContext = context;
        this.user = user;
        this.titleSearchedFor = titleSearchedFor;
        Firebase.setAndroidContext(context);
        myFirebase = new RCMDFirebase();
        recommendationList = titles;
        myLikes = new HashSet<String>();
        myDislikes = new HashSet<String>();
        myFirebase.getLikesDislikes(user, myLikes, myDislikes);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view = convertView;

        // Gets the title of the recommendation
        final RelatedObject object = getItem(position);

        // Checking if an existing view is being reused, otherwise inflate the view
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.recommendation_element,
                    parent, false);
        }

        // To send liked data to media details
        liked = 0;
        disliked = 0;

        //Set up media type icon
        ImageView mediaTypeButton = (ImageView) view.findViewById(R.id.mediaType);
        final String mediaType = object.getType();

        // Setting background of tile
        int backgroundResource = R.drawable.ic_book_icon;
        if (mediaType != null && mediaType.equals("music"))
            backgroundResource = R.drawable.ic_music_icon;
        else if (mediaType != null && mediaType.equals("movie"))
            backgroundResource = R.drawable.ic_movie_icon;

        mediaTypeButton.setBackgroundResource(backgroundResource);

        TextView mediaTitle = (TextView) view.findViewById(R.id.recommendationElement);

        mediaTitle.setText(object.name);

        mediaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Media title
                TextView titleView = (TextView) v.findViewById(R.id.recommendationElement);
                String title = titleView.getText().toString();

                String activity = "random";
                if (mContext instanceof RecommendationsForYou) activity = "recommendationsforyou";

                Intent intent = new Intent(mContext, MediaDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("user", user);
                intent.putExtra("searchTitle", titleSearchedFor);
                intent.putExtra("activity", activity);
                intent.putExtra("mediaType", mediaType);
                intent.putExtra("ratio", object.getRatio());
                intent.putExtra("ifLiked", liked);
                intent.putExtra("ifDisliked", disliked);
                mContext.startActivity(intent);
            }
        });

        final ImageButton thumbsUpBtn = (ImageButton) view.findViewById(R.id.thumbsUpBtn);

        thumbsUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!v.isSelected() && user != null && !user.equals("")) {
                    Log.v("clicked", "clicked that shit" + user);
                    myFirebase.checkLike(object.name, user, object.getType(), new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inNotLike");
                            myFirebase.setLike(object.name, user);
                            Context context = parent.getContext();
                            CharSequence text = "Liked : " + object.name;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            thumbsUpBtn.setImageResource(R.drawable.thumbs_up_button);
                            myLikes.add(object.name);
                            toast.show();
                            //recommendationList.remove(position);
                            notifyDataSetChanged();
                        }
                    }, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inLike");
                            Context context = parent.getContext();
                            CharSequence text = "You already like " + object.name;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = parent.getContext();
                            CharSequence text = "You said you disliked " + object.name;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });

                } else if (user == null || user.equals("")) {
                    Context context = parent.getContext();
                    CharSequence text = "Please login to like media";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });

        final ImageButton buttonDown = (ImageButton) view.findViewById(R.id.thumbsDownBtn);

        buttonDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!v.isSelected() && user != null && !user.equals("")) {
                    //v.setSelected(!v.isSelected());
                    Log.v("clicked", "clicked that shit" + user);
                    myFirebase.checkDislike(object.name, user, object.getType(), new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inNotLike");
                            myFirebase.setDislike(user, object.name);
                            Context context = parent.getContext();
                            CharSequence text = "Disliked : " + object.name;
                            buttonDown.setImageResource(R.drawable.ic_thumbs_down_tile_selected);
                            int duration = Toast.LENGTH_SHORT;
                            myDislikes.add(object.name);
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                            //recommendationList.remove(position);
                            notifyDataSetChanged();
                        }
                    }, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Log.v("CustomTileAdapter", "inLike");
                            Context context = parent.getContext();
                            CharSequence text = "You already dislike : " + object.name;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }, new Firebase.CompletionListener() {
                        @Override
                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                            Context context = parent.getContext();
                            CharSequence text = "You said you liked " + object.name;
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    });
                } else if (v.isSelected() && user != null && !user.equals("")) {
                    v.setSelected(v.isSelected());
                    thumbsUpBtn.setImageResource(R.drawable.ic_thumbs_up);
                    buttonDown.setSelected(!buttonDown.isSelected());
                    buttonDown.setImageResource(R.drawable.ic_thumbs_down);
                } else if (user == null || user.equals("")) {
                    Context context = parent.getContext();
                    CharSequence text = "Please login to like media";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });


        // On click listener for "Save" button on each tile
        final ImageButton saveTitleButton = (ImageButton) view.findViewById(R.id.saveMediaTitle);

        // Write title to phone's external storage
        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence text = "";

                if (!v.isSelected()) {
                    handleSaveMediaTitle(object);

                    v.setSelected(!v.isSelected());
                    saveTitleButton.setImageResource(R.drawable.ic_star);
                    text = "Saved : " + object.toString();
                } else {
                    handleRemoveTitleFromSaved(object);

                    v.setSelected(!v.isSelected());
                    saveTitleButton.setImageResource(R.drawable.ic_star_unselected);
                    text = "Removed from saved list : " + object.toString();
                }
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();
            }
        });

        if(myLikes.contains(object.name)) {
            thumbsUpBtn.setImageResource(R.drawable.ic_thumbs_up_tile_selected);
            liked = 1;
        } else if (myDislikes.contains(object.name)) {
            buttonDown.setImageResource(R.drawable.ic_thumbs_down_tile_selected);
            disliked = 1;
        } else {
            thumbsUpBtn.setImageResource(R.drawable.ic_thumbs_up);
            buttonDown.setImageResource(R.drawable.ic_thumbs_down);
        }

        return view;
    }

    // Writes saved media title to phone's external storage
    public void handleSaveMediaTitle(RelatedObject object) {
        boolean match = false;
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");
                String mediaTitle = object.name + "\n";

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;

                    while ((line = reader.readLine()) != null && !match) {
                        if (line.contains(object.name))
                            match = true;
                    }

                    if (!match) {
                        // create a filewriter and set append modus to true
                        FileWriter fw = new FileWriter(file, true);
                        fw.append(mediaTitle);
                        fw.close();
                        Log.v("tag", "file written: " + mediaTitle);
                    }

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }

    public void handleRemoveTitleFromSaved(RelatedObject object) {
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line;

                    FileWriter fw = new FileWriter(file, false);
                    while ((line = reader.readLine()) != null) {
                        Log.v("CHECKING", "" + line.contains(object.name));
                        if (!line.contains(object.name)) {
                            fw.append(line);
                            fw.close();
                        }
                    }
                    Log.v("tag", "title removed: " + object.name);

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }
            } catch (Exception e) {
                Log.v("ERROR", e.toString());
            }
        }
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
