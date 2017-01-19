package de.paulsapp.deskview;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import si.virag.fuzzydateformatter.FuzzyDateTimeFormatter;

import static de.paulsapp.deskview.R.attr.height;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
//    /**
//     * Whether or not the system UI should be auto-hidden after
//     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
//     */
  //  private static final boolean AUTO_HIDE = true;

//    /**
//     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
//     * user interaction before hiding the system UI.
//     */
  //  private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
  //  private static final int UI_ANIMATION_DELAY = 300;
    private final SimpleDateFormat _sdfWatchTime = new SimpleDateFormat("HH:mm");
 //   private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
//    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//            if (AUTO_HIDE) {
//                delayedHide(AUTO_HIDE_DELAY_MILLIS);
//            }
//            return false;
//        }
//    };
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    BroadcastReceiver _broadcastReceiver;
    private TextView clock;
    private Timer timer;
    private View mContentView;
    public Context context;
   // private final Runnable mHidePart2Runnable = new Runnable() {
//        @SuppressLint("InlinedApi")
//        @Override
//        public void run() {
//            // Delayed removal of status and navigation bar
//
//            // Note that some of these constants are new as of API 16 (Jelly Bean)
//            // and API 19 (KitKat). It is safe to use them, as they are inlined
//            // at compile-time and do nothing on earlier devices.
//
//        }
//    };

  //  private boolean mVisible;
//   private final Runnable mHideRunnable = new Runnable() {
//        @Override
//        public void run() {
//            hide();
//        }
//    };


    String lastUpdated;
//    String currentFeed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        context=this;
        mContentView = findViewById(R.id.clock);

        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        //  mControlsView.setVisibility(View.GONE);
//        mVisible = false;
//
//        // Schedule a runnable to remove the status and navigation bar after a delay
//        //  mHideHandler.removeCallbacks(mShowPart2Runnable);
//        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);

        //mVisible = true;
       // mContentView = findViewById(R.id.clock);


        // Set up the user interaction to manually show or hide the system UI.
//        mContentView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                toggle();
//            }
//        });



        clock = (TextView) findViewById(R.id.clock);
        clock.setText(_sdfWatchTime.format(new Date()));

        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.setOnErrorListener(mOnErrorListener);
       videoView.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {
//               FrameLayout frameLayout = (FrameLayout) findViewById(R.id.videoViewLayout);
//               FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
//               ViewGroup.LayoutParams layoutParams1 = frameLayout.getLayoutParams();
//               layoutParams1.width=ViewGroup.LayoutParams.MATCH_PARENT;
//               layoutParams1.height=ViewGroup.LayoutParams.MATCH_PARENT;
//               frameLayout.setLayoutParams(layoutParams1);
//               view.setLayoutParams(layoutParams);
//               DisplayMetrics dm=new DisplayMetrics();
//               getWindowManager().getDefaultDisplay().getMetrics(dm);
//               int height=dm.heightPixels;
//               int width = dm.widthPixels;
//               view.layout(view.getLeft(),view.getTop(),view.getLeft()+width,view.getTop()+height);
//               view.requestLayout();
//               view.invalidate();
                return false;
           }
       });


        startTimer();
        refreshFeeds();
        updateVideoSource(true);

//        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.screenBrightness = 0;
//        getWindow().setAttributes(params);
//        KeyguardManager km = (KeyguardManager) this.getSystemService(Context.KEYGUARD_SERVICE);
//        final KeyguardManager.KeyguardLock kl = km.newKeyguardLock("MyKeyguardLock");
//        kl.disableKeyguard();
//
//        PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
//        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP
//                | PowerManager.ON_AFTER_RELEASE, "MyWakeLock");
//        wakeLock.acquire();


    }

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            //Toast.makeText(this,"Wiedergabe Fehler",Toast.LENGTH_LONG).show();
            Log.e("videoView","Wiedergabe Fehler. what: "+what+" and extra: "+extra);
            updateVideoSource(false);
            return true;
        }
    };

