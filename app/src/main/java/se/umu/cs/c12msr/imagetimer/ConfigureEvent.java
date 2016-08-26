package se.umu.cs.c12msr.imagetimer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;

public class ConfigureEvent extends AppCompatActivity {

    public static final String HOURS_SET = "se.umu.cs.c12msr.imagetimer.hours";
    public static final String MINUTES_SET = "se.umu.cs.c12msr.imagetimer.minutes";
    public static final String SECONDS_SET = "se.umu.cs.c12msr.imagetimer.seconds";

    private ImageView mPictureView;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;
    private String imagePath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_configure_event_tool_bar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        imagePath = intent.getStringExtra(MainActivity.MESSAGE);

        mHoursPicker = (NumberPicker) findViewById(R.id.activity_ce_hours_picker);
        mHoursPicker.setMaxValue(23);
        mHoursPicker.setMinValue(0);
        mHoursPicker.setWrapSelectorWheel(false);

        mMinutesPicker = (NumberPicker) findViewById(R.id.activity_ce_minutes_picker);
        mMinutesPicker.setMaxValue(59);
        mMinutesPicker.setMinValue(0);
        mMinutesPicker.setWrapSelectorWheel(false);

        mSecondsPicker = (NumberPicker) findViewById(R.id.activity_ce_seconds_picker);
        mSecondsPicker.setMaxValue(59);
        mSecondsPicker.setMinValue(0);
        mSecondsPicker.setWrapSelectorWheel(false);

        mPictureView = (ImageView) findViewById(R.id.activity_configure_event_image);

        mPictureView.post(new Runnable() {
            @Override
            public void run() {
                setPic(imagePath);
            }
            private void setPic(String mCurrentPhotoPath) {
                // Get the dimensions of the ImageView
                int targetW = mPictureView.getWidth();
                int targetH = mPictureView.getHeight();

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
                mPictureView.setImageBitmap(bitmap);
            }
        });

    }

    public void confirmButtonPressed(View view) {
        int hours = mHoursPicker.getValue();
        int minutes = mMinutesPicker.getValue();
        int seconds = mSecondsPicker.getValue();

        Intent intent = new Intent();
        intent.putExtra(HOURS_SET, hours);
        intent.putExtra(MINUTES_SET, minutes);
        intent.putExtra(SECONDS_SET, seconds);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void cancelButtonPressed(View view) {
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED, intent);
        finish();
    }



}
