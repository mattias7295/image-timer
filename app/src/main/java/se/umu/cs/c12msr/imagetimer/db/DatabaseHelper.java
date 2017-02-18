package se.umu.cs.c12msr.imagetimer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import se.umu.cs.c12msr.imagetimer.main.TimerEvent;

/**
 * Created by Mattias-stationary on 22-Aug-16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, DatabaseContract.DATABASE_NAME, null, DatabaseContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseContract.TimerEventEntry.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DatabaseContract.TimerEventEntry.DELETE_TABLE);
        onCreate(db);
    }

    public void asyncInsert(final TimerEvent te) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_IMAGE, te.getImagePath());
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_TIME, te.getTime());
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_EVENT_NAME, te.getName());
                db.insert(DatabaseContract.TimerEventEntry.TABLE_NAME, null, values);
            }
        }).start();
    }

    public long blockingInsert(String imageName, long time, String name) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_IMAGE, imageName);
        values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_TIME, time);
        values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_EVENT_NAME, name);
        return db.insert(DatabaseContract.TimerEventEntry.TABLE_NAME, null, values);
    }

    public List<TimerEvent> fetchAllEvents() {
        String[] projection = {
                DatabaseContract.TimerEventEntry._ID,
                DatabaseContract.TimerEventEntry.COLUMN_NAME_IMAGE,
                DatabaseContract.TimerEventEntry.COLUMN_NAME_TIME,
                DatabaseContract.TimerEventEntry.COLUMN_NAME_EVENT_NAME
        };

        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(DatabaseContract.TimerEventEntry.TABLE_NAME, projection, null, null, null, null, null);

        List<TimerEvent> eventList = new ArrayList<>();
        try {
            while (c.moveToNext()) {
                long id = c.getLong(c.getColumnIndex(DatabaseContract.TimerEventEntry._ID));
                String imagePath = c.getString(c.getColumnIndex(DatabaseContract.TimerEventEntry.COLUMN_NAME_IMAGE));
                long time = c.getLong(c.getColumnIndex(DatabaseContract.TimerEventEntry.COLUMN_NAME_TIME));
                String name = c.getString(c.getColumnIndex(DatabaseContract.TimerEventEntry.COLUMN_NAME_EVENT_NAME));
                eventList.add(new TimerEvent(id, time, imagePath, name));
            }
        } finally {
            c.close();
        }
        return eventList;
    }
}
