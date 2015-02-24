package vc.min.ryan.stopwatch;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
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
        this(id, 0,0,0);
    }

    public TimerItem(int id, long startTime, long time, long pauseTime){
        this.id = id;
        this.startTime = startTime;
        this.time = time;
        this.pauseTime = time;
        this.running = false;
        this.destroyed = false;
        this.laps = new ArrayList<LapItem>();
    }

    private TimerItem(Parcel in){
        this.id = in.readInt();
        this.startTime = in.readLong();
        this.time = in.readLong();
        this.pauseTime = in.readLong();
        this.running = in.readByte() != 0;
        this.destroyed = in.readByte() != 0;
        in.readTypedList(laps, LapItem.CREATOR);
    }

    public void start(){
        running = true;
        startTime = SystemClock.uptimeMillis();
    }

    public void stop(){
        running = false;
    }

    public void destroy(){
        destroyed = true;
        running = false;
    }

    public void resume(){
        running = true;
    }

    public void pause(){
        pauseTime = time;
        running = false;
    }

    public void reset(){
        time = 0;
        pauseTime = 0;
        startTime = SystemClock.uptimeMillis();
    }

    public void lap(){
        laps.add(new LapItem(time));
    }

    public void updateTimer(){
        if(running)
            time = pauseTime + SystemClock.uptimeMillis() - startTime;
    }

    public int getId(){
        return id;
    }

    public String getFormattedTime(){
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", minutes, seconds, (time / 10) % 100);
    }

    public List<LapItem> getLaps(){
        return laps;
    }

    public int getSeconds(){
        return (int) (time / 1000);
    }

    public int getMinutes(){
        return (int) (time / 1000) / 60;
    }

    private long getTime(){
        return time;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

}
