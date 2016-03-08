package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
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
    private static final int REQUEST_PICTURE = 1;


    /*allows interaction with the start page
    public interface OnButtonSelectionListener{
        void onLinkedSelected();
        void onSavedSelected();
        void onPicksSelected();
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected();
    }*/


    public ProfileFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        Button linked = (Button)rootView.findViewById(R.id.linked);
        linked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "linked");
                AccountsFragment accounts = new AccountsFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, accounts)
                        .commit();

            }
        });

        Button saved = (Button)rootView.findViewById(R.id.saved);
        saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "saved");
                SavedFragment saved = new SavedFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, saved)
                        .commit();


            }
        });

        Button picked = (Button)rootView.findViewById(R.id.picked);
        picked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "picked");
                PicksFragment picks = new PicksFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, picks)
                        .commit();

            }
        });

        View cameraButton = rootView.findViewById(R.id.pic);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.v(TAG, "Camera button pressed");

                //implicit intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_PICTURE);
                }
            }
        });



        return rootView;

    }
}