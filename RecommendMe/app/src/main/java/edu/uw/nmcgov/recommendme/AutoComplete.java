package edu.uw.nmcgov.recommendme;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by iguest on 3/8/16.
 */
public class AutoComplete {

    public AutoComplete() {

    }

    public void testWeb(String autocomplete) {
        Log.v("test", "yahhh");
        String str="https://students.washington.edu/tylerj11/recommendMe/phpWebService/uploadFiles.php?media=" + autocomplete;
        try {
            new testWebService().execute(new URL(str));
        } catch(Exception e){
            Log.v("test", e.toString());
        }
    }

    private class testWebService extends AsyncTask<URL, Integer, String> {

        protected String doInBackground(URL... urls) {
            try {
                for (URL url : urls) {
                    URLConnection urlc = url.openConnection();
                    BufferedReader bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                    String line;
                    while ((line = bfr.readLine()) != null) {
                        Log.v("TESTWEB", line);
                    }
                }
            } catch (Exception e) {
                Log.v("error", e.toString());
            }
            return "done2";

        }
        protected void onPostExecute(String result) {
            Log.v("test", result);
        }
    }
}
