package edu.uw.nmcgov.recommendme;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by austinweale on 3/5/16.
 */
public class EditFragment extends Fragment {
//    private static final String TAG = "linked";
//
//    private String type;
//    Activity activity;
//    private List<AutoCompleteTextView> texts;
//    private ArrayList<Button> change_buttons;
//    private RCMDFirebase firebase;
//
//    public interface sendList {
//        public void update(List<String> list);
//    }
//
//    public EditFragment(){
//        activity = this.getActivity();
//        change_buttons = new ArrayList<Button>();
//    }
//
//    public void onAttach(Context context){
//        super.onAttach(context);
//    }
//
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
//        final View rootView = inflater.inflate(R.layout.edit_fragment, container, false);
//
//        Firebase.setAndroidContext(this.getActivity());
//        firebase = new RCMDFirebase();
//
//        texts = new LinkedList<AutoCompleteTextView>();
//
//        texts.add((AutoCompleteTextView) rootView.findViewById(R.id.searchMediaText1));
//        texts.add((AutoCompleteTextView) rootView.findViewById(R.id.searchMediaText2));
//        texts.add((AutoCompleteTextView) rootView.findViewById(R.id.searchMediaText3));
//
//        String[] suggestions = {};
//        ArrayList<String> lst = new ArrayList<String>(Arrays.asList(suggestions));
//        final ArrayAdapter adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, lst);
//
//
//        for (final AutoCompleteTextView searchMediaText : texts) {
//            searchMediaText.setAdapter(adapter);
//            searchMediaText.setThreshold(1);
//
//            searchMediaText.setOnKeyListener(new View.OnKeyListener() {
//                @Override
//                public boolean onKey(View v, int keyCode, KeyEvent event) {
//                    if(event.getAction() == KeyEvent.ACTION_UP)
//                        firebase.autoComplete(searchMediaText.getText().toString(), adapter);
//                    return false;
//                }
//            });
//        }
//
//        //get the type (movie/music/book)
//        Bundle bundle = this.getArguments();
//        if (bundle == null) {
//            Log.v(TAG, "NO");
//        } else {
//            type = bundle.getString("type");
//        }
//
//        //different buttons for each editText
//        Button button1 = (Button)(rootView.findViewById(R.id.change_1));
//        button1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.searchMediaText1)).getText().toString());
//            }
//        });
//
//        Button button2 = (Button)(rootView.findViewById(R.id.change_2));
//        button2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.searchMediaText2)).getText().toString());
//            }
//        });
//
//        Button button3 = (Button)(rootView.findViewById(R.id.change_3));
//        button3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View V) {
//                Log.v(TAG, "changed " + type + ((EditText) rootView.findViewById(R.id.searchMediaText3)).getText().toString());
//            }
//        });
//
//        change_buttons.add(button1);
//        change_buttons.add(button2);
//        change_buttons.add(button3);
//        hideButtons();
//
//        return rootView;
//
//    }
//
//    public List send() {
//        List<String> list = new LinkedList<String>();
//
//        for (AutoCompleteTextView editText : texts) {
//            String text = editText.getText().toString();
//            if (text.length() > 0) {
//                list.add(text);
//            }
//        }
//        return list;
//    }
//
//    public void hideButtons(){
//        Log.v(TAG, "change " + change_buttons.toString());
//        for(int i = 0; i < change_buttons.size(); i++){
//
//            change_buttons.get(i).setVisibility(View.GONE);
//            change_buttons.get(i).setHeight(0);
//        }
//    }
}
