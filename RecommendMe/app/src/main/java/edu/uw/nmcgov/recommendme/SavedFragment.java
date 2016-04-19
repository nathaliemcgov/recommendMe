package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by austinweale on 3/4/16.
 */
public class SavedFragment extends Fragment {

    private static final String TAG = "saved";

    public interface OnProfileSelectedListener extends AccountsFragment.OnProfileSelectedListener {
        void onProfileSelected();
    }

    public SavedFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.saved_fragment, container, false);

        if (isExternalStorageWritable()) {
            try {
                File dir;
                dir = Environment.getExternalStorageDirectory();

                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");

                BufferedReader reader = new BufferedReader(new FileReader(file));

                String text = reader.readLine();

                Log.v(TAG, text);
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
        return rootView;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
