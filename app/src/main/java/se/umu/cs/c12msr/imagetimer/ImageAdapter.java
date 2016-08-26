package se.umu.cs.c12msr.imagetimer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mattias-stationary on 16-Aug-16.
 */
public class ImageAdapter extends BaseAdapter {

    private final File[] mImages;
    private final LayoutInflater mInflater;
    private Context mContext;

    public ImageAdapter(Context context) {
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mImages = dir.listFiles();
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @Override
    public int getCount() {
        return mImages.length;
    }

    @Override
    public File getItem(int position) {
        return mImages[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View frame = convertView;
        ResizableImageView picture;
        TextView name;
        if (frame == null) {

            frame = mInflater.inflate(R.layout.grid_item, parent, false);
            frame.setTag(R.id.grid_item_picture, frame.findViewById(R.id.grid_item_picture));
            frame.setTag(R.id.grid_item_text, frame.findViewById(R.id.grid_item_text));

        }
        picture = (ResizableImageView) frame.getTag(R.id.grid_item_picture);
        name = (TextView) frame.getTag(R.id.grid_item_text);
        name.setText("test");
        Picasso.with(mContext).load(mImages[position]).resize(80,80).centerCrop().into(picture);
        return frame;
    }
    /*
    private void setPic(String mCurrentPhotoPath, ResizableImageView imageView) {
        // Get the dimensions of the ImageView
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/ targetW, photoH/ targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }*/

}
