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
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getActivity;

/**
 * Created by iguest on 3/8/16.
 */
public class CustomTileAdapter extends ArrayAdapter<RelatedObject> {

    private RecommendationTileGrid.BtnClickListener mClickListener = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> recommendationList;
    private RCMDFirebase firebase;
    private String user;

    public CustomTileAdapter(Context context, List<RelatedObject> titles, String user) {
        super(context, 0, titles);
        mContext = context;
        this.user = user;
        Firebase.setAndroidContext(context);
        firebase = new RCMDFirebase();
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        final View view = convertView;
        // Gets the title of the recommendation
        final RelatedObject object = getItem(position);

        // Checking if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recommendation_element,
                    parent, false);
        }

        TextView mediaTitle = (TextView) convertView.findViewById(R.id.recommendationElement);
        //TextView ratio = (TextView) convertView.findViewById(R.id.tileRatio);

        mediaTitle.setText(object.name);
        //ratio.setText(((int) Math.floor(object.getRatio() * 100)) + "%");
        mediaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Media title
                TextView titleView = (TextView) v.findViewById(R.id.recommendationElement);
                String title = titleView.getText().toString();
                Log.v("tag", title);

                String activity = "random";
                Log.v("tile adapter", mContext.toString());
                if (mContext instanceof RecommendationsForYou) activity = "recommendationsforyou";

                Intent intent = new Intent(mContext, MediaDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("user", user);
                intent.putExtra("activity", activity);
                mContext.startActivity(intent);
            }
        });

        ImageButton button = (ImageButton) convertView.findViewById(R.id.thumbsUpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("clicked", "clicked that shit" + user);
                if(!v.isSelected() && user != null && !user.equals("")) {
                    v.setSelected(!v.isSelected());
                    Log.v("clicked", "clicked that shit" + user);
                    firebase.setLike(object.name, user);
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
        ImageButton saveTitleButton = (ImageButton) convertView.findViewById(R.id.saveMediaTitle);

        // Write title to phone's external storage
        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence text = "Saved : " + object.toString();
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(getContext(), text, duration);
                toast.show();

                handleSaveMediaTitle(object);
            }
        });

        return convertView;
    }

    // Writes saved media title to phone's external storage
    public void handleSaveMediaTitle(RelatedObject object) {
        if (isExternalStorageWritable()) {
            try {
                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");
                String mediaTitle = object.name + "\n";

                try {
                    // create a filewriter and set append modus to true
                    FileWriter fw = new FileWriter(file, true);
                    fw.append(mediaTitle);
                    fw.close();

                } catch (IOException e) {
                    Log.w("ExternalStorage", "Error writing " + file, e);
                }

                Log.v("tag", "file written: " + mediaTitle);
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
