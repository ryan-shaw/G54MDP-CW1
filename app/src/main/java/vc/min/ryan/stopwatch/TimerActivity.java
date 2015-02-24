package vc.min.ryan.stopwatch;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
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
    private int timerId;
    private TimerItem timer;

    // Views
    private TextView secondsText;
    private ImageButton fabStart;
    private ImageButton fabDestroy;
    private View fabStop;

    private final String TAG = "TimerActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TimerActivity", "onCreate");

        setContentView(R.layout.activity_timer);

        secondsText = (TextView) findViewById(R.id.time);
        fabStart = (ImageButton) findViewById(R.id.start_button);
        fabDestroy = (ImageButton) findViewById(R.id.reset_button);
        GUIUtils.configureFab(fabStart);
        GUIUtils.configureFab(fabDestroy);
        fabStart.setOnClickListener(startListener);
        fabDestroy.setOnClickListener(destroyListener);
        timerId  = getIntent().getIntExtra("timerId", 0);
        Log.d(TAG, "" + timerId);

        Intent intent = new Intent(this, TimerService.class);
        bindService(intent, timerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("tag", "onStart");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("tag", "onDestroy");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
        if(bound){
            unbindService(timerServiceConnection);
            bound = false;
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("tag", "onStop");

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
        Log.d("tag", "onPause");
    }

    public void onStopTimer (View v) {
        if (bound) {
            timerService.stopTimer(timerId);
        }
    }

    View.OnClickListener destroyListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            Log.d("TimerActivity", "Destroy timer: " + timerId);
            if(bound){
                timerService.destroyTimer(timerId);
            }
            finish();
        }
    };

    private void updateControls(){
        if(bound){
            if(timerService.isRunning(timerId)){
                fabStart.setImageResource(R.drawable.ic_action_pause);
            }else{
                fabStart.setImageResource(R.drawable.ic_action_play);
            }
        }
    }


    View.OnClickListener startListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(bound){
                if(timerService.isRunning(timerId)){
                    timerService.pauseTimer(timerId);
                    fabStart.setImageResource(R.drawable.ic_action_play);
                }else{
                    timerService.startTimer(timerId);
                    fabStart.setImageResource(R.drawable.ic_action_pause);
                }
            }
        }
    };


    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TimerItem item = intent.getParcelableExtra("obj");
            if(item.getId() != timerId) return;
            secondsText.setText(item.getFormattedTime());

        }
    };

    private ServiceConnection timerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) service;
            timerService = localBinder.getService();
            bound = true;
            updateControls();
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            bound = false;
        }
    };
}
