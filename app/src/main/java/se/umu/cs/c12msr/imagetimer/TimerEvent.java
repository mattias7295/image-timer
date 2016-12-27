package se.umu.cs.c12msr.imagetimer;

import android.os.CountDownTimer;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by c12msr on 17-Aug-16.
 */
public class TimerEvent implements Cloneable, Parcelable {
    private String mImageName;
    private long mTime;
    private long mId;


    private long mTimeLeft;
    private long mTimerId;

    //TODO: temp image
    private int mImageID;

    public TimerEvent(long id, String imageName, long time) {
        this.mId = id;
        this.mImageName = imageName;
        this.mTime = time;
    }

    public long getId() {
        return mId;
    }

    public String getImageName() {
        return mImageName;
    }


    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getImageID() {
        return mImageID;
    }

    public void setImageID(int imageID) {
        mImageID = imageID;
    }

    public long getTimeLeft() {
        return mTimeLeft;
    }

    public void setTimer(long time) {
        mTimeLeft = time;
    }

    public long getTimerId() {
        return mTimerId;
    }

    public void setTimerId(long mTimerId) {
        this.mTimerId = mTimerId;
    }

    /**
     * purpose is to be able to set the time when the timer is started
     * without changing the time for the next run.
     * @return
     * @throws CloneNotSupportedException
     */
    @Override
    public TimerEvent clone() throws CloneNotSupportedException {
        return (TimerEvent) super.clone();
    }

    @Override
    public String toString() {
        return "TimerEvent{" + mTime +'}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImageName);
        dest.writeLong(this.mTime);
        dest.writeLong(this.mId);
        dest.writeLong(this.mTimeLeft);
        dest.writeLong(this.mTimerId);
        dest.writeInt(this.mImageID);
    }

    protected TimerEvent(Parcel in) {
        this.mImageName = in.readString();
        this.mTime = in.readLong();
        this.mId = in.readLong();
        this.mTimeLeft = in.readLong();
        this.mTimerId = in.readLong();
        this.mImageID = in.readInt();
    }

    public static final Parcelable.Creator<TimerEvent> CREATOR = new Parcelable.Creator<TimerEvent>() {
        @Override
        public TimerEvent createFromParcel(Parcel source) {
            return new TimerEvent(source);
        }

        @Override
        public TimerEvent[] newArray(int size) {
            return new TimerEvent[size];
        }
    };
}
