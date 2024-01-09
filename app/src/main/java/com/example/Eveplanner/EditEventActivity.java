package com.example.Eveplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

/**
 * EditEventActivity allows the user to edit the details of a saved event.
 */
public class EditEventActivity extends AppCompatActivity {

    private EditText eventNameEditText;
    private EditText eventAddressEditText;
    private EditText eventDateEditText;
    private EditText eventTypeEditText;
    private EventDatabaseHelper databaseHelper;
    private DatePickerDialog datePickerDialog;
    private long eventId; // The ID of the event

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        eventNameEditText = findViewById(R.id.event_name);
        eventAddressEditText = findViewById(R.id.event_address);
        eventDateEditText = findViewById(R.id.event_date);
        eventTypeEditText = findViewById(R.id.event_type);

        databaseHelper = new EventDatabaseHelper(this);
        eventId = getIntent().getLongExtra("eventId", -1); // Get the event ID from the intent

        eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button updateButton = findViewById(R.id.update_event);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvent();
            }
        });
    }

    /**
     * Displays a DatePickerDialog to select the event date.
     */
    private void showDatePickerDialog() {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(EditEventActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        eventDateEditText.setText(date);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Updates the event details in the database.
     */
    private void updateEvent() {
        String name = eventNameEditText.getText().toString().trim();
        String address = eventAddressEditText.getText().toString().trim();
        String date = eventDateEditText.getText().toString().trim();
        String type = eventTypeEditText.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || date.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, name);
        values.put(EventDatabaseHelper.COLUMN_ADDRESS, address);
        values.put(EventDatabaseHelper.COLUMN_DATE, date);
        values.put(EventDatabaseHelper.COLUMN_TYPE, type);

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String selection = EventDatabaseHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(eventId)}; // Use the event ID in the WHERE clause

        int updatedRows = db.update(EventDatabaseHelper.TABLE_NAME, values, selection, selectionArgs);

        if (updatedRows > 0) {
            // Event updated successfully
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            // Failed to update event
            Toast.makeText(this, "Failed to update event", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvent(); // Load the event details
    }

    /**
     * Loads the event details from the database and sets them to the EditText fields.
     */
    public void loadEvent() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {
                EventDatabaseHelper.COLUMN_NAME,
                EventDatabaseHelper.COLUMN_ADDRESS,
                EventDatabaseHelper.COLUMN_DATE,
                EventDatabaseHelper.COLUMN_TYPE
        };

        String selection = EventDatabaseHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(eventId)};

        Cursor cursor = db.query(
                EventDatabaseHelper.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_NAME));
            String address = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_ADDRESS));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_DATE));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(EventDatabaseHelper.COLUMN_TYPE));

            eventNameEditText.setText(name);
            eventAddressEditText.setText(address);
            eventDateEditText.setText(date);
            eventTypeEditText.setText(type);
        }

        cursor.close();
    }

    /**
     * Override the onBackPressed method to ensure correct back navigation
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}