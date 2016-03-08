package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartPage extends AppCompatActivity {

    private static final String TAG = "start";
    private String[] types;
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        index = 0;

        types = new String[3];
        types[0] = "movie";
        types[1] = "music";
        types[2] = "book";

        addFragment(types[0]);
        TextView text = (TextView)findViewById(R.id.type_box);
        text.setText(types[index]);
        index++;

        Button button = (Button)findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if(index < types.length) {
                    Log.v(TAG, "changed " + types[index]);
                    TextView text = (TextView) findViewById(R.id.type_box);
                    text.setText(types[index]);
                    index++;
                }else{
                    Log.v(TAG, "to next page!");
                    sendToNext();
                }
            }
        });

    }

    public void sendToNext(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void addFragment(String type){
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        Fragment edit = new EditFragment();
        edit.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.add_box, edit)
                .commit();
    }
}