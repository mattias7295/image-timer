package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


public class EventListFragment extends Fragment {

    private static final String TAG = "EventListFragment";

    private static final String ARG_ID      = "arg_id";
    private static final String ARG_TIME    = "arg_time";
    private static final String ARG_IMAGE   = "arg_image";
    private static final String ARG_IMAGEID = "arg_imageid";

    private OnTimerEventInteractionListener mCallback;
    private ListView mListView;
    private List<TimerEvent> mEvents;


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
            mEvents.add(event);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timer_event, container, false);

        mListView = (ListView) view.findViewById(R.id.fragment_timerevent_lv);
        mListView.setAdapter(new EventListAdapter(getActivity(), mEvents));

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
    public void onPause() {
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }


    /**
     *  Add a event to the list of running events.
     * @param event the event to add to the list
     */
    public void addEvent(TimerEvent event) {
        mEvents.add(event);
    }


    public interface OnTimerEventInteractionListener {
        // TODO: Update argument type and name
        void onTimerEventInteraction(Uri uri);
    }
}
