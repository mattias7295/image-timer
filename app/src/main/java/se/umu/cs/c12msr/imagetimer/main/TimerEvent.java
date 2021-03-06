package se.umu.cs.c12msr.imagetimer.main;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by c12msr on 17-Aug-16.
 */
public class TimerEvent implements Cloneable, Parcelable {


    private String mImagePath;
    private String mName;
    private long mTime;
    private long mId;


    private long mTimeLeft;
    private long mTimerId;

    //TODO: remove this when finished
    private int mImageID;

    public TimerEvent(long id, long time, String imagePath, String name) {
        this.mId = id;
        this.mTime = time;
        this.mImagePath = imagePath;
        this.mName = name;
    }



    public long getId() {
        return mId;
    }

    public String getImagePath() {
        return mImagePath;
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImagePath);
        dest.writeString(this.mName);
        dest.writeLong(this.mTime);
        dest.writeLong(this.mId);
        dest.writeLong(this.mTimeLeft);
        dest.writeLong(this.mTimerId);
        dest.writeInt(this.mImageID);
    }

    protected TimerEvent(Parcel in) {
        this.mImagePath = in.readString();
        this.mName = in.readString();
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
