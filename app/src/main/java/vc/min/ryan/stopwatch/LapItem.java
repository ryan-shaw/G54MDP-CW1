package vc.min.ryan.stopwatch;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ryan on 24/02/2015.
 */
public class LapItem implements Parcelable{

    private long time;

    public LapItem(long time){
        this.time = time;
    }

    public int describeContents(){
        return 0;
    }

    public static final Parcelable.Creator<LapItem> CREATOR =
            new Parcelable.Creator<LapItem>() {
                public LapItem createFromParcel(Parcel in){
                    return new LapItem(in);
                }

                public LapItem[] newArray(int size){
                    return new LapItem[size];
                }
            };

    public void writeToParcel(Parcel out, int flags){
        out.writeLong(time);
    }

    private LapItem(Parcel in){
        time = in.readLong();
    }

    public String getFormattedTime(){
        int seconds = (int) (time / 1000);
        int minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d:%02d", minutes, seconds, (time / 10) % 100);
    }

    public long getTime(){
        return time;
    }
}
