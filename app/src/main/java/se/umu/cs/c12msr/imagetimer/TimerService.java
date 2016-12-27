package se.umu.cs.c12msr.imagetimer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

public class TimerService extends Service {

    public static final String COUNTDOWN_BR = "se.umu.cs.c12msr.imagetimer.countdown_br";
    public static final String TIME_LEFT = "countdown";



    private static final String TAG = "TimerService";

    private NotificationManager mNM;

    private int NOTIFICATION = 1;

    private Hashtable<Long, Long> mTimerEvents;
    private Intent broadcastIntent = new Intent(COUNTDOWN_BR);
    private boolean canStop;


    public class LocalBinder extends Binder {
        TimerService getService() {
            return TimerService.this;
        }
    }

    public TimerService() {
    }

    @Override
    public void onCreate() {
        mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mTimerEvents = new Hashtable<>();
        canStop = true;
        startUpdateTimer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mNM.cancel(NOTIFICATION);

        Toast.makeText(this, R.string.timer_service_started, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void addTimerEvent(long key, long time) {
        mTimerEvents.put(key, time);
        /*if (canStop) {
            startUpdateTimer();
        }*/
    }

    public void removeTimerEvent(long key) {
        mTimerEvents.remove(key);
    }


    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                if (mTimerEvents.size() != 0) {
                    ArrayList<Long> removeList = new ArrayList<Long>();
                    long timeLeft[] = new long[mTimerEvents.size()*2];
                    int i = 0;
                    //canStop = true;
                    for (Long key : mTimerEvents.keySet()) {
                        timeLeft[i] = key;
                        long newValue = mTimerEvents.get(key) - 1000;
                        timeLeft[i+1] = newValue;
                        mTimerEvents.put(key, newValue);
                        i+=2;
                        if (newValue < 0) {
                            removeList.add(key);
                        }
                    }
                        broadcastIntent.putExtra(TIME_LEFT, timeLeft);
                        sendBroadcast(broadcastIntent);

                    // Remove expired timers
                    if (removeList.size() != 0) {
                        for (Long key : removeList) {

                            if (!MainActivity.isAppForeground) {
                                // show notification if app is in background
                                showNotification(key);
                            }
                            mTimerEvents.remove(key);
                        }
                    }
                    /*if (canStop) {
                        cancel();
                    }*/
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
