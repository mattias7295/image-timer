package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        void handleTimerExpiration(TimerEvent event);
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
        } else {
            holder = (ViewHolder) convertView.getTag();

        }
        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerEvent event = getItem(position);
                mEvents.remove(event);
                mCallback.handleRemovePressed(event);
                notifyDataSetChanged();
            }
        });

        holder.setData(event);

        return convertView;
    }

    public void updateTimers(long timeLeft[]) {
        //TODO: try to find better solution for this.
        for (int i = 0; i < timeLeft.length; i+=2) {
            for (int j = 0; j < lstHolders.size(); j++) {
                ViewHolder holder = lstHolders.get(j);
                if (holder.timerEvent.getTimerId() == timeLeft[i]) {
                    holder.timerEvent.setTimer(timeLeft[i+1]);
                    holder.updateTimeRemaining();
                    break;
                }
            }
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
            name.setText(timerEvent.getImageName());

            updateTimeRemaining();
            Picasso.with(mContext).load(timerEvent.getImageID()).into(picture);
        }

        public void updateTimeRemaining() {
            long timeInSeconds = timerEvent.getTimeLeft()/1000;
            if (timeInSeconds >= 0) {
                countDownText.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d",
                        timeInSeconds/3600, (timeInSeconds%3600) / 60, timeInSeconds%60));
            } else {
                countDownText.setText("Expired!!");
                mCallback.handleTimerExpiration(timerEvent);
            }
        }

    }
}
