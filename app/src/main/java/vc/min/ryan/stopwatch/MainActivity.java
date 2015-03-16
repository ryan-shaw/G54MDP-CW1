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
    private int currentId;
    private boolean bound;
    private TimerService timerService;

    private Context context;
    private GUIUtils guiUtils;
    private Intent serviceIntent;

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
        Log.d("tag", "onStart");
    }

    @Override
    protected void onStop(){
        Log.d("tag", "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        Log.d("tag", "onDestroy");
        super.onDestroy();
        if(bound){
            unbindService(timerServiceConnection);
            bound = false;
        }

        // Destroy the service if no timers are running and user exited app
        boolean anyRunning = false;
        for(int i = 0; i < timerService.getTimers().size(); i++){
            if(timerService.getTimers().get(i).isRunning()){
                anyRunning = true;
                break;
            }
        }
        if(!anyRunning)
            stopService(serviceIntent);
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("MainActivity", "onResume");
        LocalBroadcastManager.getInstance(this).registerReceiver(timeReceiver,
                new IntentFilter("time"));
        if(bound) {
            mAdapter.updateData(timerService.getTimers());
            mAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onPause(){
        super.onPause();
        // Unregister as the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(timeReceiver);
    }

    View.OnClickListener addOnClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
        Intent intent = new Intent(context, TimerActivity.class);
        TimerItem timer = new TimerItem(currentId++);
        dataset.add(timer);
        mAdapter.notifyDataSetChanged();
        intent.putExtra("timerId", timer.getId());
        timerService.addTimer(timer);
        }
    };

    private BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TimerItem item = (TimerItem) intent.getParcelableExtra("obj");
            for(int i = 0; i < mAdapter.getData().size(); i++){
                TimerItem item1 = mAdapter.getData().get(i);
                if(item1.getFormattedTime() != item.getFormattedTime() && item1.getId() == item.getId()){
                    item1 = item;
                    mAdapter.notifyItemChanged(i);
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
        }

        @Override
        public void onServiceDisconnected(ComponentName name){
            bound = false;
        }
    };
}