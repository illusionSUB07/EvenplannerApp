package com.example.Eveplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * EventDatabaseHelper is a helper class for managing the SQLite database for events.
 */
public class EventDatabaseHelper extends SQLiteOpenHelper implements EventDatabase {

    private static final String DATABASE_NAME = "event.db";
    private static final int DATABASE_VERSION = 2; // Updated database version
    public static final String TABLE_NAME = "events";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_REMINDER = "reminder_datetime";

    /**
     * Constructs a new instance of EventDatabaseHelper.
     *
     * @param context The context of the application.
     */
    public EventDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is created for the first time.
     *
     * @param db The SQLiteDatabase instance.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_ADDRESS + " TEXT, "
                + COLUMN_DATE + " TEXT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_REMINDER + " INTEGER" // Change the data type of the reminder column to INTEGER
                + ")";
        db.execSQL(createTableQuery);
    }

    /**
     * Called when the database needs to be upgraded.
     *
     * @param db         The SQLiteDatabase instance.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle the upgrade by dropping and recreating the table
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Checks if an event with the given name already exists in the database.
     *
     * @param eventName The name of the event to check.
     * @return {@code true} if the event exists, {@code false} otherwise.
     */
    @Override
    public boolean eventExists(String eventName) {
        SQLiteDatabase db = getReadableDatabase();

        String[] projection = {COLUMN_NAME};
        String selection = COLUMN_NAME + "=?";
        String[] selectionArgs = {eventName};

        Cursor cursor = db.query(TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        boolean exists = cursor.getCount() > 0;
        cursor.close();

        return exists;
    }

    /**
     * Inserts a new event into the database.
     *
     * @param values The ContentValues object containing the event details.
     * @return The row ID of the inserted event, or -1 if insertion failed.
     */
    @Override
    public long insertEvent(ContentValues values) {
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, values);
    }
}
