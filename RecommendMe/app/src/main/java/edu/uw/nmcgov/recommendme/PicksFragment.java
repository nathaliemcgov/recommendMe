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
import android.widget.TextView;

/**
 * Created by austinweale on 3/4/16.
 */
public class PicksFragment extends Fragment {

    private static final String TAG = "saved";

    //so we can communicate with the profile activity
    public interface OnProfileSelectedListener extends AccountsFragment.OnProfileSelectedListener {
        void onProfileSelected();
        void onPickSelected(String type);
    }

    public PicksFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.picks_fragment, container, false);

        //back button
        Button button = (Button)rootView.findViewById(R.id.picks_back);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "back");
                ((AccountsFragment.OnProfileSelectedListener)getActivity()).onProfileSelected();

            }
        });

//        //books category
//        Button books = (Button)rootView.findViewById(R.id.book_button);
//        books.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "book");
//                ((AccountsFragment.OnProfileSelectedListener)getActivity()).onPickSelected("book");
//                TextView edit = (TextView)getActivity().findViewById(R.id.edit_picks_type);
//                edit.setText("edit book picks");
//            }
//        });
//
//        //movies category
//        Button movies = (Button)rootView.findViewById(R.id.movies_button);
//        movies.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "movies");
//                ((AccountsFragment.OnProfileSelectedListener)getActivity()).onPickSelected("movie");
//                TextView edit = (TextView)getActivity().findViewById(R.id.edit_picks_type);
//                edit.setText("edit movie picks");
//            }
//        });
//
//        //music category
//        Button music = (Button)rootView.findViewById(R.id.music_button);
//        music.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "music");
//                ((AccountsFragment.OnProfileSelectedListener)getActivity()).onPickSelected("music");
//                TextView edit = (TextView)getActivity().findViewById(R.id.edit_picks_type);
//                edit.setText("edit music picks");
//            }
//        });

        return rootView;

    }
}
