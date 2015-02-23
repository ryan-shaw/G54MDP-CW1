package vc.min.ryan.stopwatch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.app.Activity;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;

public class TimerActivity extends Activity {
    TimerService timerService;
    boolean bound = false;
    private GUIUtils guiutils;

    // Views
    private TextView secondsText;
    private ImageButton fabStart;
    private ImageButton fabDestroy;
    private View fabStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag", "onCreate");
        setContentView(R.layout.activity_timer);
        guiutils = new GUIUtils();
        secondsText = (TextView) findViewById(R.id.time);
        fabStart = (ImageButton) findViewById(R.id.start_button);
        fabDestroy = (ImageButton) findViewById(R.id.reset_button);
        guiutils.configureFab(fabStart);
        guiutils.configureFab(fabDestroy);
        fabStart.setOnClickListener(startListener);
        fabDestroy.setOnClickListener(destroyListener);
        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("tag", "onStart");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("tag", "onStop");
        if(bound){
            unbindService(timerServiceConnection);
            bound = false;
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("tag", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(timeReceiver,
                new IntentFilter("time"));
    }

    @Override
    protected void onPause(){
        super.onPause();
        // Unregister as the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
    }

    public void onStopTimer (View v) {
        if (bound) {
            timerService.stopTimer();
        }
    }

    View.OnClickListener destroyListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(bound){
                timerService.destroy();
            }
            finish();
        }
    };

    View.OnClickListener startListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(bound){
                if(timerService.isRunning()){
                    timerService.pauseTimer();
                    fabStart.setImageResource(R.drawable.ic_action_play);
                }else{
                    timerService.startTimer();
                    fabStart.setImageResource(R.drawable.ic_action_pause);
                }

            }
        }
    };


    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long time = intent.getLongExtra("millisecs", 0);

            int seconds = (int) (time / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliSeconds = (int) (time % 1000);

            secondsText.setText(minutes + ":"
            + String.format("%02d", seconds) + ":"
            + new DecimalFormat("00").format(milliSeconds));

        }
    };

    private ServiceConnection timerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) service;
            timerService = localBinder.getService();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            bound = false;
        }
    };
}
