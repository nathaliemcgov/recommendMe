package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iguest on 3/8/16.
 */
public class CustomTileAdapter extends ArrayAdapter<RelatedObject> {

    public CustomTileAdapter(Context context, List<RelatedObject> titles) {
        super(context, 0, titles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the title of the recommendation
        RelatedObject title = getItem(position);

        // Checking if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recommendation_element,
                    parent, false);
    }

        TextView mediaTitle = (TextView) convertView.findViewById(R.id.recommendationElement);

        mediaTitle.setText(title.name);
        Log.v("tag", position + "");
        return convertView;
    }
}
