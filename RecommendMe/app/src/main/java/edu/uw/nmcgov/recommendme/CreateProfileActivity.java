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
import java.util.List;
import java.util.Map;

public class CreateProfileActivity extends Activity {
    private static final String TAG = "start";
    private EditFragment current;

    private String[] types;
    private int index;
    private AutoCompleteTextView desertIslandTextViews;
    private RCMDFirebase firebase;
    private Button nextButton;
    private List<String> desertIslandList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);
        index = 0;

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        // AUTOCOMPLETE
        // Sets keyboard to always hidden on create
//        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        // The text entry so user can search
//        desertIslandTextViews = (AutoCompleteTextView) findViewById(R.id.searchMediaText);
//        String[] suggestions = {};
//        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(suggestions));
//        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, lst);
//
//        desertIslandTextViews.setAdapter(adapter);
//        desertIslandTextViews.setThreshold(1);
//
//        desertIslandTextViews.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if(event.getAction() == KeyEvent.ACTION_UP)
//                    firebase.autoComplete(desertIslandTextViews.getText().toString(), adapter);
//                return false;
//            }
//        });

        desertIslandList = new ArrayList<String>();

        types = new String[5];
        types[0] = "Movies";
        types[1] = "Books";
        types[2] = "Musical Artists";
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
                // AUTOCOMPLETE
                // Hides keyboard when search button is clicked
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(desertIslandTextViews.getWindowToken(), 0);

                // Get the titles that the user entered in the three fields - add to list
                EditText field1 = (EditText) findViewById(R.id.searchMediaText1);
                EditText field2 = (EditText) findViewById(R.id.searchMediaText2);
                EditText field3 = (EditText) findViewById(R.id.searchMediaText3);

                String firstField = field1.getText().toString();
//                String secondField =
                if (firstField.length() > 0) {
                    index++;
                    if (index < 3) {
                        // Enforcing at least one desert island field to be filled
//                        String firstField = field1.getText().toString();
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
    //                    EditText field1 = (EditText) findViewById(R.id.searchMediaText1);
                        EditText field2 = (EditText) findViewById(R.id.searchMediaText2);
                        EditText field3 = (EditText) findViewById(R.id.searchMediaText3);

                        // 3 titles
                        String titles = field1.getText().toString() + "," +
                                field2.getText().toString() + "," + field3.getText().toString();
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
        CharSequence text = "Please input at least one " + mediaType;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, text, duration);
        toast.show();
    }
}

