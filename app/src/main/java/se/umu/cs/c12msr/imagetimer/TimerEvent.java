package se.umu.cs.c12msr.imagetimer;

/**
 * Created by Mattias-stationary on 17-Aug-16.
 */
public class TimerEvent {

    private String mImageName;
    private int mHours;
    private int mMinutes;
    private int mSeconds;


    public TimerEvent(String mImageName, int mHours, int mMinutes, int mSeconds) {
        this.mImageName = mImageName;
        this.mHours = mHours;
        this.mMinutes = mMinutes;
        this.mSeconds = mSeconds;
    }

    public String getImageName() {
        return mImageName;
    }


    public int getHours() {
        return mHours;
    }

    public void setHours(int hours) {
        mHours = hours;
    }

    public int getMinutes() {
        return mMinutes;
    }

    public void setMinutes(int minutes) {
        mMinutes = minutes;
    }

    public int getSeconds() {
        return mSeconds;
    }

    public void setSeconds(int seconds) {
        mSeconds = seconds;
    }
}
