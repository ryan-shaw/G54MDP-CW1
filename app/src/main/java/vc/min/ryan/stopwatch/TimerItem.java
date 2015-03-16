package vc.min.ryan.stopwatch;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import java.util.ArrayList;
import java.util.List;

/**
 * TimerItem class
 *
 * Handles the functions for individual timers
 *
 * Created by Ryan on 23/02/2015.
 */
public class TimerItem implements Parcelable{

    private long startTime;
    private long time;
    private long pauseTime;
    private boolean running;
    private int id;
    private boolean destroyed;
    private List<LapItem> laps;
    private long timeBeforeLap;

    /**
     * Never used in the project but needed for when receiving parcel
     */
    public TimerItem(){
        laps = new ArrayList<LapItem>();
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeInt(id);
        out.writeLong(startTime);
        out.writeLong(time);
        out.writeLong(pauseTime);
        out.writeByte((byte) (running ? 1 : 0));
        out.writeByte((byte) (destroyed ? 1 : 0));
        out.writeList(laps);
        out.writeLong(timeBeforeLap);
    }

    public static final Parcelable.Creator<TimerItem> CREATOR =
            new Parcelable.Creator<TimerItem>() {
                public TimerItem createFromParcel(Parcel in){
                    return new TimerItem(in);
                }

                public TimerItem[] newArray(int size){
                    return new TimerItem[size];
                }
            };

    public TimerItem(int id){
        this(id, 0,0, 0);
    }

    public TimerItem(int id, long startTime, long time, long timeBeforeLap){
        this.id = id;
        this.startTime = startTime;
        this.time = time;
        this.pauseTime = time;
        this.running = false;
        this.destroyed = false;
        this.laps = new ArrayList<LapItem>();
        this.timeBeforeLap = timeBeforeLap;
    }

    private TimerItem(Parcel in){
        this.id = in.readInt();
        this.startTime = in.readLong();
        this.time = in.readLong();
        this.pauseTime = in.readLong();
        this.running = in.readByte() != 0;
        this.destroyed = in.readByte() != 0;
        in.readTypedList(laps, LapItem.CREATOR);
        this.timeBeforeLap = in.readLong();
    }

    /**
     * Set the timer running
     */
    public void start(){
        running = true;
        startTime = SystemClock.elapsedRealtime();
    }

    /**
     * Stop the timer running
     */
    public void stop(){
        running = false;
    }

    /**
     * Destroy the timer
     */
    public void destroy(){
        destroyed = true;
        running = false;
    }

    /**
     * Resume the timer from paused state
     */
    public void resume(){
        running = true;
    }

    /**
     * Pause the timer
     */
    public void pause(){
        pauseTime = time;
        running = false;
    }

    /**
     * Reset the timer
     */
    public void reset(){
        time = 0;
        pauseTime = 0;
        startTime = SystemClock.elapsedRealtime();
    }

    /**
     * Create a new lap
     */
    public void lap(){
        long lapTime = time - timeBeforeLap;
        timeBeforeLap = time;
        laps.add(new LapItem(lapTime));
    }

    /**
     * Update the timer
     */
    public void updateTimer(){
        if(running)
            time = pauseTime + SystemClock.elapsedRealtime() - startTime;
    }

    /**
     * Get the ID of the timer
     * @return timerId
     */
    public int getId(){
        return id;
    }

    /**
     * Get formatted time string of the timer value
     * @return formattedTime
     */
    public String getFormattedTime(){
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", minutes, seconds, (time / 10) % 100);
    }

    /**
     * Get laps list
     * @return laps
     */
    public List<LapItem> getLaps(){
        return laps;
    }

    /**
     * Get the running value of the timer
     * @return running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Check if the timer is pending to be destroyed
     * @return
     */
    public boolean isDestroyed() {
        return destroyed;
    }

}
