package edu.uw.nmcgov.recommendme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;

public class ManageAccountActivity extends AppCompatActivity {

    private static final String TAG = "manage account activity";
    private String user;
    private RCMDFirebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_account);

        Bundle bundle = getIntent().getExtras();
        user = bundle.getString("user");   // User's email

        Firebase.setAndroidContext(this);
        firebase = new RCMDFirebase();

        if(user == null) user = "";

        Log.v(TAG, user);

        Button updateEmail = (Button) findViewById(R.id.updateEmailBtn);
        Button delete = (Button) findViewById(R.id.deleteProfile);

        updateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Intent intent = new Intent(V.getContext(), UpdateEmailActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Profile")
                        .setMessage("Are you sure you want to delete your profile?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebase.deleteUser(user);
                                Intent intent = new Intent(v.getContext(), StartPage.class);
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }
}
