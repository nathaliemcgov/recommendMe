package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by austinweale on 3/3/16.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "profile";


    //allows interaction with the start page
    public interface OnButtonSelectionListener{
        void onLinkedSelected();
        void onSavedSelected();
        void onPicksSelected();
    }


    public ProfileFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.activity_profile_page, container, false);

        Button linked = (Button)rootView.findViewById(R.id.linked);
        linked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "linked");

            }
        });

        Button saved = (Button)rootView.findViewById(R.id.saved);
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "saved");

            }
        });

        Button picked = (Button)rootView.findViewById(R.id.picked);
        picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "picked");

            }
        });


        return rootView;

    }
}
