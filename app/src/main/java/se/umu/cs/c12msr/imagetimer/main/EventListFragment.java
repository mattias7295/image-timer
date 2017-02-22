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
import java.util.List;

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
    private List<TimerEvent> mEvents;


    private EventListAdapter mAdapter;


    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance(long id, long time, String imagePath,
                                                int imageId, String name) {
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

    public static EventListFragment newInstance() {
        return new EventListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer_event, container, false);
        mAdapter = new EventListAdapter(getActivity(), mEvents, this);
        mListView = (ListView) view.findViewById(R.id.fragment_timerevent_lv);
        mListView.setAdapter(mAdapter);

        Bundle args = getArguments();
        if (args != null) {
            long id = args.getLong(ARG_ID);
            long time = args.getLong(ARG_TIME);
            String imagePath = args.getString(ARG_IMAGE_PATH);
            int imageId = args.getInt(ARG_IMAGEID);
            String name = args.getString(ARG_NAME);

            TimerEvent event = new TimerEvent(id, time, imagePath, name);
            event.setImageID(imageId);
        }
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
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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

    public void setActiveEventList(List<TimerEvent> list) {
        this.mEvents = list;
    }


    public void update() {
        mAdapter.update();
    }


    @Override
    public void handleRemovePressed(TimerEvent event) {
        // Tell MainActivity that a event was removed
        mCallback.onTimerEventInteraction(event);
    }



    public interface OnTimerEventInteractionListener {
        // TODO: Update argument type and name
        void onTimerEventInteraction(TimerEvent event);
    }
}
