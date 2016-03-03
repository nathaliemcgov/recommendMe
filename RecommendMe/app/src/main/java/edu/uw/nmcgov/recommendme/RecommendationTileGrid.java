package edu.uw.nmcgov.recommendme;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendationTileGrid extends Fragment {

    private final String TAG = "RecommendationTileGrid";

    private GridView tileGrid;
    private ArrayList<String> recommendationList;
    ArrayAdapter<String> adapter;

    static final String[] recommendations = new String[] {
            "The Departed", "The Illiad"
    };

    public RecommendationTileGrid() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recommendation_tile_grid, container, false);

        tileGrid = (GridView) rootView.findViewById(R.id.recommendationList);

        recommendationList = new ArrayList<String>();
        recommendationList.add("");

        // Tile grid area
        AdapterView gridView = (AdapterView)rootView.findViewById(R.id.recommendationList);
        gridView.setAdapter(adapter);

        // Listens for click on specific media recommendation
        tileGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Gets the media tile that was selected
                Cursor selectedMedia = (Cursor) parent.getItemAtPosition(position);
                Log.i(TAG, "Selected media: " + selectedMedia.toString());

                // Switches fragments in order to show the details about the media selected

            }
        });
        return rootView;
    }
}
