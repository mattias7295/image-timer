package se.umu.cs.c12msr.imagetimer;

/**
 * Created by Mattias-stationary on 17-Aug-16.
 */
public class TimeableEvent {

    private String[] mTags;
    private String mImagePath;
    private int mHours;
    private int mMinutes;
    private int mSeconds;


    public TimeableEvent(String[] mTags, String mImagePath, int mHours, int mMinutes, int mSeconds) {
        this.mTags = mTags;
        this.mImagePath = mImagePath;
        this.mHours = mHours;
        this.mMinutes = mMinutes;
        this.mSeconds = mSeconds;
    }

    public TimeableEvent(String mImagePath, int mHours, int mMinutes, int mSeconds) {
        this.mImagePath = mImagePath;
        this.mHours = mHours;
        this.mMinutes = mMinutes;
        this.mSeconds = mSeconds;
    }
}
