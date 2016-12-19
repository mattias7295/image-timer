package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by c12msr on 12-Nov-16.
 */
public class EventListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<TimerEvent> mEvents;

    public EventListAdapter(Context context, List<TimerEvent> events) {
        this.mContext = context;
        this.mEvents = events;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View frame = convertView;
        SquareImageView picture;
        TextView description;
        TextView countDownText;

        if (frame == null) {
            frame = mInflater.inflate(R.layout.event_list_item, parent, false);
            frame.setTag(R.id.event_list_image, frame.findViewById(R.id.event_list_image));
            frame.setTag(R.id.event_list_text, frame.findViewById(R.id.event_list_text));
            frame.setTag(R.id.event_list_timer_text, frame.findViewById(R.id.event_list_timer_text));

        }
        TimerEvent event = getItem(position);

        picture = (SquareImageView) frame.getTag(R.id.event_list_image);
        description = (TextView) frame.getTag(R.id.event_list_text);
        countDownText = (TextView) frame.getTag(R.id.event_list_timer_text);
        //TODO: should show description of the image.
        description.setText(event.getImageName());
        event.startTimer(countDownText);

        Picasso.with(mContext).load(event.getImageID()).into(picture);
        return frame;
    }
}
