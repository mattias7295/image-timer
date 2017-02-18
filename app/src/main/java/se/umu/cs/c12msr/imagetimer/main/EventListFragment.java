package se.umu.cs.c12msr.imagetimer.main;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import se.umu.cs.c12msr.imagetimer.R;


public class EventListFragment extends Fragment implements EventListAdapter.OnEventListListener {

    private static final String TAG = "EventListFragment";

    private static final String RUNNING_TIMER_EVENTS = "running_timer_events";
    private static final String CURRENT_COUNTER_VALUE = "current_counter_value";

    private static final String ARG_ID              = "arg_id";
    private static final String ARG_TIME            = "arg_time";
    private static final String ARG_IMAGE_PATH      = "arg_image_path";
    private static final String ARG_IMAGEID         = "arg_imageid";
    private static final String ARG_NAME            = "arg_name";

    private OnTimerEventInteractionListener mCallback;
    private ListView mListView;
    private ArrayList<TimerEvent> mEvents;

    private TimerService mBoundService;
    private boolean mIsBound;

    private long mCounter;

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBoundService = ((TimerService.LocalBinder)service).getService();

            Toast.makeText(getActivity(), R.string.timer_service_connected, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBoundService = null;

            Toast.makeText(getActivity(), R.string.timer_service_disconnected, Toast.LENGTH_SHORT).show();
        }
    };

    private void doBindService() {
        Log.i(TAG, "Binding service");
        getActivity().bindService(new Intent(getActivity(), TimerService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    private void doUnbindService() {
        if (mIsBound) {
            Log.i(TAG, "Unbinding service");
            getActivity().unbindService(mConnection);
            mIsBound = false;
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PowerManager.ACTION_DEVICE_IDLE_MODE_CHANGED)) {
                Log.i(TAG, "onReceive: Device idle mode changed");
            } else if (intent.getAction().equals(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED)) {
                Log.i(TAG, "onReceive: Power save mode changed");
            } else if (intent.getAction().equals(TimerService.COUNTDOWN_BR)) {
                if (intent.getExtras() != null) {
                    long timeLeft[] = intent.getLongArrayExtra(TimerService.TIME_LEFT);
                    ((EventListAdapter)mListView.getAdapter()).updateTimers(timeLeft);
                }
            }
        }
    };

    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance(long id, long time, String imagePath, int imageId, String name) {
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putLong(ARG_TIME, time);
        args.putString(ARG_IMAGE_PATH, imagePath);
        args.putInt(ARG_IMAGEID, imageId);
        args.putString(ARG_NAME, name);
        EventListFragment fragment = new EventListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle args = getArguments();
        if (args != null) {
            long id = args.getLong(ARG_ID);
            long time = args.getLong(ARG_TIME);
            String imagePath = args.getString(ARG_IMAGE_PATH);
            int imageId = args.getInt(ARG_IMAGEID);
            String name = args.getString(ARG_NAME);

            TimerEvent event = new TimerEvent(id, time, imagePath, name);
            event.setImageID(imageId);
            addEvent(event);
        }
        doBindService();

        if (savedInstanceState != null) {
            mEvents = savedInstanceState.getParcelableArrayList(RUNNING_TIMER_EVENTS);
            for (TimerEvent event : mEvents) {
                if (mBoundService.hasTimerEventExpired(event.getTimerId())) {
                    event.setTimer(-1L);
                }
            }
            mCounter = savedInstanceState.getLong(CURRENT_COUNTER_VALUE, 1L);
        } else {
            mEvents = new ArrayList<>();
            mCounter = 1L;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer_event, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_timerevent_lv);
        mListView.setAdapter(new EventListAdapter(getActivity(), mEvents, this));

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTimerEventInteractionListener) {
            mCallback = (OnTimerEventInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTimerEventInteractionListener");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "Saving state");

        outState.putParcelableArrayList(RUNNING_TIMER_EVENTS, mEvents);
        outState.putLong(CURRENT_COUNTER_VALUE, mCounter);
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Register broadcast receiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(TimerService.COUNTDOWN_BR);
        getActivity().registerReceiver(br, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Unregister broadcast receiver");
        getActivity().unregisterReceiver(br);

    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        try {
            getActivity().unregisterReceiver(br);
        } catch (Exception e) {
            // Receiver was probably already stopped in onPause()
        }
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
    }

    public void restartedFromNotification(long timerEventId) {
        for (TimerEvent event : mEvents) {
            if (event.getTimerId() == timerEventId) {
                // set expired
                event.setTimer(-1L);
                break;
            }
        }
        ((EventListAdapter)mListView.getAdapter()).notifyDataSetChanged();
    }

    /**
     *  Add a event to the list of running events.
     * @param event the event to add to the list
     */
    public void addEvent(TimerEvent event) {
        if (!mIsBound) {
            doBindService();
        }
        try {
            TimerEvent clone = event.clone();
            clone.setTimer(clone.getTime());
            clone.setTimerId(mCounter++);
            mEvents.add(clone);
            mBoundService.addTimerEvent(clone.getTimerId(), clone.getTimeLeft());
            ((EventListAdapter) mListView.getAdapter()).notifyDataSetChanged();
        } catch (CloneNotSupportedException e) {
            // Can't happen
        }
    }

    @Override
    public void handleRemovePressed(TimerEvent event) {
        // Remove event from service
        mBoundService.removeTimerEvent(event.getTimerId());
    }

    @Override
    public void handleTimerExpiration(TimerEvent event) {
        // Show alertdialog with sound

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        final Ringtone r = RingtoneManager.getRingtone(getActivity(), soundUri);
        r.play();

        new AlertDialog.Builder(getActivity())
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


    public interface OnTimerEventInteractionListener {
        // TODO: Update argument type and name
        void onTimerEventInteraction(Uri uri);
    }
}