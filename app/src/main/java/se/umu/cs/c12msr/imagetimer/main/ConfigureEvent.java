package se.umu.cs.c12msr.imagetimer.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.squareup.picasso.Picasso;

import java.io.File;

import se.umu.cs.c12msr.imagetimer.R;

public class ConfigureEvent extends AppCompatActivity {

    private static final String TAG = "ConfigureEvent";

    public static final String HOURS_SET = "se.umu.cs.c12msr.imagetimer.hours";
    public static final String MINUTES_SET = "se.umu.cs.c12msr.imagetimer.minutes";
    public static final String NAME_SET = "se.umu.cs.c12msr.imagetimer.name";

    private ImageView mPictureView;
    private NumberPicker mHoursPicker;
    private NumberPicker mMinutesPicker;
    private EditText tvName;


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
        String imageFilePath = intent.getStringExtra(PhotoGridFragment.MESSAGE);

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

        tvName = (EditText) findViewById(R.id.name_tv);

        mPictureView = (ImageView) findViewById(R.id.activity_configure_event_image);

        /*
        File image = new File(imageFilePath);
        if (image.exists()) {
            Picasso.with(this).load(image).into(mPictureView);
        }
        */
        Picasso.with(this).load(R.drawable.blue).into(mPictureView);
    }

    public void confirmButtonPressed(View view) {
        int hours = mHoursPicker.getValue();
        int minutes = mMinutesPicker.getValue();
        String name = tvName.getText().toString();

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
