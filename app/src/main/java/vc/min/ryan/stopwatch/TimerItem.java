package vc.min.ryan.stopwatch;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;

/**
 * Created by Ryan on 23/02/2015.
 */
public class TimerItem implements Parcelable{

    private long startTime;
    private long time;
    private long pauseTime;

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(startTime);
        out.writeLong(time);
        out.writeLong(pauseTime);
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

    public TimerItem(long startTime, long time, long pauseTime){
        this.startTime = startTime;
        this.time = time;
        this.pauseTime = time;
    }

    private TimerItem(Parcel in){
        this.startTime = in.readLong();
        this.time = in.readLong();
        this.pauseTime = in.readLong();
    }

    private long getTime(){
        return time;
    }

}
