package edu.uw.nmcgov.recommendme;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by austinweale on 3/9/16.
 */
public class ExistingLogin extends Activity {

    private static final String TAG = "existing";
    private int count;
    private EditText usernameField;
    private String username;
    private HashMap<String, String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_login_activity);
        count = 0;
        map = new HashMap<String, String>();

        changeVisibility(R.id.existing_password, View.GONE, 0);

        Button button = (Button)findViewById(R.id.proceed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                changeVisibility(R.id.existing_password, View.VISIBLE, ActionBar.LayoutParams.WRAP_CONTENT);
                usernameField = (EditText) findViewById(R.id.existing_email_entry);
                username = usernameField.getText().toString().trim();
                recommendationsForYou();
                if (count < 1) {
                    changeVisibility(R.id.existing_password, View.VISIBLE, ActionBar.LayoutParams.WRAP_CONTENT);
                    usernameField = (EditText) findViewById(R.id.existing_email_entry);
                    username = usernameField.getText().toString();
                    changeVisibility(R.id.existing_email, View.GONE, 0);
                    count++;
                } else {
                    recommendationsForYou();
                    Log.v(TAG, "back");

                    EditText edit = (EditText) findViewById(R.id.existing_email_entry);
                    if (edit.getText().length() == 0) {
                        toasted("email");
                    } else {
                        map.put("email", edit.getText().toString());
                        /*changeVisibility(R.id.existing_password, View.VISIBLE, ActionBar.LayoutParams.WRAP_CONTENT);
                        changeVisibility(R.id.existing_email, View.GONE, 0);*/
                    }
                }
            }
        });
    }

    public void recommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void toasted(String type){
        Context context = getApplicationContext();
        CharSequence text = "please enter a " + type;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void changeVisibility(int id, int visibility, int height){
        LinearLayout password = (LinearLayout)findViewById(id);
        password.setVisibility(visibility);
        ViewGroup.LayoutParams params = password.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = height;
    }
}
