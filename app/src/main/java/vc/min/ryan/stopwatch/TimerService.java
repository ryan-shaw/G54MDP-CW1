package vc.min.ryan.stopwatch;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

public class TimerService extends Service {
    private long milliTime = 0;
    private long startTime;
    private boolean running = false;
    private long pauseTime = 0;

    private Handler handler = new Handler();
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
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
    }

    public void stopTimer(){
        handler.removeCallbacks(updateTime);
        running = false;
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

    private void sendTime(){
        Intent intent = new Intent("time");
        intent.putExtra("millisecs", milliTime);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
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
