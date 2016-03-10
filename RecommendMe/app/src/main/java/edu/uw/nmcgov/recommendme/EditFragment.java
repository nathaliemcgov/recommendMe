package edu.uw.nmcgov.recommendme;

import android.app.Activity;
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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by austinweale on 3/5/16.
 */
public class EditFragment extends Fragment {
    private static final String TAG = "linked";

    private String type;
    Activity activity;
    private List<EditText> texts;
    private ArrayList<Button> change_buttons;

    public interface sendList {
        public void update(List<String> list);
    }

    public EditFragment(){
        activity = this.getActivity();
        change_buttons = new ArrayList<Button>();
    }

    public void onAttach(Context context){
        super.onAttach(context);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.edit_fragment, container, false);

        texts = new LinkedList<EditText>();

        texts.add((EditText)rootView.findViewById(R.id.entry_1));
        texts.add((EditText)rootView.findViewById(R.id.entry_2));
        texts.add((EditText)rootView.findViewById(R.id.entry_3));


        //get the type (movie/music/book)
        Bundle bundle = this.getArguments();
        if (bundle == null) {
            Log.v(TAG, "NO");
        } else {
            type = bundle.getString("type");
        }

        //different buttons for each editText
        Button button1 = (Button)(rootView.findViewById(R.id.change_1));
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });

        Button button2 = (Button)(rootView.findViewById(R.id.change_2));
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });

        Button button3 = (Button)(rootView.findViewById(R.id.change_3));
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View V) {
                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.entry_3)).getText().toString());
            }
        });
        change_buttons.add(button1);
        change_buttons.add(button2);
        change_buttons.add(button3);
        hideButtons();

        return rootView;

    }
    public List send() {
        List<String> list = new LinkedList<String>();

        for (EditText editText : texts) {
            String text = editText.getText().toString();
            if (text.length() > 0) {
                list.add(text);
            }
        }
        return list;
    }

    public void hideButtons(){
        Log.v(TAG, "change " + change_buttons.toString());
        for(int i = 0; i < change_buttons.size(); i++){

            change_buttons.get(i).setVisibility(View.GONE);
            change_buttons.get(i).setHeight(0);
        }
    }


}
