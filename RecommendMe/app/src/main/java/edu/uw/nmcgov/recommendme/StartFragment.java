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
public class StartFragment extends Fragment {

    private static final String TAG = "profile";

    public StartFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.start_fragment, container, false);

        Button button = (Button)rootView.findViewById(R.id.profile_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "clicked");
                ProfileFragment profile = new ProfileFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.mainContainer, profile)
                        .commit();

            }
        });


        return rootView;

    }
}