public void updateVideoSource(boolean ignoreLastUpdated){
    Log.d("deskview","updateVideoSource gestartet. ignoreLastUpdated: "+ignoreLastUpdated);
    SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds",Context.MODE_PRIVATE);
    lastUpdated = sharedPreferences.getString("lastUpdated","");
    boolean newVideo = sharedPreferences.getBoolean("newVideo",false);
    //Log.d("updatedVideoSource","Es wird überprüft, ob upgedatet werden muss");
    if(!lastUpdated.equals("")&&newVideo|ignoreLastUpdated){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("newVideo",false);
        editor.apply();
        VideoView videoView = (VideoView) findViewById(R.id.videoView);
        videoView.stopPlayback();
        videoView.setVideoPath(Environment.getDataDirectory().toString()+"/data/de.paulsapp.deskview/"+lastUpdated+".mp4");
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

        Log.d("updatedVideoSource","Video Source wurde erfolgreich upgedated");

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });
    }else{
        Log.d("updatedVideoSource","Video Source wurde NICHT upgedated");
    }

}public void updateEpisodeDate(){
        Log.d("deskview","updateEpisodeDate gestartet.");
        SharedPreferences sharedPreferences = this.getSharedPreferences("de.paulsapp.deskview.feeds",Context.MODE_PRIVATE);
        TextView videoInfo = (TextView)findViewById(R.id.videoInfo);
        long lUClock = sharedPreferences.getLong(lastUpdated+"clock",0);
        Date date = new Date(lUClock);
        videoInfo.setText(lastUpdated+": "+FuzzyDateTimeFormatter.getTimeAgo(this,date));
    }

    public void startTimer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                refreshFeeds();

            }
        }, 10 * 1000, 3 * 60 * 1000); //alle 10min

    }


    public void refreshFeeds() {
        String url[][] = {{"tagesschau","http://www.tagesschau.de/export/video-podcast/webl/tagesschau/"},
                {"tagesschau100sekunden","http://www.tagesschau.de/export/video-podcast/webl/tagesschau-in-100-sekunden/"},
                {"tagesthemen","http://www.tagesschau.de/export/video-podcast/webl/tagesthemen/"},
                {"nachtmagazin","http://www.tagesschau.de/export/video-podcast/webl/nachtmagazin"}
        };


        for (int i = 0; i < url.length; i++) {
            Log.d("feed", "refreshFeeds gestartet. Feedname: "+url[i][0]+" Rss Url: " + url[i][1]);
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

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        // Trigger the initial hide() shortly after the activity has been
//        // created, to briefly hint to the user that UI controls
//        // are available.
//        delayedHide(100);
//    }

//    private void toggle() {
//        if (mVisible) {
//            hide();
//        } else {
//            show();
//        }
//    }

//    private void hide() {
//        // Hide UI first
//
//    }

//    @SuppressLint("InlinedApi")
//    private void show() {
//        // Show the system bar
//        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
//        mVisible = true;
//
//        // Schedule a runnable to display UI elements after a delay
//        mHideHandler.removeCallbacks(mHidePart2Runnable);
//       // mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
//    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
//    private void delayedHide(int delayMillis) {
//        mHideHandler.removeCallbacks(mHideRunnable);
//        mHideHandler.postDelayed(mHideRunnable, delayMillis);
//    }


    @Override
    public void onStart() {
        super.onStart();
        _broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context ctx, Intent intent) {
                if (intent.getAction().compareTo(Intent.ACTION_TIME_TICK) == 0)
                    clock.setText(_sdfWatchTime.format(new Date()));
                    updateEpisodeDate();
                    FullscreenActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateVideoSource(false);
                    }
                });
            }
        };

        registerReceiver(_broadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    public void onStop() {
        super.onStop();
        if (_broadcastReceiver != null)
            unregisterReceiver(_broadcastReceiver);
    }

}
