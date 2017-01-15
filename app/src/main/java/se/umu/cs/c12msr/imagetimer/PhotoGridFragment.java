package se.umu.cs.c12msr.imagetimer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class PhotoGridFragment extends Fragment {

    public static final String MESSAGE = "se.umu.cs.c12msr.imagetimer.message";
    private static final String TAG = "PhotoGridFragment";

    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CONFIGURE_EVENT = 2;

    private GridView mPictureGrid;
    private OnPhotoGridInteractionListener mCallback;
    private DatabaseHelper dbHelper;
    private String mCurrentPhoto;
    private List<TimerEvent> mTimerEvents;

    public PhotoGridFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PhotoGridFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PhotoGridFragment newInstance() {
        PhotoGridFragment fragment = new PhotoGridFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        // TODO: Use these on real demo
        dbHelper = new DatabaseHelper(getActivity());
        mTimerEvents  = dbHelper.fetchAllEvents();

        // FOR TESTING
        //mTimerEvents = createTempEvents();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_photo_grid, container, false);

        mPictureGrid = (GridView) view.findViewById(R.id.fragment_photogrid_gv);
        mPictureGrid.setAdapter(new PhotoGridAdapter(getActivity(), mTimerEvents));
        mPictureGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                mCallback.onPhotoGridInteraction((TimerEvent) parent.getAdapter().getItem(position));
            }
        });

        FloatingActionButton fab = (FloatingActionButton) view
                .findViewById(R.id.fragment_photogrid_fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    takePhoto();
                }
            });
        }


        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        if (context instanceof OnPhotoGridInteractionListener) {
            mCallback = (OnPhotoGridInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnPhotoGridInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Intent intent = new Intent(getActivity(), ConfigureEvent.class);
            intent.putExtra(MESSAGE, mCurrentPhoto);
            startActivityForResult(intent, REQUEST_CONFIGURE_EVENT);
        } else if (requestCode == REQUEST_CONFIGURE_EVENT) {
            if (resultCode == Activity.RESULT_OK) {
                /* get the time set */
                int hours = data.getIntExtra(ConfigureEvent.HOURS_SET, 0);
                int minutes = data.getIntExtra(ConfigureEvent.MINUTES_SET, 0);
                int seconds = data.getIntExtra(ConfigureEvent.SECONDS_SET, 0);

                /* compute time in ms */
                long time = hours * 60 * 60 * 1000 +
                            minutes * 60 * 1000 +
                            seconds * 1000;

                /* insert value in db and create TimerEvent */
                long id = dbHelper.blockingInsert(mCurrentPhoto, time);
                TimerEvent te = new TimerEvent(id, mCurrentPhoto, time);
                ((PhotoGridAdapter) mPictureGrid.getAdapter()).addEvent(te);

            } else if (resultCode == Activity.RESULT_CANCELED) {
                removeFile(mCurrentPhoto);
            }
        }
    }

    private void takePhoto() {
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
            Uri photoURI = FileProvider.getUriForFile(getActivity(),
                    "com.example.android.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhoto = image.getName();
        Log.i(TAG, image.getAbsolutePath());
        return image;
    }

    private void removeFile(String name) {
        File photoFile = new File(name);
        boolean deleted = photoFile.delete();

        if (deleted) {
            Log.i(TAG, "removeFile: file deleted");
        } else {
            Log.i(TAG, "removeFile: could'nt delete file");
        }
    }


    public interface OnPhotoGridInteractionListener {
        void onPhotoGridInteraction(TimerEvent event);
    }

    //TODO: temp way to create events
    private List<TimerEvent> createTempEvents() {
        ArrayList<TimerEvent> tmp = new ArrayList<>();
        for (int i = 0; i < mTestImageIds.length; i++) {
            TimerEvent event = new TimerEvent(i, "test" + i, (i+1)*1000*10);
            event.setImageID(mTestImageIds[i]);
            tmp.add(event);
        }
        return tmp;
    }

    // FOR TESTING
    private Integer mTestImageIds[] = {
            R.drawable.blue, R.drawable.red,
            R.drawable.green, R.drawable.cyan};
}
