package de.paulsapp.deskview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

public class FullscreenActivity extends AppCompatActivity {

    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm:ss");
    public Context context;
    BroadcastReceiver _broadcastReceiver;
    BroadcastReceiver _cameraBroadcastReceiver;
    String lastUpdated;
    private TextClock clock;
    private Timer timer;
    private View mContentView;
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.e("videoView", "Wiedergabe Fehler. what: " + what + " and extra: " + extra);
            updateVideoSource(false);
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        GetFeeds.context = this;

        setContentView(R.layout.activity_fullscreen);
        context = this;
        clock = (TextClock) findViewById(R.id.clock);
        clock.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);




        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnErrorListener(mOnErrorListener);


        startTimer();
        refreshFeeds();
        updateVideoSource(true);
        //updateCameraView();

    }

    private void updateCameraView() {
        new DownloadImageTask((ImageView) findViewById(R.id.cameraView))
                .execute("http://192.168.2.149:8080/shot.jpg");
    }

    public void updateVideoSource(boolean ignoreLastUpdated) {
        Log.d("deskview", "updateVideoSource gestartet. ignoreLastUpdated: " + ignoreLastUpdated);
        SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds", Context.MODE_PRIVATE);
        lastUpdated = sharedPreferences.getString("lastUpdated", "");
        boolean newVideo = sharedPreferences.getBoolean("newVideo", false);
        //Log.d("updatedVideoSource","Es wird überprüft, ob upgedatet werden muss");
        if (!lastUpdated.equals("") && newVideo | ignoreLastUpdated) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("updateCounter", sharedPreferences.getInt("updateCounter", 0) + 1);
            Log.d("deskviewstats","UpdateCounter wurde um eins erhöht.");
            editor.putBoolean("newVideo", false);
            editor.apply();
            VideoView videoView = (VideoView) findViewById(R.id.videoView);
            videoView.stopPlayback();
            videoView.setVideoPath(Environment.getDataDirectory().toString() + "/data/de.paulsapp.deskview/" + lastUpdated + ".mp4");
            videoView.start();
            updateEpisodeDate();
            android.widget.MediaController mc = new MediaController(this);
            mc.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            mc.setAnchorView(videoView);
            videoView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            videoView.setMediaController(mc);
            FrameLayout frameLayout = (FrameLayout) findViewById(R.id.videoViewLayout);
            frameLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            Log.d("updatedVideoSource", "Video Source wurde erfolgreich upgedated");

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mediaPlayer.setLooping(true);
                }
            });
        } else {
            Log.d("updatedVideoSource", "Video Source wurde NICHT upgedated");
        }

    }

    public void updateEpisodeDate() {
        Log.d("deskview", "updateEpisodeDate gestartet.");
        SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds", Context.MODE_PRIVATE);
        TextView videoInfo = (TextView) findViewById(R.id.videoInfo);
        long lUClock = sharedPreferences.getLong(lastUpdated + "clock", 0);
        Date date = new Date(lUClock);
        videoInfo.setText(lastUpdated + ": " + FuzzyDateTimeFormatter.getTimeAgo(this, date));
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshFeeds();

            }
        }, 10 * 1000, 3 * 60 * 1000); //alle 3min

    }

    public void updateStats() {
        SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds", Context.MODE_PRIVATE);
        final String txt = "playtimeCounter: " + sharedPreferences.getInt("playtimeCounter", 0) + "\ndownloadCounter: " + sharedPreferences.getInt("downloadCounter", 0)+"\nupdateCounter: "+sharedPreferences.getInt("updateCounter", 0);

        FullscreenActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tv = (TextView) findViewById(R.id.tvStats);
                //tv.setText(txt);
            }
        });
    }

    public void refreshFeeds() {
        String url[][] = {{"tagesschau", "http://www.tagesschau.de/export/video-podcast/webl/tagesschau/"},
                {"tagesschau100sekunden", "http://www.tagesschau.de/export/video-podcast/webl/tagesschau-in-100-sekunden/"},
                {"tagesthemen", "http://www.tagesschau.de/export/video-podcast/webl/tagesthemen/"},
                {"nachtmagazin", "http://www.tagesschau.de/export/video-podcast/webl/nachtmagazin"}
        };
        SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("playtimeCounter", sharedPreferences.getInt("playtimeCounter", 0) + 3);
        Log.d("deskviewstats","playtimeCounter wurde um drei erhöht.");
        editor.apply();
        updateStats();

        for (int i = 0; i < url.length; i++) {
            Log.d("feed", "refreshFeeds gestartet. Feedname: " + url[i][0] + " Rss Url: " + url[i][1]);
            Object tr[] = new Object[3];
            tr[0] = url[i][1];
            tr[1] = getApplicationContext();
            tr[2] = url[i][0];
            AsyncGetFeeds asyncGetFeeds = new AsyncGetFeeds();
            asyncGetFeeds.execute(tr);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateVideoSource(true);
        if (timer == null) {
            startTimer();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        SpannableString span1 = new SpannableString("HH:mm");
        SpannableString span2 = new SpannableString("ss");
        span1.setSpan(new RelativeSizeSpan(1.00f), 0, 4, 0);
        span2.setSpan(new RelativeSizeSpan(0.40f), 0, 2, 0);
        span2.setSpan(new ForegroundColorSpan(Color.GRAY),0,2,0);

        clock.setFormat24Hour((Spanned) (TextUtils.concat(span1, span2)));
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                //clock.setText(_sdfWatchTime.format(new Date()));
                updateEpisodeDate();
                FullscreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVideoSource(false);
                    }
                });
            }
        };

        _cameraBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {

                //updateCameraView();
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
        //registerReceiver(_cameraBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null) {
            unregisterReceiver(_broadcastReceiver);
        }
        if (_cameraBroadcastReceiver != null) {
            //unregisterReceiver(_cameraBroadcastReceiver);
        }
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }


}
