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
    private ArrayList<TimerItem> timerItems             = new ArrayList<TimerItem>();
    private Handler handler                             = new Handler();
    private final IBinder mBinder                       = new LocalBinder();
    private Notification.Builder notificationBuilder    = null;
    private NotificationManager notificationManager     = null;
    private final int NOTIFICATION_ID                   = 1;
    private final String TAG                            = "TimerService";
    /**
     * Holds the currentTimerId to assign to the next created timer
     */
    private int currentTimerId                          = 0;
    private final String NOTIFICATION_TITLE             = "Stopwatch";

    public class LocalBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate");
        // Start timer loop
        handler.post(updateTime);
        // Create notification builder
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        notificationBuilder = new Notification.Builder(this)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(getTimersRunning() + " timers running")
                .setSmallIcon(R.drawable.abc_ab_share_pack_holo_dark) //TODO: Make icon
                .setContentIntent(pendingIntent);

        // Start the notification in foreground
        startForeground(NOTIFICATION_ID, notificationBuilder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        return mBinder;
    }

    /**
     * Add timer to timer list
     * @param timer, the timer item
     */
    public void addTimer(TimerItem timer){
        timerItems.add(timer);
        updateNotification();
    }

    /**
     * Start a timer
     * @param timerId, the timer id
     */
    public void startTimer(int timerId){
        getTimerById(timerId).start();
        updateNotification();

        // If no timers are running the loop would have stopped
        // if 1 timer is running that means it's the timer we've
        // just added so need to start the loop again.
        if(getTimersRunning() == 1)
            handler.post(updateTime);
    }

    /**
     * Stop a timer
     * @param timerId, the timer id
     */
    public void stopTimer(int timerId){
        getTimerById(timerId).stop();
        updateNotification();
    }

    /**
     * Resume a timer, NOT USED
     * It seems pointless to implement stop and pause.
     * But this is here, in case that feature is to be added in the future
     * (same goes for below methods)
     * @param timerId, the timer id
     */
    public void resumeTimer(int timerId){
        getTimerById(timerId).resume();
        updateNotification();
    }

    /**
     * Pause a timer, NOT USED
     * @param timerId, the timer id
     */
    public void pauseTimer(int timerId){
        getTimerById(timerId).pause();
        updateNotification();

        // Send time if non are running, as it causes the timer not to update.
        if(getTimersRunning() == 0)
            sendTime();
    }

    /**
     * Reset a timer, NOT USED
     * @param timerId, the timer id
     */
    public void resetTimer(int timerId){
        getTimerById(timerId).reset();
        updateNotification();
    }

    /**
     * Lap a timer
     * @param timerId, the timer id
     */
    public void lapTimer(int timerId){
        getTimerById(timerId).lap();
        updateNotification();
    }

    /**
     * Get currentTimerId for new timer
     * @return currentTimerId
     */
    public int getNewTimerId(){
        return currentTimerId++;
    }

    /**
     * Get laps for a timer
     * @param timerId, the timer id
     * @return List of LapItem's
     */
    public List<LapItem> getLaps(int timerId){
        return getTimerById(timerId).getLaps();
    }

    /**
     * Update the foreground notification with number of running timers
     */
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

    /**
     * Destroy a timer
     * @param timerId
     */
    public void destroyTimer(int timerId){
        Log.d(TAG, "Destroy: "+ timerId);
        for(int i = timerItems.size() - 1; i >= 0; i--){
            TimerItem item = timerItems.get(i);
            if(item.getId() == timerId){
                Log.d(TAG, "Removing timer");
                timerItems.remove(item);
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

    /**
     * Get a timer by it's ID
     * @param timerId, the timer id
     * @return timer, the TimerItem or null
     */
    private TimerItem getTimerById(int timerId) {
        for (TimerItem item : timerItems) {
            if (item.getId() == timerId)
                return item;
        }
        return null;
    }

    /**
     * Send the timers to the activities
     */
    private void sendTime(){
        Intent intent = new Intent("time");
        intent.putExtra("timers", timerItems);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * The runnable update time method
     */
    private Runnable updateTime = new Runnable() {
        @Override
        public void run(){
            for(int i = 0; i < timerItems.size(); i++) {
                TimerItem item = timerItems.get(i);
                item.updateTimer();
            }
            sendTime();
            handler.post(this);
        }
    };
}
