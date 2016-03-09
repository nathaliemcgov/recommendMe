package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by austinweale on 3/5/16.
 */
public class EditFragment extends Fragment {
    private static final String TAG = "linked";

    private String type;
    Activity activity;

    public EditFragment(){
        activity = this.getActivity();
    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.edit_fragment, container, false);

        //get the type (movie/music/book)
        Bundle bundle = this.getArguments();
        if(bundle == null){
            Log.v(TAG, "NO");
        }else{
            type = bundle.getString("type");
        }

        //different buttons for each editText
        Button button1 = (Button)(rootView.findViewById(R.id.change_1));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });

        Button button2 = (Button)(rootView.findViewById(R.id.change_2));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });

        Button button3 = (Button)(rootView.findViewById(R.id.change_3));
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });

        return rootView;

    }

}
