package se.umu.cs.c12msr.imagetimer.main;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import se.umu.cs.c12msr.imagetimer.R;

public class TimerService extends Service {

    public static final String COUNTDOWN_BR = "se.umu.cs.c12msr.imagetimer.countdown_br";
    public static final String TIME_LEFT = "countdown";



    private static final String TAG = "TimerService";

    private NotificationManager mNM;

    private int NOTIFICATION = 1;

    private Hashtable<Long, Long> mTimerEvents;
    private Intent mBroadcastIntent = new Intent(COUNTDOWN_BR);
    private boolean mIsRunning;
    private PowerManager.WakeLock mWakeLock;

    private final Object mLock = new Object();
    private PowerManager mPowerManager;


    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    public TimerService() {
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate: service created");

        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mIsRunning = false;

        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = mPowerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "MyWakelockTag");

        mTimerEvents = new Hashtable<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);
        Toast.makeText(this, R.string.timer_service_started, Toast.LENGTH_SHORT).show();
        mWakeLock.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void addTimerEvent(long key, long time) {
        synchronized (mLock) {
            mTimerEvents.put(key, time);
            if (!mIsRunning) {
                mWakeLock.acquire();
                startUpdateTimer();
            }
        }
    }

    public void removeTimerEvent(long key) {
        mTimerEvents.remove(key);
    }

    public boolean hasTimerEventExpired(long key) {
        return !mTimerEvents.containsKey(key);
    }


    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mIsRunning = true;
                ArrayList<Long> removeList = new ArrayList<>();
                long timeLeft[] = new long[mTimerEvents.size()*2];

                int i = 0;
                for (Long key : mTimerEvents.keySet()) {
                    long newValue = mTimerEvents.get(key) - 1000;

                    timeLeft[i] = key;
                    timeLeft[i+1] = newValue;
                    mTimerEvents.put(key, newValue);
                    i+=2;

                    if (newValue < 0) {
                        removeList.add(key);
                    }
                }
                mBroadcastIntent.putExtra(TIME_LEFT, timeLeft);
                sendBroadcast(mBroadcastIntent);

                // Remove expired timers
                if (!removeList.isEmpty()) {
                    for (Long key : removeList) {

                        if (!MainActivity.isAppForeground || !mPowerManager.isInteractive()) {
                            // show notification if app is in background or device not interactive
                            showNotification(key);
                        }
                        mTimerEvents.remove(key);
                    }
                }
                synchronized (mLock) {
                    if (mTimerEvents.isEmpty()) {
                        mIsRunning = false;
                        cancel();
                        mWakeLock.release();
                    }
                }
            }
        }, 1000, 1000);
    }

    private final IBinder mBinder = new LocalBinder();

    private void showNotification(long id) {
        CharSequence title = getText(R.string.notification_title);
        CharSequence text = getText(R.string.timer_expired);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra(MainActivity.TIMER_ID_EXPIRED, id);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_add_white_24px)
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(title)  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }
}
