package vc.min.ryan.stopwatch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {
    // Data set
    private List<TimerItem> timerItems = new ArrayList<TimerItem>();

    private long milliTime = 0;
    private long startTime;
    private boolean running = false;
    private long pauseTime = 0;

    private Timer notificationTimer = new Timer();
    private Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        timerItems.add((TimerItem) intent.getParcelableExtra("data"));
        return mBinder;
    }

    public long getCurrentTime(){
        return milliTime;
    }

    public boolean isRunning(){
        return running;
    }

    public void startTimer(){
        milliTime = 0;
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTime, 0);
        running = true;
        startNotification();
    }

    public void stopTimer(){
        handler.removeCallbacks(updateTime);
        running = false;
        stopNotification();
    }

    public void resumeTimer(){
        handler.postDelayed(updateTime, 0);
        running = true;
    }

    public void pauseTimer(){
        pauseTime = milliTime;
        handler.removeCallbacks(updateTime);
        running = false;
    }

    public void resetTimer(){
        handler.removeCallbacks(updateTime);
        milliTime = 0;
    }

    public void destroy(){
        this.stopSelf();
    }

    private void sendTime(){
        Intent intent = new Intent("time");
        intent.putExtra("millisecs", milliTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void stopNotification(){
        notificationTimer.cancel();
    }

    private void startNotification(){
        notificationTimer.schedule(new TimerTask() {
           @Override
           public void run() {
               updateNotification(milliTime);
           }
        },0,800);
    }

    private void updateNotification(long time){
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;

        Notification.Builder notification = new Notification.Builder(this)
            .setContentTitle(String.format("%02d:%02d", minutes, seconds))
            .setStyle(new Notification.BigTextStyle())
            .setSmallIcon(R.drawable.abc_ab_share_pack_holo_dark);
        notificationManager.notify(0, notification.build());
    }

    private Runnable updateTime = new Runnable() {
        @Override
        public void run(){
            milliTime = pauseTime + SystemClock.uptimeMillis() - startTime;
            handler.postDelayed(this, 0);
            sendTime();
        }
    };
}
