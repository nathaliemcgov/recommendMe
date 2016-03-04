package edu.uw.nmcgov.recommendme;


import android.content.Context;
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
    private ArrayAdapter<String> adapter;

    private OnMediaSelectionListener callback;

    static final String[] recommendations = new String[] {
            "The Departed", "The Illiad"
    };

    public RecommendationTileGrid() {
        // Required empty public constructor
    }

    public interface OnMediaSelectionListener {
        void onMediaSelected(Cursor mediaTile);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);

        try {
            callback = (OnMediaSelectionListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnMediaSelectionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "Reached tile grid fragment!");
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recommendation_tile_grid, container, false);

        tileGrid = (GridView) rootView.findViewById(R.id.recommendationList);

        recommendationList = new ArrayList<String>();
        recommendationList.add("The Departed");
        recommendationList.add("The Illiad");

        adapter = new ArrayAdapter<String>(getActivity(), R.layout.recommendation_element, recommendationList);
        tileGrid.setAdapter(adapter);

        // Tile grid area
//        AdapterView gridView = (AdapterView) rootView.findViewById(R.id.recommendationList);
//        gridView.setAdapter(adapter);

        // Listens for click on specific media recommendation
        tileGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Gets the media tile that was selected
                Cursor selectedMedia = (Cursor) parent.getItemAtPosition(position);
                Log.i(TAG, "Selected media: " + selectedMedia.toString());

                ((OnMediaSelectionListener) getActivity()).onMediaSelected(selectedMedia);
            }
        });
        return rootView;
    }
}
