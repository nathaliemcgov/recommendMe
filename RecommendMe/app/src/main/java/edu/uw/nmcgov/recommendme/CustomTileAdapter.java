package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public View getView(int position, View convertView, ViewGroup parent) {

        final View view = convertView;
        // Gets the title of the recommendation
        final RelatedObject object = getItem(position);

        // Checking if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recommendation_element,
                    parent, false);
    }

        TextView mediaTitle = (TextView) convertView.findViewById(R.id.recommendationElement);

        mediaTitle.setText(object.name);

        mediaTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Media title
                TextView titleView = (TextView) v.findViewById(R.id.recommendationElement);
                String title = titleView.getText().toString();
                Log.v("tag", title);

                Intent intent = new Intent(mContext, MediaDetails.class);
                intent.putExtra("title", title);
                intent.putExtra("user", user);
                mContext.startActivity(intent);
            }
        });

        ImageButton button = (ImageButton) convertView.findViewById(R.id.thumbsUpBtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setSelected(!v.isSelected());
                Log.v("clicked", "clicked that shit" + user);
                firebase.setLike(object.name, user);
            }
        });

        // On click listener for "Save" button on each tile
        Button saveTitleButton = (Button) convertView.findViewById(R.id.saveMediaTitle);

        saveTitleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v("tag", object.name + " saved");
                // Write title to phone's external storage
                handleSaveMediaTitle();
            }
        });

        return convertView;
    }

    // Writes saved media title to phone's external storage
    public void handleSaveMediaTitle() {
        if (isExternalStorageWritable()) {
            try {
                // Where the file will be saved
                File dir;

                dir = Environment.getExternalStorageDirectory(); //private external
                dir = mContext.getFilesDir(); //internal file storage
                dir = mContext.getCacheDir(); //internal cache
                dir = mContext.getExternalCacheDir(); //external cache

                File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "mediaTitles.txt");
                FileOutputStream outputStream = new FileOutputStream(file);
                String msg = "Writing to file";
                outputStream.write(msg.getBytes());

                outputStream.close();
                Log.v("tag", "file written");
            } catch (IOException ioe) {
                ioe.printStackTrace();
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
