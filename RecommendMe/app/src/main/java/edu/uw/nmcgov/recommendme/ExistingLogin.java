package edu.uw.nmcgov.recommendme;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
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

/**
 * Created by austinweale on 3/9/16.
 */
public class ExistingLogin extends Activity {

    private static final String TAG = "existing";
    private int count;
    private EditText usernameField;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_login_activity);
        count = 0;

        changeVisibility(R.id.existing_password, View.GONE, 0);

        Button button = (Button)findViewById(R.id.proceed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                if (count < 1) {
                    changeVisibility(R.id.existing_password, View.VISIBLE, ActionBar.LayoutParams.WRAP_CONTENT);
                    usernameField = (EditText) findViewById(R.id.existing_email_entry);
                    username = usernameField.getText().toString();
                    changeVisibility(R.id.existing_email, View.GONE, 0);
                    count++;
                } else {
                    recommendationsForYou();
                }
            }
        });
    }

    public void recommendationsForYou() {
        Intent intent = new Intent(this, RecommendationsForYou.class);
        intent.putExtra("user", username);
        startActivity(intent);
    }

    public void changeVisibility(int id, int visibility, int height){
        LinearLayout password = (LinearLayout)findViewById(id);
        password.setVisibility(visibility);
        ViewGroup.LayoutParams params = password.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.height = height;
    }
}
