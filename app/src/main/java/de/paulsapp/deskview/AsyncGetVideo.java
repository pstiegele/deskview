package de.paulsapp.deskview;

import android.os.AsyncTask;
import android.util.Log;

public class AsyncGetVideo extends AsyncTask<Object, String, Boolean> {

    @Override
    protected Boolean doInBackground(Object... strings) {
        Log.d("feed","AsyncGetVideo gestartet");
        String url = (String) strings[0];
        String filename = (String) strings[1];
        GetFeeds getFeeds = new GetFeeds();
        try {
            Log.d("feed","AsyncGetVideo ruft nun downloadVideo() auf mit: url="+url+" und filename="+filename);
            getFeeds.downloadVideo(url,filename+".mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if(result){

        }

    }
}
