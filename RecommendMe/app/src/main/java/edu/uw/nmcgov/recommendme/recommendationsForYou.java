package edu.uw.nmcgov.recommendme;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;

public class RecommendationsForYou extends AppCompatActivity {

    private GridView tileGrid;
    private ArrayList<String> recommendationList;
    ArrayAdapter<String> adapter;

    static final String[] recommendations = new String[] {
            "The Departed", "The Illiad"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendations_for_you);

        tileGrid = (GridView) findViewById(R.id.recommendationList);

        recommendationList = new ArrayList<String>();
        recommendationList.add("");

        adapter = new ArrayAdapter<String>(this,
                R.layout.recommendation_element, recommendations);

        tileGrid.setAdapter(adapter);
    }
}
