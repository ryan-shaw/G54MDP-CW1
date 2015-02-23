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
import android.view.Menu;
import android.view.MenuItem;
import android.transition.Fade;
import android.view.View;
import android.view.animation.PathInterpolator;
import android.view.Window;
import android.app.Activity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    TimerService timerService;
    boolean bound = false;
    private GUIUtils guiutils;

    // Views
    private TextView secondsText;
    private ImageButton fabStart;
    private ImageButton fabReset;
    private View fabStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("tag", "onCreate");
        setContentView(R.layout.activity_main);
        guiutils = new GUIUtils();
        secondsText = (TextView) findViewById(R.id.time);
        fabStart = (ImageButton) findViewById(R.id.start_button);
        fabReset = (ImageButton) findViewById(R.id.reset_button);
        guiutils.configureFab(fabStart);
        guiutils.configureFab(fabReset);
        fabStart.setOnClickListener(startListener);
        fabReset.setOnClickListener(resetListener);
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

    View.OnClickListener resetListener = new View.OnClickListener(){
        @Override
        public void onClick(View view){
            if(bound){
                timerService.stopTimer();
                timerService.resetTimer();
            }
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

            secondsText.setText("" + minutes + ":"
            + String.format("%02d", seconds) + ":"
            + String.format("%02d", milliSeconds));

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
