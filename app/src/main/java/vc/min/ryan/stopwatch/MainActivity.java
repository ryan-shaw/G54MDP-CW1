package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private ImageButton fabAdd;
    private RecyclerView mRecyclerView;
    private TimerDataAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<TimerItem> dataset;
    private boolean bound;
    private TimerService timerService;

    private Context context;
    private GUIUtils guiUtils;
    private Intent serviceIntent;

    private final String TAG = "MainActivity";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        guiUtils = new GUIUtils();
        fabAdd = (ImageButton) findViewById(R.id.add_button);
        guiUtils.configureFab(fabAdd);
        fabAdd.setOnClickListener(addOnClickListener);

        // Setup list
        mRecyclerView = (RecyclerView) findViewById(R.id.timers_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        dataset = new ArrayList<TimerItem>();
        mAdapter = new TimerDataAdapter(dataset, this);
        mRecyclerView.setAdapter(mAdapter);

        serviceIntent = new Intent(this, TimerService.class);

        startService(serviceIntent);
        bindService(serviceIntent, timerServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
        LocalBroadcastManager.getInstance(this).registerReceiver(timeReceiver,
                new IntentFilter("time"));
    }

    @Override
    protected void onStop(){
        Log.d(TAG, "onStop");
        super.onStop();
        // Unregister as the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
    }

    @Override
    protected void onDestroy(){
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        if(bound){
            unbindService(timerServiceConnection);
            bound = false;
        }

        // Stop the service if no timers exist.
        if(timerService.getTimers().size() == 0) {
            Log.d(TAG, "Stopping timer service");
            stopService(serviceIntent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    /**
     * Toggle the timer state (used for the in-row play/pause button
     * @param position
     * @return newState, true = running, false = not running
     */
    public boolean toggleTimer(int position){
        if(!bound || timerService.getTimers().size() <= position) return false;
        TimerItem timer = timerService.getTimers().get(position);
        if(timer.isRunning()) {
            timerService.pauseTimer(timer.getId());
        } else {
            timerService.startTimer(timer.getId());
        }
        return timer.isRunning();
    }

    public void startTimer(int position){
        if(bound) {
            timerService.startTimer(dataset.get(position).getId());
        }
    }

    public void pauseTimer(int position){
        if(bound){
            timerService.stopTimer(dataset.get(position).getId());
        }
    }

    View.OnClickListener addOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, TimerActivity.class);
            TimerItem timer = new TimerItem(timerService.getNewTimerId());
            dataset.add(timer);
            mAdapter.notifyDataSetChanged();
            intent.putExtra("timerId", timer.getId());
            timerService.addTimer(timer);
        }
    };

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<TimerItem> timers = intent.getParcelableArrayListExtra("timers");

            for(int i = 0; i < mAdapter.getData().size(); i++){
                TimerItem item = mAdapter.getData().get(i);
                for(int j = 0; j < timers.size(); j++){
                    TimerItem item1 = timers.get(j);
                    if(item1.getId() == item.getId()){
                        item = item1;
                        mAdapter.notifyItemChanged(i);
                    }
                }
            }
        }
    };

    private ServiceConnection timerServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.LocalBinder localBinder = (TimerService.LocalBinder) service;
            timerService = localBinder.getService();
            bound = true;
            mAdapter.updateData(timerService.getTimers());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            bound = false;
        }
    };
}