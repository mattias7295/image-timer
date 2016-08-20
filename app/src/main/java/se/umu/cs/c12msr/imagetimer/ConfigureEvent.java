package se.umu.cs.c12msr.imagetimer;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;

public class ConfigureEvent extends AppCompatActivity {

    private ImageView mPictureView;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private NumberPicker mSecondsPicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_event);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_configure_event_tool_bar);
        setSupportActionBar(toolbar);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //Intent intent = getIntent();
        //final String imagePath = intent.getStringExtra(MainActivity.MESSAGE);

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
        /*
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
        */
    }

    public void confirmButtonPressed(View view) {

    }

    public void cancelButtonPressed(View view) {

    }



}
