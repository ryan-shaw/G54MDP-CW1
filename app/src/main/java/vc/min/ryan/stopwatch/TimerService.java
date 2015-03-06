package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class TimerService extends Service {
    /**
     * TimerItem data set
     */
    private List<TimerItem> timerItems = new ArrayList<TimerItem>();

    private Handler handler             = new Handler();
    private final IBinder mBinder       = new LocalBinder();
    private Notification notification; // Our foreground notification
    private final int NOTIFICATION_ID = 1;

    public class LocalBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public void onCreate(){
        Log.d("service", "onCreate");
        handler.post(updateTime);
        notification = new Notification(R.drawable.abc_ab_share_pack_holo_dark, "Timers running", System.currentTimeMillis());
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, "Test notify", "messege", pendingIntent);
        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service", "onBind");
        return mBinder;
    }

    public void addTimer(TimerItem timer){
        timerItems.add(timer);
    }

    public void startTimer(int timerId){
        getTimerById(timerId).start();
    }

    public void stopTimer(int timerId){
        getTimerById(timerId).stop();
    }

    public void resumeTimer(int timerId){
        getTimerById(timerId).resume();
    }

    public void pauseTimer(int timerId){
        getTimerById(timerId).pause();
    }

    public void resetTimer(int timerId){
        getTimerById(timerId).reset();
    }

    public void lapTimer(int timerId){
        getTimerById(timerId).lap();
    }

    public List<LapItem> getLaps(int timerId){
        return getTimerById(timerId).getLaps();
    }

    public void destroyTimer(int timerId){
        Log.d("service", "Destroy: "+ timerId);
        for(int i = timerItems.size() - 1; i >= 0; i--){
            TimerItem item = timerItems.get(i);
            if(item.getId() == timerId){
                timerItems.remove(i);
            }
        }
        Intent intent = new Intent("destroy");
        intent.putExtra("timerId", timerId);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public boolean isRunning(int timerId){
        return getTimerById(timerId).isRunning();
    }

    public List<TimerItem> getTimers(){
        return timerItems;
    }

    private TimerItem getTimerById(int timerId){
        for(TimerItem item : timerItems){
            if(item.getId() == timerId)
                return item;
        }
        return null;
    }

    private void sendTime() {
        for(TimerItem item: timerItems){
            Intent intent = new Intent("time");
            intent.putExtra("obj", item);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run(){
            for(int i = 0; i < timerItems.size(); i++) {
                TimerItem item = timerItems.get(i);
                item.updateTimer();
                sendTime();
            }
            handler.post(this);
        }
    };
}
