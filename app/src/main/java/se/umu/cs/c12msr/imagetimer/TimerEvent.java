package se.umu.cs.c12msr.imagetimer;

/**
 * Created by Mattias-stationary on 17-Aug-16.
 */
public class TimerEvent {

    private String mImagePath;
    private int mHours;
    private int mMinutes;
    private int mSeconds;


    public TimerEvent(String mImagePath, int mHours, int mMinutes, int mSeconds) {
        this.mImagePath = mImagePath;
        this.mHours = mHours;
        this.mMinutes = mMinutes;
        this.mSeconds = mSeconds;
    }

    public String getmImagePath() {
        return mImagePath;
    }

    public void setmImagePath(String mImagePath) {
        this.mImagePath = mImagePath;
    }

    public int getmHours() {
        return mHours;
    }

    public void setmHours(int mHours) {
        this.mHours = mHours;
    }

    public int getmMinutes() {
        return mMinutes;
    }

    public void setmMinutes(int mMinutes) {
        this.mMinutes = mMinutes;
    }

    public int getmSeconds() {
        return mSeconds;
    }

    public void setmSeconds(int mSeconds) {
        this.mSeconds = mSeconds;
    }
}
