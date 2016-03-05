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
 * Created by austinweale on 3/4/16.
 */
public class PicksFragment extends Fragment {

    private static final String TAG = "saved";

    public interface OnProfileSelectedListener extends AccountsFragment.OnProfileSelectedListener {
        void onProfileSelected();
    }

    public PicksFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.picks_fragment, container, false);

        Button button = (Button)rootView.findViewById(R.id.picks_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "back");
                ((AccountsFragment.OnProfileSelectedListener)getActivity()).onProfileSelected();

            }
        });

        return rootView;

    }
}
