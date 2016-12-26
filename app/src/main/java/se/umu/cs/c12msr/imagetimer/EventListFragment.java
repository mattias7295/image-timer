package se.umu.cs.c12msr.imagetimer;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EventListFragment extends Fragment implements EventListAdapter.OnEventListListener {

    private static final String TAG = "EventListFragment";

    private static final String ARG_ID      = "arg_id";
    private static final String ARG_TIME    = "arg_time";
    private static final String ARG_IMAGE   = "arg_image";
    private static final String ARG_IMAGEID = "arg_imageid";

    private OnTimerEventInteractionListener mCallback;
    private ListView mListView;
    private List<TimerEvent> mEvents;

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
            if (intent.getExtras() != null) {
                long timeLeft[] = intent.getLongArrayExtra(TimerService.TIME_LEFT);
                ((EventListAdapter)mListView.getAdapter()).updateTimers(timeLeft);
            }
        }
    };

    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance(long id, long time, String imagePath, int imageId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        args.putLong(ARG_TIME, time);
        args.putString(ARG_IMAGE, imagePath);
        args.putInt(ARG_IMAGEID, imageId);
        EventListFragment fragment = new EventListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvents = new ArrayList<>();

        Bundle args = getArguments();
        if (args != null) {
            long id = args.getLong(ARG_ID);
            long time = args.getLong(ARG_TIME);
            String imageP = args.getString(ARG_IMAGE);
            int imageId = args.getInt(ARG_IMAGEID);

            TimerEvent event = new TimerEvent(id, imageP, time);
            event.setImageID(imageId);
            addEvent(event);
        }
        doBindService();

        mCounter = 1L;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer_event, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_timerevent_lv);
        mListView.setAdapter(new EventListAdapter(getActivity(), mEvents, this));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

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
    public void onResume() {
        super.onResume();
        Log.i(TAG, "Register broadcast receiver");
        getActivity().registerReceiver(br, new IntentFilter(TimerService.COUNTDOWN_BR));
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
            e.printStackTrace();
        }
    }

    @Override
    public void handleRemovePressed(TimerEvent event) {
        // Remove event from service
        mBoundService.removeTimerEvent(event.getTimerId());
    }


    public interface OnTimerEventInteractionListener {
        // TODO: Update argument type and name
        void onTimerEventInteraction(Uri uri);
    }
}
