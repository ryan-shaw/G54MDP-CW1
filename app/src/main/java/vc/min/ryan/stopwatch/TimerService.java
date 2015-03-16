package vc.min.ryan.stopwatch;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
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
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = 1;
    private int currentTimerId = 0;
    private final String NOTIFICATION_TITLE = "Stopwatch";

    public class LocalBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public void onCreate(){
        Log.d("service", "onCreate");
        handler.post(updateTime);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder = new Notification.Builder(this)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(getTimersRunning() + " timers running")
                .setSmallIcon(R.drawable.abc_ab_share_pack_holo_dark)
                .setContentIntent(pendingIntent);
        //notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("service", "onBind");
        return mBinder;
    }

    public void addTimer(TimerItem timer){
        timerItems.add(timer);
        updateNotification();
    }

    public void startTimer(int timerId){
        getTimerById(timerId).start();
        updateNotification();
    }

    public void stopTimer(int timerId){
        getTimerById(timerId).stop();
        updateNotification();
    }

    public void resumeTimer(int timerId){
        getTimerById(timerId).resume();
        updateNotification();

    }

    public void pauseTimer(int timerId){
        getTimerById(timerId).pause();
        updateNotification();

    }

    public void resetTimer(int timerId){
        getTimerById(timerId).reset();
        updateNotification();

    }

    public void lapTimer(int timerId){
        getTimerById(timerId).lap();
        updateNotification();
    }

    public int getNewTimerId(){
        return currentTimerId++;
    }

    public List<LapItem> getLaps(int timerId){
        return getTimerById(timerId).getLaps();
    }

    private void updateNotification(){
        notificationBuilder.setContentText(getTimersRunning() + " timers running");
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    /**
     * Get the number of running timers
     * @return runningCount
     */
    private int getTimersRunning(){
        int running = 0;
        for(TimerItem timer : timerItems){
            if(timer.isRunning()) running++;
        }
        return running;
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
        updateNotification();
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

    private void sendTime(TimerItem item) {
        Intent intent = new Intent("time");
        intent.putExtra("obj", item);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run(){
            for(int i = 0; i < timerItems.size(); i++) {
                TimerItem item = timerItems.get(i);
                item.updateTimer();
                sendTime(item);
            }
            handler.postDelayed(this, 10);
        }
    };
}
