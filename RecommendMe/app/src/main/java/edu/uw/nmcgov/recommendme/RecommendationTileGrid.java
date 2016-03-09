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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendationTileGrid extends Fragment {

    private final String TAG = "RecommendationTileGrid";

    private TextView titleSearchedFor;
    private GridView tileGrid;
    private List<String> recommendationList;
    private ArrayAdapter<String> adapter;
    private String title;

    private OnMediaSelectionListener callback;

    public RecommendationTileGrid() {
        // Required empty public constructor
    }

    public interface OnMediaSelectionListener {
        void onMediaSelected(String mediaTile);
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

        TextView textView = (TextView) ((RecommendationSearchResults) getActivity()).findViewById(R.id.titleSearchedFor);
        title = textView.getText().toString();

        // Container for tiles
        tileGrid = (GridView) rootView.findViewById(R.id.recommendationList);

        recommendationList = new ArrayList<String>();
        populateTiles();

        // Listens for click on specific media recommendation
        tileGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Gets the media tile that was selected
                String selectedMedia = parent.getItemAtPosition(position).toString();

                Log.v(TAG, "Selected media: " + selectedMedia);

                ((OnMediaSelectionListener) getActivity()).onMediaSelected(selectedMedia);
            }
        });
        return rootView;
    }

    private void populateTiles() {
        CustomTileAdapter customAdapter = new CustomTileAdapter(this.getContext(), recommendationList);

        RCMDFirebase firebase = new RCMDFirebase();
        tileGrid.setAdapter(customAdapter);

        firebase.queryTitle(title, recommendationList, customAdapter);
    }
}
