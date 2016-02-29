package com.example.dan.myapplication;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    private TextView tempTextView;
    private Handler mHandler = new Handler();
    private long startTime;
    private long elapsedTime = 0;
    private final int REFRESH_RATE = 10;
    private String hours,minutes,seconds,milliseconds;
    private long secs,mins,hrs;
    private boolean counting = false;
    private SharedPreferences prefs;
    private SharedPreferences.Editor ed;
    private String START_TIME_KEY = "start_time_key";
    private String COUNTING_KEY = "counting_key";
    private String ELAPSED_TIME_KEY = "elapsed_key";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Typeface font = Typeface.createFromAsset(getAssets(), "altehaasgroteskbold.ttf");
        tempTextView = (TextView) findViewById(R.id.timer);
        tempTextView.setTypeface(font);
        tempTextView = (TextView) findViewById(R.id.timerMs);
        tempTextView.setTypeface(font);
        font = Typeface.createFromAsset(getAssets(), "coolvetica.ttf");
        Button tempBtn = (Button)findViewById(R.id.startButton);
        tempBtn.setTypeface(font);
        tempBtn = (Button)findViewById(R.id.resetButton);
        tempBtn.setTypeface(font);
        tempBtn = (Button)findViewById(R.id.stopButton);
        tempBtn.setTypeface(font);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("D7C3FED6C0273D67D38E0186CFA3B220")
                .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        prefs = getPreferences(MODE_PRIVATE);
        ed = prefs.edit();

    }

    /*private void checkScreenDensity(){
        tempTextView = (TextView)findViewById(R.id.backgroundText);
        switch (getResources().getDisplayMetrics().densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                tempTextView.setVisibility(View.GONE);
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                tempTextView.setVisibility(View.GONE);
                break;
            case DisplayMetrics.DENSITY_HIGH:
                tempTextView.setVisibility(View.VISIBLE);
                break;
        }
    }*/

    public void startClick (View view){
        showStopButton();
        startTime = System.currentTimeMillis() - elapsedTime;
        counting=true;
        mHandler.removeCallbacks(startTimer);
        mHandler.postDelayed(startTimer, 0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void stopClick (View view){
        show_START_RESET_buttons();
        mHandler.removeCallbacks(startTimer);
        counting = false;
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void resetClick (View view){
        elapsedTime= 0;
        ((TextView)findViewById(R.id.timer)).setText(getString(R.string.timer));
        ((TextView)findViewById(R.id.timerMs)).setText("00");
    }

    private Runnable startTimer = new Runnable() {
        public void run() {
            elapsedTime = System.currentTimeMillis() - startTime;
            updateTimer(elapsedTime);
            mHandler.postDelayed(this,REFRESH_RATE);
        }
    };

    private void updateTimer (float time){
        secs = (long)(time/1000);
        mins = (long)((time/1000)/60);
        hrs = (long)(((time/1000)/60)/60);

		/* Convert the seconds to String
		 * and format to ensure it has
		 * a leading zero when required
		 */
        secs = secs % 60;
        seconds=String.valueOf(secs);
 /*       if(secs == 0){
            seconds = "00";
        }*/
        if(secs <10 && secs >= 0 && (mins > 0 || hrs>0)){
            seconds = "0"+ seconds;
        }

		/* Convert the minutes to String and format the String */

        mins = mins % 60;
        minutes=String.valueOf(mins);
//        if(mins == 0){
//            minutes = "00";
//        }
        if(mins <10 && mins >= 0 && hrs>0){
            minutes = "0"+ minutes;
        }

        hours=String.valueOf(hrs);
       /* if(hrs == 0){
            hours = "00";
        }
        if(hrs <10 && hrs > 0){
            hours = "0"+hours;
        }*/

        milliseconds = String.valueOf((long)time);
        if(milliseconds.length()==2){
            milliseconds = "0"+ milliseconds;
        }
        if(milliseconds.length()<=1){
            milliseconds = "00"+ milliseconds;
        }
        milliseconds = milliseconds.substring(milliseconds.length()-3, milliseconds.length()-1);

        if(hrs == 0) {
            if(mins == 0){
                ((TextView) findViewById(R.id.timer)).setText(seconds);
            }else
                ((TextView) findViewById(R.id.timer)).setText(minutes + ":" + seconds);
        }else
        ((TextView)findViewById(R.id.timer)).setText(hours + ":" + minutes + ":" + seconds);

        ((TextView)findViewById(R.id.timerMs)).setText("" + milliseconds);
    }

    private void showStopButton(){
        (findViewById(R.id.startButton)).setVisibility(View.GONE);
        (findViewById(R.id.layout_reset)).setVisibility(View.INVISIBLE);
        (findViewById(R.id.stopButton)).setVisibility(View.VISIBLE);
    }

    private void show_START_RESET_buttons(){
        (findViewById(R.id.startButton)).setVisibility(View.VISIBLE);
        (findViewById(R.id.layout_reset)).setVisibility(View.VISIBLE);
        (findViewById(R.id.stopButton)).setVisibility(View.GONE);
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @Override
    protected void onStop() {
        super.onStop();
        ed.putLong(START_TIME_KEY, startTime);
        ed.putLong(ELAPSED_TIME_KEY, elapsedTime);
        ed.putBoolean(COUNTING_KEY, counting);
        ed.commit();
        mHandler.removeCallbacks(startTimer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = prefs.getLong(START_TIME_KEY, System.currentTimeMillis());
        elapsedTime = prefs.getLong(ELAPSED_TIME_KEY, 0);
        counting = prefs.getBoolean(COUNTING_KEY, false);
        if(counting){
            mHandler.postDelayed(startTimer, 0);
            showStopButton();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }else if(elapsedTime!=0){
            updateTimer(elapsedTime);
            show_START_RESET_buttons();
        }

    }
}