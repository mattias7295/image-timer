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
import java.util.Collection;
import java.util.List;

import se.umu.cs.c12msr.imagetimer.R;


public class EventListFragment extends Fragment implements EventListAdapter.OnEventListListener {

    private static final String TAG = "EventListFragment";


    public static final String ARG_NEW_EVENT = "arg_new_event";
    public static final String ARG_ACTIVE_EVENTS = "arg_active_events";

    private OnTimerEventInteractionListener mCallback;
    private ListView mListView;
    private List<TimerEvent> mEvents;


    private EventListAdapter mAdapter;


    public EventListFragment() {
        // Required empty public constructor
    }

    public static EventListFragment newInstance(TimerEvent event) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_NEW_EVENT, event);
        EventListFragment fragment = new EventListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static EventListFragment newInstance(ArrayList<TimerEvent> activeEvents) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARG_ACTIVE_EVENTS, activeEvents);
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
            TimerEvent event = args.getParcelable(ARG_NEW_EVENT);
            if (event != null) {
                mEvents.add(event);
            }
            ArrayList<TimerEvent> events = args.getParcelableArrayList(ARG_ACTIVE_EVENTS);
            if (events != null) {
                mEvents.addAll(events);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer_event, container, false);
        mAdapter = new EventListAdapter(getActivity(), mEvents, this);
        mListView = (ListView) view.findViewById(R.id.fragment_timerevent_lv);
        mListView.setAdapter(mAdapter);



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

    public void addActiveEvent(TimerEvent event) {
        mEvents.add(event);
        mAdapter.notifyDataSetChanged();
    }

    public void setActiveEvents(Collection<TimerEvent> events) {
        mEvents.addAll(events);
        mAdapter.notifyDataSetChanged();
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
