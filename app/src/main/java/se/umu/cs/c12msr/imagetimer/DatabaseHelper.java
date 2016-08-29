package se.umu.cs.c12msr.imagetimer;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_EVENT_ID, "test");
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_IMAGE, te.getImageName());
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_HOURS, te.getHours());
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_MINUTES, te.getMinutes());
                values.put(DatabaseContract.TimerEventEntry.COLUMN_NAME_SECONDS, te.getSeconds());
                db.insert(DatabaseContract.TimerEventEntry.TABLE_NAME, null, values);
            }
        }).start();
    }
}
