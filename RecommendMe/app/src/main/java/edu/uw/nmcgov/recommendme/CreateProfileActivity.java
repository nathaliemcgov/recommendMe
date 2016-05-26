package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreateProfileActivity extends Activity {
    private static final String TAG = "start";
    private EditFragment current;

    private String[] types;
    private int index;
    private RCMDFirebase firebase;
    private Button nextButton;
    private List<String> desertIslandList;
    private List<AutoCompleteTextView> texts;
    private AutoCompleteTextView field1;
    private AutoCompleteTextView field2;
    private AutoCompleteTextView field3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        index = 0;

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // Sets keyboard to always hidden on create
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        field1 = (AutoCompleteTextView) findViewById(R.id.searchMediaText1);
        field2 = (AutoCompleteTextView) findViewById(R.id.searchMediaText2);
        field3 = (AutoCompleteTextView) findViewById(R.id.searchMediaText3);

        // Adding autocomplete to all three fields
        texts = new LinkedList<AutoCompleteTextView>();
        texts.add(field1);
        texts.add(field2);
        texts.add(field3);

        String[] suggestions = {};
        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(suggestions));
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, lst);

        for (final AutoCompleteTextView searchMediaText : texts) {
            searchMediaText.setAdapter(adapter);
            searchMediaText.setThreshold(1);

            searchMediaText.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if(event.getAction() == KeyEvent.ACTION_UP)
                        firebase.autoComplete(searchMediaText.getText().toString(), adapter);
                    return false;
                }
            });
        }

        desertIslandList = new ArrayList<String>();

        types = new String[5];
        types[0] = "MOVIES";
        types[1] = "BOOKS";
        types[2] = "MUSICAL ARTISTS";
        types[3] = "Email Address";
        types[4] = "Password";

        index = 0;

        // Movies
        TextView text = (TextView) findViewById(R.id.mediaTypeDI);
        text.setText(types[index]);

        // Click listener for "Next" button
        nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the titles that the user entered in the three fields - add to list
                String firstField = field1.getText().toString();
                String secondField = field2.getText().toString();
                String thirdField = field3.getText().toString();
                // Checking if any of the fields were filled in + enforcing at least one desert island field to be filled
                if (firstField.length() > 0 || secondField.length() > 0 || thirdField.length() > 0) {
                    index++;
                    if (index < 3) {
                        // Enforcing at least one desert island field to be filled
                        // 3 titles
                        String titles = field1.getText().toString() + "," +
                                field2.getText().toString() + "," + field3.getText().toString();
                        desertIslandList.add(titles);

                        field1.getText().clear();
                        field2.getText().clear();
                        field3.getText().clear();

                        // Books, Music
                        TextView text = (TextView) findViewById(R.id.mediaTypeDI);
                        text.setText(types[index]);
                    } else {
                        List<String> singleMediaTypeDI = new ArrayList<String>();

                        // 3 titles
                        String titles = firstField + "," + secondField + "," + thirdField;
                        desertIslandList.add(titles);
                        Log.v("Desert Island List", desertIslandList.toString());

                        // Hide desert island entries
                        LinearLayout desertIslandEntry = (LinearLayout) findViewById(R.id.createProfileArea);
                        desertIslandEntry.setVisibility(View.INVISIBLE);

                        // Send user to recommendationsForYou
                        Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                        intent.putExtra("movies", desertIslandList.get(0));
                        intent.putExtra("books", desertIslandList.get(1));
                        intent.putExtra("music", desertIslandList.get(2));
                        startActivity(intent);
                    }
                } else {
                    toasted(types[index]);
                }
            }
        });
    }

    public void toasted(String mediaType) {
        CharSequence text = "Please input at least one " + mediaType.substring(0, mediaType.length() - 1).toLowerCase();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}

