package edu.uw.nmcgov.recommendme;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by austinweale on 3/5/16.
 */
public class EditFragment extends Fragment {
    private static final String TAG = "linked";

    private String type;


    public EditFragment(){

    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.edit_fragment, container, false);

        //get the type (movie/music/book)
        Bundle bundle = this.getArguments();
        if(bundle == null){
            Log.v(TAG, "NO");
        }else{
            type = bundle.getString("type");
        }

        //different buttons for each editText
        ArrayList<ButtonLink> buttons = new ArrayList<>();
        buttons.add(new ButtonLink((Button)(getActivity().findViewById(R.id.change_1)),
                        (EditText)(getActivity().findViewById(R.id.entry_1))));
        buttons.add(new ButtonLink((Button)(getActivity().findViewById(R.id.change_2)),
                (EditText)(getActivity().findViewById(R.id.entry_2))));
        buttons.add(new ButtonLink((Button)(getActivity().findViewById(R.id.change_3)),
                (EditText)(getActivity().findViewById(R.id.entry_3))));

        for(ButtonLink button: buttons) {
            button.click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View V) {
                    Log.v(TAG, "changed " + type);
                }
            });
        }

        return rootView;

    }

    private class ButtonLink {

        private Button click;
        private EditText edit;

        public ButtonLink(Button button, EditText edit){
            this.click = button;
            this.edit = edit;
        }
    }

}
