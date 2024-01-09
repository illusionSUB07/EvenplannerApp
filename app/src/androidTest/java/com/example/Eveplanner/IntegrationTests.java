package com.example.Eveplanner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * , this test verifies that if an event can be successfully inserted into the database and then retrieved,
 * with all details correctly saved and returned or not while testing the
 * interaction between multiple components (the database helper, the SQLite database, and the event data model).
 */
@RunWith(AndroidJUnit4.class)
public class IntegrationTests {

    private EventDatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    @Before
    public void setup() {
        // Create a new instance of EventDatabaseHelper
        databaseHelper = new EventDatabaseHelper(InstrumentationRegistry.getInstrumentation().getTargetContext());

        // Get a writable database
        database = databaseHelper.getWritableDatabase();
    }

    @After
    public void cleanup() {
        // Clean up the database
        database.delete(EventDatabaseHelper.TABLE_NAME, null, null);
        database.close();
        databaseHelper.close();
    }

    @Test
    public void testEventInsertionAndRetrieval() {
        // Prepare test data
        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, "Event 1");
        values.put(EventDatabaseHelper.COLUMN_ADDRESS, "Address 1");
        values.put(EventDatabaseHelper.COLUMN_DATE, "2022-01-01");
        values.put(EventDatabaseHelper.COLUMN_TYPE, "Type 1");

        // Insert the event into the database
        long eventId = database.insert(EventDatabaseHelper.TABLE_NAME, null, values);

        // Verify that the insertion was successful
        Assert.assertNotEquals(-1, eventId);

        // Retrieve the event from the database
        Cursor cursor = database.query(
                EventDatabaseHelper.TABLE_NAME,
                null,
                EventDatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(eventId)},
                null,
                null,
                null
        );

        // Check if the cursor is not null and has a record
        Assert.assertNotNull(cursor);
        Assert.assertEquals(1, cursor.getCount());

        // Retrieve the event details from the cursor
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_NAME));
            String address = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_ADDRESS));
            String date = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_DATE));
            String type = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_TYPE));

            // Verify the event details
            Assert.assertEquals("Event 1", name);
            Assert.assertEquals("Address 1", address);
            Assert.assertEquals("2022-01-01", date);
            Assert.assertEquals("Type 1", type);
        }

        cursor.close();
    }
}
