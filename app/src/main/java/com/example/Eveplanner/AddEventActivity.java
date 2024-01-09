package com.example.Eveplanner;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

/**
 * AddEventActivity provides a UI for the user to add a new event. When the user clicks on the save button,
 * the event details are saved in the SQLite database.
 */
public class AddEventActivity extends AppCompatActivity {

    public EditText eventNameEditText;
    public EditText eventAddressEditText;
    public EditText eventDateEditText;
    public EditText eventTypeEditText;
    public DatePickerDialog datePickerDialog;
    public EventDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        eventNameEditText = findViewById(R.id.event_name);
        eventAddressEditText = findViewById(R.id.event_address);
        eventDateEditText = findViewById(R.id.event_date);
        eventTypeEditText = findViewById(R.id.event_type);

        databaseHelper = new EventDatabaseHelper(this);

        eventDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        Button saveButton = findViewById(R.id.save_event);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvent();
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

        datePickerDialog = new DatePickerDialog(AddEventActivity.this,
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
     * Saves the event details to the database.
     */
    public void saveEvent() {
        String name = eventNameEditText.getText().toString().trim();
        String address = eventAddressEditText.getText().toString().trim();
        String date = eventDateEditText.getText().toString().trim();
        String type = eventTypeEditText.getText().toString().trim();

        if (name.isEmpty() || address.isEmpty() || date.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if the event already exists
        if (databaseHelper.eventExists(name)) {
            Toast.makeText(this, "Event already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(EventDatabaseHelper.COLUMN_NAME, name);
        values.put(EventDatabaseHelper.COLUMN_ADDRESS, address);
        values.put(EventDatabaseHelper.COLUMN_DATE, date);
        values.put(EventDatabaseHelper.COLUMN_TYPE, type);

        long result = databaseHelper.insertEvent(values);
        if (result != -1) {
            Toast.makeText(this, "Event saved", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
        }
    }
}