package com.example.Eveplanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.EditText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * JUnitTests is a test class that contains unit tests for the EventDatabaseHelper and AddEventActivity
 * and SaveEvent classes.
 */
@RunWith(AndroidJUnit4.class)
public class JUnitTests {

    private EventDatabaseHelper databaseHelper;
    private SQLiteDatabase database;


    @Before
    public void setup() {
        databaseHelper = new EventDatabaseHelper(ApplicationProvider.getApplicationContext());
        database = databaseHelper.getWritableDatabase();


    }

    @After
    public void cleanup() {
        database.delete(EventDatabaseHelper.TABLE_NAME, null, null);
        database.close();
        databaseHelper.close();
    }

    @Test
    public void testEventExists_whenEventDoesNotExist() {
        boolean exists = databaseHelper.eventExists("Non-existent Event");
        assertFalse(exists);
    }

    @Test
    public void testEventExists_whenEventExists() {
        // Insert a sample event for testing
        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, "Sample Event");
        long eventId = database.insert(EventDatabaseHelper.TABLE_NAME, null, values);

        boolean exists = databaseHelper.eventExists("Sample Event");
        assertTrue(exists);
    }

    @Test
    public void testInsertEvent() {
        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, "New Event");
        values.put(EventDatabaseHelper.COLUMN_ADDRESS, "Event Address");
        values.put(EventDatabaseHelper.COLUMN_DATE, "2023-07-09");
        values.put(EventDatabaseHelper.COLUMN_TYPE, "Meeting");
        values.put(EventDatabaseHelper.COLUMN_REMINDER, 123456789);

        long id = databaseHelper.insertEvent(values);
        assertTrue(id != -1);

        Cursor cursor = database.query(EventDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        String eventName = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_NAME));
        String eventAddress = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_ADDRESS));
        String eventDate = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_DATE));
        String eventType = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_TYPE));
        long eventReminder = cursor.getLong(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_REMINDER));

        assertEquals("New Event", eventName);
        assertEquals("Event Address", eventAddress);
        assertEquals("2023-07-09", eventDate);
        assertEquals("Meeting", eventType);
        assertEquals(123456789, eventReminder);

        cursor.close();
    }

    @Test
    public void testSaveEvent_withValidData() {
        // Cleanup existing data in the database
        databaseHelper.onUpgrade(database, 0, 0);

        // Create a ContentValues object with valid event data
        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, "Sample Event");
        values.put(EventDatabaseHelper.COLUMN_ADDRESS, "Event Address");
        values.put(EventDatabaseHelper.COLUMN_DATE, "2023-07-09");
        values.put(EventDatabaseHelper.COLUMN_TYPE, "Meeting");
        values.put(EventDatabaseHelper.COLUMN_REMINDER, 123456789);

        // Perform the insert operation
        long id = databaseHelper.insertEvent(values);

        // Verify that the insert operation was successful
        assertTrue(id != -1);

        // Query the database to retrieve the inserted event
        Cursor cursor = database.query(EventDatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        assertNotNull(cursor);
        assertEquals(1, cursor.getCount());

        cursor.moveToFirst();
        String eventName = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_NAME));
        String eventAddress = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_ADDRESS));
        String eventDate = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_DATE));
        String eventType = cursor.getString(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_TYPE));
        long eventReminder = cursor.getLong(cursor.getColumnIndex(EventDatabaseHelper.COLUMN_REMINDER));

        // Verify the inserted event data
        assertEquals("Sample Event", eventName);
        assertEquals("Event Address", eventAddress);
        assertEquals("2023-07-09", eventDate);
        assertEquals("Meeting", eventType);
        assertEquals(123456789, eventReminder);

        cursor.close();
    }

    @Test
    public void testSaveEvent_withEmptyFields() {
        // Set up the database
        databaseHelper.onCreate(database);

        // Launch the AddEventActivity using ActivityScenario
        ActivityScenario<AddEventActivity> scenario = ActivityScenario.launch(AddEventActivity.class);

        // Perform UI interactions within the activity
        scenario.onActivity(activity -> {
            EditText eventNameEditText = activity.findViewById(R.id.event_name);
            EditText eventAddressEditText = activity.findViewById(R.id.event_address);
            EditText eventDateEditText = activity.findViewById(R.id.event_date);
            EditText eventTypeEditText = activity.findViewById(R.id.event_type);

            // Set empty values for all fields
            eventNameEditText.setText("");
            eventAddressEditText.setText("");
            eventDateEditText.setText("");
            eventTypeEditText.setText("");

            // Trigger the save event button click
            activity.findViewById(R.id.save_event).performClick();

            // Verify that the event is not saved in the database
            boolean eventExists = databaseHelper.eventExists("Sample Event");
            assertFalse(eventExists);
        });

        // Close the activity scenario
        scenario.close();
    }

    }