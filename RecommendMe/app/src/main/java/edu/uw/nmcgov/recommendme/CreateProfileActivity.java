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
        types[1] = "Music";
        types[2] = "Books";
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
                index++;

                if (index < 3) {
                    // Get the titles that the user entered in the three fields - add to list
                    List<String> singleMediaTypeDI = new ArrayList<String>();
                    EditText field1 = (EditText) findViewById(R.id.searchMediaText1);
                    EditText field2 = (EditText) findViewById(R.id.searchMediaText2);
                    EditText field3 = (EditText) findViewById(R.id.searchMediaText3);

                    // 3 titles
                    String titles = types[index] + "," + field1.getText().toString() + "," +
                            field2.getText().toString() + "," + field3.getText().toString() + "|";
                    desertIslandList.add(titles);

                    field1.getText().clear();
                    field2.getText().clear();
                    field3.getText().clear();

                    // Books, Music
                    TextView text = (TextView) findViewById(R.id.mediaTypeDI);
                    text.setText(types[index]);

                } else {
                    List<String> singleMediaTypeDI = new ArrayList<String>();
                    EditText field1 = (EditText) findViewById(R.id.searchMediaText1);
                    EditText field2 = (EditText) findViewById(R.id.searchMediaText2);
                    EditText field3 = (EditText) findViewById(R.id.searchMediaText3);

                    // 3 titles
                    String titles = types[index] + "," + field1.getText().toString() + "," +
                            field2.getText().toString() + "," + field3.getText().toString() + "|";
                    desertIslandList.add(titles);

                    // Hide desert island entries
                    LinearLayout desertIslandEntry = (LinearLayout) findViewById(R.id.createProfileArea);
                    desertIslandEntry.setVisibility(View.INVISIBLE);

                    // Send user to recommendationsForYou
                    Intent intent = new Intent(getApplicationContext(), EmailPasswordActivity.class);
                    intent.putExtra("movies", desertIslandList.get(0));
                    intent.putExtra("books", desertIslandList.get(1));
                    intent.putExtra("music", desertIslandList.get(2));
                    startActivity(intent);

                    // Replacing current screen with fragment
//                    android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//
//                    FragmentTransaction ft = fm.beginTransaction();
//                    EmailPasswordFragment emailPasswordFrag = new EmailPasswordFragment();
//
//                    Bundle desertIslandBundle = new Bundle();
//                    desertIslandBundle.putString("movies", desertIslandList.get(0));
//                    desertIslandBundle.putString("books", desertIslandList.get(1));
//                    desertIslandBundle.putString("music", desertIslandList.get(2));
//                    emailPasswordFrag.setArguments(desertIslandBundle);
//
//                    ft.add(R.id.emailPassContainer, emailPasswordFrag, "EmailPass");
//                    ft.commit();
                }
            }
        });

        // Email address and password
//        if (index > 2 && index < types.length) {
//            // Replacing current screen with fragment
//            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
//
//            FragmentTransaction ft = fm.beginTransaction();
//            EmailPasswordFragment emailPasswordFrag = new EmailPasswordFragment();
//
//            Bundle desertIslandBundle = new Bundle();
//            desertIslandBundle.putString("movies", desertIslandList.get(0));
//            desertIslandBundle.putString("books", desertIslandList.get(1));
//            desertIslandBundle.putString("music", desertIslandList.get(2));
//            emailPasswordFrag.setArguments(desertIslandBundle);
//
//            ft.add(R.id.createProfileArea, emailPasswordFrag, "EmailPass");
//            ft.commit();
//        }
    }

//        Button button = (Button)findViewById(R.id.next_button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
////                if (index == 1) {
////                    EditText email = (EditText) findViewById(R.id.email_pass);
////                    userEmail = email.getText().toString(); // User's email address
////                    Log.v("email", userEmail);
////                    Map<String, String> map = new HashMap<String, String>();
////                    map.put("name", userEmail);
////                    firebase.createUser(map);
////                }
//                if (index < types.length) {
//                    Log.v("index", index + "");
//
//                    if (index >= 1) {
//                        if (current != null) {
//                            List<String> list = ((EditFragment)current).send();
//                            Log.v("LIST222", list.toString());
//
//                            // Logging likes into Firebase
//                            // Make a list of lists of 3 books, 3 movies, 3 music artists
////                            firebase.setManyLikes(list, userEmail, types[index - 1]);
//                        }
//
//                        current = addFragment(types[index]);
//                        ((EditFragment)current).hideButtons();
//
//                        /*TextView text = (TextView)findViewById(R.id.confirm);
//                        text.setVisibility(View.INVISIBLE);
//                        text.setHeight(0);
//                        EditText confirm = (EditText)findViewById(R.id.email_pass_confirm);
//                        confirm.setVisibility(View.INVISIBLE);
//                        confirm.setHeight(0);*/
//                    }
//
//                    Log.v(TAG, "changed " + types[index]);
////                    TextView text = (TextView) findViewById(R.id.mediaTypeDI);
////                    text.setText(types[index]);
////                    index++;
//                } else {
//                    List<String> list = ((EditFragment)current).send();
//                    Log.v("LIST222", list.toString());
//
//                    // Logging likes into Firebase
////                    firebase.setManyLikes(list, userEmail, types[index - 1]);
//                    Log.v(TAG, "Account Created!");
//                    sendToRecommendationsForYou();
//                }
//            }
//        });
//    }
//
//    // Sends user to RecommendationsForYou screen w/ username
//    public void sendToRecommendationsForYou(){
//        Intent intent = new Intent(this, RecommendationsForYou.class);
//        startActivity(intent);
//    }
//
//    public EditFragment addFragment(String type) {
//        Bundle bundle = new Bundle();
//        bundle.putString("type", type);
//
//        Fragment edit = new EditFragment();
//        edit.setArguments(bundle);
//        getFragmentManager().beginTransaction()
//                .replace(R.id.add_box, edit)
//                .commit();
//        return (EditFragment)edit;
//    }
//
//    @Override
//    public void update(List<String> list) {
//        Log.v(TAG, list.toString());
//    }
}

