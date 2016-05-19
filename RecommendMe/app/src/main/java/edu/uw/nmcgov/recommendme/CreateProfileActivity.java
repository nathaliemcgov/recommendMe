package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateProfileActivity extends Activity implements EditFragment.sendList {
    private static final String TAG = "start";
    private String[] types;
    private int index;
    private String userEmail;
    private TextView emailText;
    private EditFragment current;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        index = 0;

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        types = new String[4];
        types[0] = "email";
        types[1] = "movie";
        types[2] = "music";
        types[3] = "book";

        // Entering user's email address
        emailText = (TextView)findViewById(R.id.type_box);
        emailText.setText(types[index]);
        index++;



        Button button = (Button)findViewById(R.id.next_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (index == 1) {
                    EditText email = (EditText) findViewById(R.id.email_pass);
                    userEmail = email.getText().toString(); // User's email address
                    Log.v("email", userEmail);
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("name", userEmail);
                    firebase.createUser(map);
                }
                if(index < types.length) {
                    Log.v("index", index + "");
                    if(index >= 1){
                        if(current != null){
                            List<String> list = ((EditFragment)current).send();
                            Log.v("LIST222", list.toString());
                            firebase.setManyLikes(list, userEmail, types[index - 1]);
                            for(String input : list) {
                                Log.v(TAG, input);
                            }
                        }

                        current = addFragment(types[index]);
                        ((EditFragment)current).hideButtons();

                        EditText editText = (EditText)findViewById(R.id.email_pass);
                        editText.setVisibility(View.INVISIBLE);
                        editText.setHeight(0);
                        /*TextView text = (TextView)findViewById(R.id.confirm);
                        text.setVisibility(View.INVISIBLE);
                        text.setHeight(0);
                        EditText confirm = (EditText)findViewById(R.id.email_pass_confirm);
                        confirm.setVisibility(View.INVISIBLE);
                        confirm.setHeight(0);*/
                    }

                    Log.v(TAG, "changed " + types[index]);
                    TextView text = (TextView) findViewById(R.id.type_box);
                    text.setText(types[index]);
                    index++;
                } else {
                    List<String> list = ((EditFragment)current).send();
                    Log.v("LIST222", list.toString());
                    firebase.setManyLikes(list, userEmail, types[index - 1]);
                    for(String input : list) {
                        Log.v(TAG, input);
                    }
                    Log.v(TAG, "Account Created!");
                    sendToRecommendationsForYou();
                }
            }
        });
    }

    // Sends user to RecommendationsForYou screen w/ username
    public void sendToRecommendationsForYou(){
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", userEmail);
        startActivity(intent);
    }

    public EditFragment addFragment(String type) {
        Bundle bundle = new Bundle();
        bundle.putString("type", type);

        Fragment edit = new EditFragment();
        edit.setArguments(bundle);
        getFragmentManager().beginTransaction()
                .replace(R.id.add_box, edit)
                .commit();
        return (EditFragment)edit;

    }

    @Override
    public void update(List<String> list) {
        Log.v(TAG, list.toString());
    }
}

