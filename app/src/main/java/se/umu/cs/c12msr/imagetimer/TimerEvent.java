package se.umu.cs.c12msr.imagetimer;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by c12msr on 17-Aug-16.
 */
public class TimerEvent implements Cloneable {
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
}
