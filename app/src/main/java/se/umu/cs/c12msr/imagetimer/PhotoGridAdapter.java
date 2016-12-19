package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by Mattias-stationary on 16-Aug-16.
 */
public class PhotoGridAdapter extends BaseAdapter {

    private static final String TAG = "PhotoGridAdapter";

    private final List<TimerEvent> mTimerEvents;
    private final LayoutInflater mInflater;
    private Context mContext;
    private File extDir;

    public PhotoGridAdapter(Context context, List<TimerEvent> teList) {
        extDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        this.mTimerEvents = teList;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mTimerEvents.size();
    }

    @Override
    public TimerEvent getItem(int position) {
        return mTimerEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mTimerEvents.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View frame = convertView;
        SquareImageView picture;
        TextView name;

        if (frame == null) {
            frame = mInflater.inflate(R.layout.grid_item, parent, false);
            frame.setTag(R.id.grid_item_picture, frame.findViewById(R.id.grid_item_picture));
            frame.setTag(R.id.grid_item_text, frame.findViewById(R.id.grid_item_text));

        }

        TimerEvent event = getItem(position);
        picture = (SquareImageView) frame.getTag(R.id.grid_item_picture);
        name = (TextView) frame.getTag(R.id.grid_item_text);
        //TODO: "name" should describe the event not the path of the file.
        name.setText(event.getImageName());

        //TODO: use imageFile instead in production.
        //File imageFile = new File(extDir + "/" + getItem(position).getImageName());
        Picasso.with(mContext).load(event.getImageID()).resize(80,80).centerCrop().into(picture);
        return frame;
    }

    public void addEvent(TimerEvent te) {
        mTimerEvents.add(te);
        notifyDataSetChanged();
    }

}
