package se.umu.cs.c12msr.imagetimer;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    public static final String MESSAGE = "se.umu.cs.c12msr.imagetimer.message";

    private static final String TAG = "MainActivity";
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CONFIGURE_EVENT = 2;

    private GridView mPictureGrid;
    private String mCurrentPhotoPath;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_main_tool_bar);
        setSupportActionBar(toolbar);

        dbHelper = new DatabaseHelper(getApplicationContext());

        mPictureGrid = (GridView) findViewById(R.id.activity_main_image_grid);
        mPictureGrid.setAdapter(new TimerEventAdapter(this, new ArrayList<TimerEvent>()));
        mPictureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(MainActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, ConfigureEvent.class);
            intent.putExtra(MESSAGE, mCurrentPhotoPath);
            startActivityForResult(intent, REQUEST_CONFIGURE_EVENT);
        } else if (requestCode == REQUEST_CONFIGURE_EVENT) {
            if (resultCode == RESULT_OK) {
                TimerEvent te = createTimerEvent(data);
                ((TimerEventAdapter) mPictureGrid.getAdapter()).addEvent(te);
                dbHelper.asyncInsert(te);
            } else if (resultCode == RESULT_CANCELED) {

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_take_photo:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.example.android.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                break;
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return true;
    }

    private TimerEvent createTimerEvent(Intent data) {
        int hours = data.getIntExtra(ConfigureEvent.HOURS_SET, 0);
        int minutes = data.getIntExtra(ConfigureEvent.MINUTES_SET, 0);
        int seconds = data.getIntExtra(ConfigureEvent.SECONDS_SET, 0);
        TimerEvent te = new TimerEvent(mCurrentPhotoPath, hours, minutes, seconds);
        return te;
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
