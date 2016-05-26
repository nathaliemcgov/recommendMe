package edu.uw.nmcgov.recommendme;


import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendationTileGrid extends Fragment {

    private final String TAG = "RecommendationTileGrid";

    private String titleSearchedFor;
    private GridView tileGrid;
    private List<RelatedObject> recommendationList;
    private ArrayAdapter<String> adapter;
    private String title;
    private RCMDFirebase firebase;
    private String user;
    private List<RelatedObject> savedList;
    private CheckBox movieCheck;
    private CheckBox bookCheck;
    private CheckBox musicCheck;
    private CustomTileAdapter customAdapter;

    private OnMediaSelectionListener callback;

    //public interface BtnClickListener {
        //public abstract void onBtnClick(int position);
    //}

    public RecommendationTileGrid() {
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

        recommendationList = new ArrayList<RelatedObject>();
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_recommendation_tile_grid, container, false);

        Firebase.setAndroidContext(container.getContext());
        firebase = new RCMDFirebase();

        Bundle bundle = this.getArguments();
        if (bundle.getString("user") != null && bundle.getString("user").length() > 0)
            user = bundle.getString("user");
        else
            user = "";

        if (bundle.getString("searchTitle") != null && bundle.getString("searchTitle").length() > 0)
            titleSearchedFor = bundle.getString("searchTitle");
        else
            titleSearchedFor = "";

        // Container for tiles
        tileGrid = (GridView) rootView.findViewById(R.id.recommendationList);

        customAdapter = new CustomTileAdapter(this.getContext(), recommendationList, user, titleSearchedFor);

        tileGrid.setAdapter(customAdapter);

        if(! (getActivity() instanceof SavedActivity)) {
            movieCheck = (CheckBox) getActivity().findViewById(R.id.movie_check);
            bookCheck = (CheckBox) getActivity().findViewById(R.id.book_check);
            musicCheck = (CheckBox) getActivity().findViewById(R.id.music_check);

            movieCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof RecommendationsForYou)
                        populateTilesForUser();
                    else
                        populateTilesForSearch();
                }
            });

            musicCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof RecommendationsForYou)
                        populateTilesForUser();
                    else
                        populateTilesForSearch();
                }
            });

            bookCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getActivity() instanceof RecommendationsForYou)
                        populateTilesForUser();
                    else
                        populateTilesForSearch();
                }
            });
        }

        if (getActivity() instanceof RecommendationsForYou) { // If the user reached screen by creating an account or logging in
            populateTilesForUser();
        } else if (getActivity() instanceof RecommendationSearchResults) { // If the user reached screen by searching media title
            TextView textView = (TextView) ((RecommendationSearchResults) getActivity()).findViewById(R.id.titleSearchedFor);
            title = textView.getText().toString();
            populateTilesForSearch();
        } else if (getActivity() instanceof SavedActivity) { // If the user reached screen by saved recommendations
            populateTilesForSavedRecommendations();
        }

        Log.v("REC_LIST", recommendationList.toString());

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

    // If the user reached screen by searching media title
    private void populateTilesForSearch() {

        recommendationList.clear();
        customAdapter.notifyDataSetChanged();
        List<String> types = new ArrayList<String>();
        if(movieCheck.isChecked())
            types.add("movie");
        if(musicCheck.isChecked())
            types.add("music");
        if(bookCheck.isChecked())
            types.add("book");

        firebase.queryTitle(title, user, recommendationList, customAdapter, types);
    }

    // If the user reached screen by creating an account or logging in
    private void populateTilesForUser() {
//        String username;
//        Bundle bundle = getIntent().getExtras();
//        if (bundle.getString("user") != null && bundle.getString("user").length() > 0) {
//            username = bundle.getString("user");
//        }

        recommendationList.clear();
        customAdapter.notifyDataSetChanged();
        List<String> types = new ArrayList<String>();
        if(movieCheck.isChecked())
            types.add("movie");
        if(musicCheck.isChecked())
            types.add("music");
        if(bookCheck.isChecked())
            types.add("book");

        firebase.recommendationsForUser(user, recommendationList, customAdapter, types);

    }

    // If the user reached screen by saved recommendations
    private void populateTilesForSavedRecommendations() {
        // Getting list of saved media titles
        if (isExternalStorageWritable()) {
            try {
                File dir;
                dir = Environment.getExternalStorageDirectory();

                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");

                BufferedReader reader = new BufferedReader(new FileReader(file));

                savedList = new ArrayList<RelatedObject>();
                String line;

                while ((line = reader.readLine()) != null) {
                    RelatedObject relatedObject = new RelatedObject(line);
                    savedList.add(relatedObject);
                }

                Log.v("SavedActivity", "" + savedList);
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        if(savedList == null)
            savedList = new ArrayList<RelatedObject>();
        Log.v("LIST OF SAVED", "" + savedList);

        CustomTileAdapter customAdapter = new CustomTileAdapter(this.getContext(), savedList, user, titleSearchedFor);
        tileGrid.setAdapter(customAdapter);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public boolean inFirebase() {
        return recommendationList.size() > 0;
    }
}
