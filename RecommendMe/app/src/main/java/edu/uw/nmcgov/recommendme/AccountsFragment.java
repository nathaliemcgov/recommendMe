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
public class AccountsFragment extends Fragment {

    private static final String TAG = "linked";

    //so we can communicate with the profile activity
    public interface OnProfileSelectedListener {
        void onProfileSelected();
//        void onPickSelected(String type);
    }

    public AccountsFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.account_fragment, container, false);

        //back button
        Button button = (Button)rootView.findViewById(R.id.linked_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "back");
                ((OnProfileSelectedListener)getActivity()).onProfileSelected();

            }
        });

        return rootView;

    }
}
