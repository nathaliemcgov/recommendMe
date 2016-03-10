package edu.uw.nmcgov.recommendme;

import android.app.ActionBar;
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

/**
 * Created by austinweale on 3/9/16.
 */
public class ExistingLogin extends AppCompatActivity {

    private static final String TAG = "existing";
    private int count;

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
                Log.v(TAG, "back");
                if(count < 1) {
                    EditText edit = (EditText)findViewById(R.id.existing_email_entry);
                    if(edit.getText().length() == 0){
                        toasted("email");
                    }else {
                        changeVisibility(R.id.existing_password, View.VISIBLE, ActionBar.LayoutParams.WRAP_CONTENT);
                        changeVisibility(R.id.existing_email, View.GONE, 0);
                        count++;
                    }
                }else{
                    EditText edit = (EditText)findViewById(R.id.existing_password_entry);
                    if(edit.getText().length() == 0){
                        toasted("password");
                    }else {
                        sendToNext();
                    }
                }
            }
        });

    }

    public void toasted(String type){
        Context context = getApplicationContext();
        CharSequence text = "please enter a " + type;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void sendToNext(){
        Intent intent = new Intent(this, ProfileActivity.class);
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
