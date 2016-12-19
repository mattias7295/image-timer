package se.umu.cs.c12msr.imagetimer;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by c12msr on 17-Aug-16.
 */
public class TimerEvent {

    private static final long COUNT_DOWN_INTERVAL = 1000; // 1 sec interval

    private String mImageName;
    private long mTime;
    private long mId;
    private CountDownTimer mTimer;
    private TextView mTimertv;
    private long mTimeLeft;

    //TODO: temp image
    private int mImageID;

    public TimerEvent(long id, String imageName, long time) {
        this.mId = id;
        this.mImageName = imageName;
        this.mTime = time;

        mTimer = new CountDownTimer(time, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimertv.setText(String.format(Locale.ENGLISH, "%02d:%02d",
                        millisUntilFinished/(1000*60), millisUntilFinished/1000));

                // Store time left
                mTimeLeft = millisUntilFinished;
            }

            @Override
            public void onFinish() {
                mTimertv.setText("Finished");
            }
        };
    }

    public void startTimer(TextView tv) {
        this.mTimertv = tv;
        mTimer.start();
    }

    public void cancelTimer() {
        mTimer.cancel();
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

    @Override
    public String toString() {
        return "TimerEvent{" + mTime +'}';
    }
}
