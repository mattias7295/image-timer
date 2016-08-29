package se.umu.cs.c12msr.imagetimer;

import android.provider.BaseColumns;

/**
 * Created by Mattias-stationary on 22-Aug-16.
 */
public final class DatabaseContract {

    public static final  int    DATABASE_VERSION   = 1;
    public static final  String DATABASE_NAME      = "timeevents.db";
    private static final String TEXT_TYPE          = " TEXT";
    private static final String INTEGER_TYPE       = " INTEGER";
    private static final String COMMA_SEP          = ",";

    private DatabaseContract() {
    }

    public static abstract class TimerEventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_NAME_EVENT_ID = "eventid";
        public static final String COLUMN_NAME_IMAGE = "image";
        public static final String COLUMN_NAME_HOURS = "hours";
        public static final String COLUMN_NAME_MINUTES = "minutes";
        public static final String COLUMN_NAME_SECONDS = "seconds";

        public static final String CREATE_TABLE = "CREATE TABLE " +
                TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_EVENT_ID + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_IMAGE + TEXT_TYPE + COMMA_SEP +
                COLUMN_NAME_HOURS + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_MINUTES + INTEGER_TYPE + COMMA_SEP +
                COLUMN_NAME_SECONDS + INTEGER_TYPE +
                " )";
        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
