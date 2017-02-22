package se.umu.cs.c12msr.imagetimer.main;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks2;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import se.umu.cs.c12msr.imagetimer.R;

public class MainActivity extends AppCompatActivity
        implements PhotoGridFragment.OnPhotoGridInteractionListener,
        EventListFragment.OnTimerEventInteractionListener {

    public static final String TIMER_ID_EXPIRED = "timer.id.expired";

    public static boolean isAppForeground;

    private static final String TAG = "MainActivity";

    private Long mTimerEventExpired;

    private int eventCount;

    private TimerService mTimerService;
    private boolean mIsBound;

    private Map<Long, TimerEvent> activeEvents;
    private Long eventId;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTimerService = ((TimerService.LocalBinder)service).getService();

            Toast.makeText(getApplicationContext(), R.string.timer_service_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mTimerService = null;

            Toast.makeText(getApplicationContext(), R.string.timer_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    private void doBindService() {
        Log.i(TAG, "Binding service");
        getApplicationContext().bindService(new Intent(getApplicationContext(), TimerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            Log.i(TAG, "Unbinding service");
            getApplicationContext().unbindService(mConnection);
            mIsBound = false;
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(TimerService.COUNTDOWN_BR)) {
                if (intent.getExtras() != null) {
                    long timeLeft[] = intent.getLongArrayExtra(TimerService.TIME_LEFT);
                    updateTimers(timeLeft);

                    EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                            .findFragmentByTag(EventListFragment.class.getName());
                    if (eventListFragment != null) {
                        // Update list if the fragment is available
                        eventListFragment.update();
                    }
                }
            }
        }
    };

    public void handleTimerExpiration(TimerEvent event) {
        // Show alertdialog with sound

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        final Ringtone r = RingtoneManager.getRingtone(this, soundUri);
        r.play();

        new AlertDialog.Builder(this)
                .setTitle(event.getName() + " has expired!")
                .setMessage("Press ok to remove this timer")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        r.stop();
                    }
                })
                .show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isAppForeground = true;

        activeEvents = new HashMap<>();
        eventId = 0L;
        // bind TimerService
        doBindService();

        // use custom toolbar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        // Check whether the activity is using the layout version
        // with the fragment_container FrameLayout. If so, we must
        // add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.timer_event_fragment);

                if (eventListFragment != null) {
                    Log.i(TAG, "send id to eventlistfragment " + savedInstanceState.getLong(TIMER_ID_EXPIRED));
                    eventListFragment.restartedFromNotification(
                            savedInstanceState.getLong(TIMER_ID_EXPIRED));
                    mTimerEventExpired = null;
                } else {
                    // save the value for when EventListFragment is created
                    mTimerEventExpired = savedInstanceState.getLong(TIMER_ID_EXPIRED);
                }
                return;
            }

            // Create an instance of PhotoGridFragment
            PhotoGridFragment pgf = PhotoGridFragment.newInstance();

            String tag = PhotoGridFragment.class.getName();
            // In case this activity was started with special instructions
            // from an Intent, pass the Intent's extras to the fragment as
            // arguments
            pgf.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout.
            replaceWithFragment(tag, pgf);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
            Log.i(TAG, "onTrimMemory: screen not showing");
            //Screen is not showing
            isAppForeground = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isAppForeground = true;
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.COUNTDOWN_BR);
        registerReceiver(br, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
        unregisterReceiver(br);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mTimerEventExpired = intent.getLongExtra(TIMER_ID_EXPIRED, 0L);
        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.timer_event_fragment);

        if (eventListFragment != null) {
            // in two-pane layout
            eventListFragment.restartedFromNotification(mTimerEventExpired);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        MenuItem item = menu.findItem(R.id.badge);
        MenuItemCompat.setActionView(item, R.layout.feed_update_count);
        Button eventCountButton = (Button) MenuItemCompat.getActionView(item);
        eventCountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                        .findFragmentByTag(EventListFragment.class.getName());
                if (eventListFragment == null) {
                    EventListFragment newFragment = EventListFragment.newInstance();
                    newFragment.setActiveEventList(new ArrayList<>(activeEvents.values()));
                    String tag = EventListFragment.class.getName();
                    replaceWithFragment(tag, newFragment);
                }
            }
        });
        eventCountButton.setText(String.valueOf(eventCount));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: fill menu later if needed.
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public void onPhotoGridInteraction(TimerEvent event) {
        // The user selected a photo from the PhotoGridFragment
        addEvent(event);

        eventCount++;
        invalidateOptionsMenu();
        String tag = EventListFragment.class.getName();

        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentByTag(tag);


        if (eventListFragment != null) {
            // if the fragment is available, we're in two-pane layout.
            eventListFragment.update();
        } else {
            // One-pane layout.
                Log.i(TAG, "onPhotoGridInteraction: creating new EventListFragment");
                // Create fragment and give it an argument for the selected photo.
                EventListFragment newFragment = EventListFragment.newInstance();
                newFragment.setActiveEventList(new ArrayList<>(activeEvents.values()));
                if (mTimerEventExpired != null) {
                    newFragment.restartedFromNotification(mTimerEventExpired);
                    mTimerEventExpired = null;
                }

                replaceWithFragment(tag, newFragment);
        }
    }

    private void replaceWithFragment(String tag, Fragment newFragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, newFragment, tag);
        transaction.addToBackStack(tag);
        transaction.commit();
    }


    private void addEvent(TimerEvent event) {
        try {
            //TODO: total hack...
            TimerEvent clone = event.clone();
            clone.setTimer(clone.getTime());
            clone.setTimerId(eventId);
            activeEvents.put(eventId, clone);
            mTimerService.addTimerEvent(eventId, clone.getTime());
            eventId++;
        } catch (CloneNotSupportedException e) {
            // Can't happen
        }
    }

    private void updateTimers(long[] timeLeft) {
        for (int i = 0; i < timeLeft.length; i+=2) {
            if (activeEvents.containsKey(timeLeft[i])) {
                TimerEvent event = activeEvents.get(timeLeft[i]);
                event.setTimer(timeLeft[i+1]);
                if (event.getTimeLeft() < 0){
                    handleTimerExpiration(event);
                }
            } else  {
                mTimerService.removeTimerEvent(timeLeft[i]);
            }

        }
    }


    @Override
    public void onTimerEventInteraction(TimerEvent event) {
        activeEvents.remove(event.getTimerId());
        eventCount--;
        invalidateOptionsMenu();
    }
}
