package edu.uw.nmcgov.recommendme;

import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SavedActivity extends AppCompatActivity {

    private ImageButton thumbsUpBtn;
    private ImageButton thumbsDownBtn;
    private RCMDFirebase firebase;
    private String user;
    RecommendationTileGrid tileGridFragment;
    List<String> savedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        // Getting list of saved media titles
        if (isExternalStorageWritable()) {
            try {
                File dir;
                dir = Environment.getExternalStorageDirectory();

                File file = new File(Environment.getExternalStorageDirectory(), "mediaTitles.txt");

                BufferedReader reader = new BufferedReader(new FileReader(file));

                savedList = new ArrayList<String>();
                String line;

                while ((line = reader.readLine()) != null) {
                    savedList.add(line);
                }

                Log.v("SavedActivity", "" + savedList);
            }
            catch(IOException ioe){
                ioe.printStackTrace();
            }
        }

        // Fragment to display tile grid for saved media titles
//        FragmentManager fm = getSupportFragmentManager();
//
//        FragmentTransaction ft = fm.beginTransaction();
//        tileGridFragment = new RecommendationTileGrid();
//        ft.add(R.id.gridContainer3, tileGridFragment, "Grid");
//        ft.commit();
    }


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
