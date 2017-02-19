package de.paulsapp.deskview;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by pstiegele on 15.01.2017. Hell yeah!
 */

public class GetFeeds {

    OkHttpClient client = new OkHttpClient();
    public static Context context = null;

    String run(String url) throws IOException{
        Request request = new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    void downloadVideo(String downloadUrl,String filename) throws Exception{
        Log.d("feed","downloadVideo() gestartet");
        Request request = new Request.Builder().url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if(!response.isSuccessful()){
            throw new IOException("Failed to Download: "+downloadUrl);
        }
        File dir = new File(Environment.getDataDirectory().toString());
        File file = new File(dir+"/data/de.paulsapp.deskview",filename);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream fos = new FileOutputStream(file,false);
        Log.d("feed","es wird geschrieben");
        InputStream inputStream = response.body().byteStream();

        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        int counter=0;
        while((len = inputStream.read(buffer))!=-1){
            fos.write(buffer,0,len);
            counter++;
         //   Log.d("feed","und geschrieben...");
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("de.paulsapp.deskview.feeds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("downloadCounter",sharedPreferences.getInt("downloadCounter",0)+counter*1024);
        Log.d("deskviewstats","downloadCounter wurde um "+counter*1024+" erhöht.");
        editor.apply();
        fos.close();
        Log.d("feed","alles fertig");
    }
}
