package se.umu.cs.c12msr.imagetimer.main;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import se.umu.cs.c12msr.imagetimer.R;
import se.umu.cs.c12msr.imagetimer.main.SquareImageView;
import se.umu.cs.c12msr.imagetimer.main.TimerEvent;

/**
 * Created by c12msr on 12-Nov-16.
 */
public class EventListAdapter extends BaseAdapter {

    private static final String TAG = "EventListAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<TimerEvent> mEvents;


    final private List<ViewHolder> lstHolders;


    public interface OnEventListListener {
        void handleRemovePressed(TimerEvent event);
    }

    private OnEventListListener mCallback;

    public EventListAdapter(Context context, List<TimerEvent> events, OnEventListListener callback) {
        this.mContext = context;
        this.mEvents = events;
        this.mCallback = callback;

        lstHolders = new ArrayList<>();
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public TimerEvent getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        TimerEvent event = getItem(position);

        if (convertView == null) {

            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.event_list_item, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.event_list_text);
            holder.picture = (SquareImageView) convertView.findViewById(R.id.event_list_image);
            holder.countDownText = (TextView) convertView.findViewById(R.id.event_list_timer_text);
            holder.removeButton = (Button) convertView.findViewById(R.id.event_list_remove_button);


            convertView.setTag(holder);
            synchronized (lstHolders) {
                lstHolders.add(holder);
            }
            //Log.i(TAG, "getView: creating new listviewitem");
            //Log.i(TAG, "getView: lstholder size:  " + lstHolders.size());
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerEvent event = getItem(position);
                mEvents.remove(event);
                notifyDataSetChanged();
                mCallback.handleRemovePressed(event);
            }
        });

        holder.setData(event);

        return convertView;
    }

    public void update() {
        for (ViewHolder holder :
                lstHolders) {
            holder.updateTimeRemaining();
        }
    }

    private class ViewHolder {
        TextView name;
        TextView countDownText;
        SquareImageView picture;
        Button removeButton;
        TimerEvent timerEvent;

        public void setData(TimerEvent event) {
            timerEvent = event;
            name.setText(timerEvent.getName());

            updateTimeRemaining();
            /*
            File imageFile = new File(timerEvent.getImagePath());

            if (imageFile.exists()) {
                Picasso.with(mContext).load(imageFile).fit().into(picture);
            }
            */
            //TODO: testing
            Picasso.with(mContext).load(timerEvent.getImageID()).fit().into(picture);
        }

        public void updateTimeRemaining() {
            long timeInSeconds = timerEvent.getTimeLeft()/1000;
            if (timeInSeconds >= 0) {
                countDownText.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                        timeInSeconds/3600, (timeInSeconds%3600) / 60, timeInSeconds%60));
            } else {
                countDownText.setText(R.string.expired);
            }
        }

    }
}
