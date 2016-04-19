package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by austinweale on 3/3/16.
 */
public class ProfileFragment extends Fragment {

    private static final String TAG = "profile";
    private static final int REQUEST_PICTURE = 1;


    /*allows interaction with the start page
    public interface OnButtonSelectionListener{
        void onLinkedSelected();
        void onSavedSelected();
        void onPicksSelected();
    }

    public interface OnProfileSelectedListener {
        void onProfileSelected();
    }*/

    private String user;

    public ProfileFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        final View rootView = inflater.inflate(R.layout.profile_fragment, container, false);

        Bundle bundle = this.getArguments();
        user = bundle.getString("user", "");
        if(user == null) user = "";
        Log.v(TAG, user+"test");

        Button login = (Button) rootView.findViewById(R.id.login);
        Button linked = (Button) rootView.findViewById(R.id.linked);
        Button saved = (Button) rootView.findViewById(R.id.saved);
        Button picked = (Button) rootView.findViewById(R.id.picked);
        Button signUp = (Button) rootView.findViewById(R.id.signUp);
        Button logout = (Button) rootView.findViewById(R.id.logout);

        if(!user.equals("")) {
            Log.v(TAG, user +"test");

            login.setVisibility(View.INVISIBLE);
            signUp.setVisibility(View.INVISIBLE);

            linked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
//                    Log.v(TAG, "linked");
//                    AccountsFragment accounts = new AccountsFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, accounts)
//                            .commit();

                }
            });

            saved.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
//                    Log.v(TAG, "saved");
//                    SavedFragment saved = new SavedFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, saved)
//                            .commit();


                }
            });

            picked.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
//                    Log.v(TAG, "picked");
//                    PicksFragment picks = new PicksFragment();
//                    getFragmentManager().beginTransaction()
//                            .replace(R.id.mainContainer, picks)
//                            .commit();

                }
            });

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(getActivity(), StartPage.class);
                    startActivity(intent);
                }
            });
        } else {

            linked.setVisibility(View.INVISIBLE);
            saved.setVisibility(View.INVISIBLE);
            picked.setVisibility(View.INVISIBLE);
            logout.setVisibility(View.INVISIBLE);

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(getActivity(), ExistingLogin.class);
                    startActivity(intent);


                }
            });

            signUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Intent intent = new Intent(getActivity(), CreateProfileActivity.class);
                    startActivity(intent);


                }
            });
        }
        return rootView;

    }
}
