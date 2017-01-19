package de.paulsapp.deskview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AsyncGetFeeds extends AsyncTask<Object, String, Object[]> {

    @Override
    protected Object[] doInBackground(Object... params) { //Object[0]=Feed Url (String), Object[1]=Context, Object[2]=Feedname (String)
        String feedname = (String) params[2];
        GetFeeds getFeeds = new GetFeeds();
        String response="";
        try {
            response = getFeeds.run((String)params[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pubDate = response;
        pubDate = pubDate.split("<pubDate>")[2];
        pubDate = pubDate.split("</pubDate>")[0];
        pubDate = pubDate.split(", ")[1];
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss ZZZZ");
        Date date=new Date();
        try {
            date = sdf.parse(pubDate);
            Log.d("feedDate",date.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        response=response.split("<enclosure url=\"")[1];
        response=response.split("\"")[0];
        Log.d("feed","Url extrahiert: "+response);
        Context context = (Context) params[1];
        SharedPreferences sharedPreferences = context.getSharedPreferences("de.paulsapp.deskview.feeds",Context.MODE_PRIVATE);
        String lastURL = sharedPreferences.getString(feedname,"");
        Log.d("feed","lastURL lautet: "+lastURL);
       if (!response.equals(lastURL)){
        //if (true){
                Object[] ob = new Object[3]; //ob[0]=ob neue Episode gefunden oder nicht, ob[1]=Download Url, ob[2]=feedname
                ob[0]=true;
                ob[1]=response;
                ob[2]=feedname;

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(feedname,response);
                Log.d("feedDate","gespeichert wird: "+date.getTime());
                editor.putLong(feedname+"clock",date.getTime());
                editor.putString("lastUpdated",feedname);
                editor.putBoolean("newVideo",true);
                editor.apply();
                Log.d("feed","Download wird gestartet");
                return ob;
            }
        Log.d("feed","Download wird nicht gestartet");
        Object ob[] = new Object[3];
        ob[0]=false;

        return ob;
    }

    @Override
    protected void onPostExecute(Object[] result) {
        //Log.d("feed","onPostExecute() in downloadFeed");
        boolean boo = (boolean) result[0];
        if(boo==true){
            Log.d("feed","onPostExecute startet nun AsyncGetVideo");
            AsyncGetVideo asyncGetVideo = new AsyncGetVideo();
            Object[] obj = new Object[2];
            obj[0]=result[1];
            obj[1]=result[2];
            asyncGetVideo.execute(obj);
        }

    }

    @Override
    protected void onPreExecute() {}

}

