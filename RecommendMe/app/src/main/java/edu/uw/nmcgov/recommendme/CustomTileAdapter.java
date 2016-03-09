package edu.uw.nmcgov.recommendme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by iguest on 3/8/16.
 */
public class CustomTileAdapter extends ArrayAdapter<String> {

    private RecommendationTileGrid.BtnClickListener mClickListener = null;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> recommendationList;

    public CustomTileAdapter(Context context, List<String> titles) {
        super(context, 0, titles);
    }

    public CustomTileAdapter(Context context, List<String> titles, RecommendationTileGrid.BtnClickListener listener) {
        super(context, 0, titles);

        mContext = context;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        recommendationList = titles;
        mClickListener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the title of the recommendation
        String title = getItem(position);

        // Checking if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.recommendation_element,
                    parent, false);
    }

        TextView mediaTitle = (TextView) convertView.findViewById(R.id.recommendationElement);

        mediaTitle.setText(title);
        Log.v("tag", position + "");


        return convertView;
    }
}
