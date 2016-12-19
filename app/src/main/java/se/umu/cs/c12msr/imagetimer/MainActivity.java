package se.umu.cs.c12msr.imagetimer;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements PhotoGridFragment.OnPhotoGridInteractionListener,
        EventListFragment.OnTimerEventInteractionListener {


    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check whether the activity is using the layout version
        // with the fragment_container FrameLayout. If so, we must
        // add the first fragment
        if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }

            // Create an instance of PhotoGridFragment
            PhotoGridFragment pgf = PhotoGridFragment.newInstance();

            // In case this activity was started with special instructions
            // from an Intent, pass the Intent's extras to the fragment as
            // arguments
            pgf.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout.
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, pgf).commit();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //TODO: need the menu?
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            default:
                break;
        }

        return true;
    }


    @Override
    public void onPhotoGridInteraction(TimerEvent event) {
        // TODO: interaction on the grid.

        // The user selected a photo from the PhotoGridFragment

        EventListFragment eventListFragment = (EventListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.timer_event_fragment);

        if (eventListFragment != null) {
            // if the fragment is available, we're in two-pane layout.

            // TODO: start a timer in the EventListFragment
            eventListFragment.addEvent(event);
        } else {
            // One-pane layout.

            // Create fragment and give it an argument for the selected photo.
            EventListFragment newFragment = EventListFragment.newInstance(event.getId(),
                    event.getTime(), event.getImageName(), event.getImageID());

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back.
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);

            // Commit the transaction.
            transaction.commit();
        }
    }

    @Override
    public void onTimerEventInteraction(Uri uri) {
        // TODO: interaction on a timed event.
    }
}
