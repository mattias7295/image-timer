package se.umu.cs.c12msr.imagetimer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ConfigureEvent extends AppCompatActivity {

    private static final String TAG = "ConfigureEvent";

    public static final String HOURS_SET = "se.umu.cs.c12msr.imagetimer.hours";
    public static final String MINUTES_SET = "se.umu.cs.c12msr.imagetimer.minutes";
    public static final String NAME_SET = "se.umu.cs.c12msr.imagetimer.name";

    private ImageView mPictureView;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private EditText mNameTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_configure_event_tool_bar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        String imageFile = intent.getStringExtra(PhotoGridFragment.MESSAGE);

        mHoursPicker = (NumberPicker) findViewById(R.id.activity_ce_hours_picker);
        if (mHoursPicker != null) {
            mHoursPicker.setMaxValue(23);
            mHoursPicker.setMinValue(0);
            mHoursPicker.setWrapSelectorWheel(false);
        }

        mMinutesPicker = (NumberPicker) findViewById(R.id.activity_ce_minutes_picker);
        if (mMinutesPicker != null) {
            mMinutesPicker.setMaxValue(59);
            mMinutesPicker.setMinValue(0);
            mMinutesPicker.setWrapSelectorWheel(false);
        }

        mNameTV = (EditText) findViewById(R.id.name_tv);

        mPictureView = (ImageView) findViewById(R.id.activity_configure_event_image);

        if (StorageHelper.isExternalStorageReadable()) {
            File extDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            final String imageFilePath;
            if (extDir != null) {
                imageFilePath = extDir.getAbsolutePath() + "/" + imageFile;
                Picasso.with(this).load(imageFilePath).into(mPictureView);
            }
        }
        /*
        mPictureView.post(new Runnable() {
            @Override
            public void run() {
                // Get the dimensions of the ImageView
                int targetW = mPictureView.getWidth();
                int targetH = mPictureView.getHeight();

                // Get the dimensions of the bitmap
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imageFilePath, bmOptions);
                int photoW = bmOptions.outWidth;
                int photoH = bmOptions.outHeight;

                // Determine how much to scale down the image
                int scaleFactor = Math.min(photoW/ targetW, photoH/ targetH);

                // Decode the image file into a Bitmap sized to fill the View
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = scaleFactor;

                Bitmap bitmap = BitmapFactory.decodeFile(imageFilePath, bmOptions);
                mPictureView.setImageBitmap(bitmap);
            }
        });
        */
    }

    public void confirmButtonPressed(View view) {
        int hours = mHoursPicker.getValue();
        int minutes = mMinutesPicker.getValue();
        String name = mNameTV.getText().toString();

        Intent intent = new Intent();
        intent.putExtra(HOURS_SET, hours);
        intent.putExtra(MINUTES_SET, minutes);
        intent.putExtra(NAME_SET, name);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void cancelButtonPressed(View view) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }



}
