package com.example.Eveplanner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * SavedEventsActivity displays the list of saved events and provides options for editing, deleting,
 * and adding reminders to events.
 */
public class SavedEventsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> eventList;
    private ArrayList<Long> eventIdList;
    private EventDatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_events);

        listView = findViewById(R.id.listView);
        eventList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventList);
        listView.setAdapter(adapter);

        databaseHelper = new EventDatabaseHelper(this);

        loadSavedEvents();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showOptionsDialog(position);
            }
        });
    }

    /**
     * Loads the saved events from the database and displays them in the ListView.
     */
    private void loadSavedEvents() {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {
                EventDatabaseHelper.COLUMN_ID,
                EventDatabaseHelper.COLUMN_NAME,
                EventDatabaseHelper.COLUMN_ADDRESS,
                EventDatabaseHelper.COLUMN_DATE,
                EventDatabaseHelper.COLUMN_TYPE,
                EventDatabaseHelper.COLUMN_REMINDER
        };

        Cursor cursor = db.query(
                EventDatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        eventList.clear();
        eventIdList.clear();

        int idColumnIndex = cursor.getColumnIndex(EventDatabaseHelper.COLUMN_ID);
        int nameColumnIndex = cursor.getColumnIndex(EventDatabaseHelper.COLUMN_NAME);
        int addressColumnIndex = cursor.getColumnIndex(EventDatabaseHelper.COLUMN_ADDRESS);
        int dateColumnIndex = cursor.getColumnIndex(EventDatabaseHelper.COLUMN_DATE);
        int typeColumnIndex = cursor.getColumnIndex(EventDatabaseHelper.COLUMN_TYPE);


        while (cursor.moveToNext()) {
            long eventId = cursor.getLong(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String address = cursor.getString(addressColumnIndex);
            String date = cursor.getString(dateColumnIndex);
            String type = cursor.getString(typeColumnIndex);


            String eventInfo = "Name: " + name + "\n"
                    + "Address: " + address + "\n"
                    + "Date: " + date + "\n"
                    + "Type: " + type;

            eventList.add(eventInfo);
            eventIdList.add(eventId);

        }

        cursor.close();
        adapter.notifyDataSetChanged();
    }

    /**
     * Shows a dialog with options for the selected event.
     *
     * @param position The position of the selected event in the ListView.
     */
    private void showOptionsDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Options")
                .setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                editEvent(position);
                                break;
                            case 1:
                                deleteEvent(position);
                                break;
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Starts the EditEventActivity to edit the selected event.
     *
     * @param position The position of the selected event in the ListView.
     */
    private void editEvent(int position) {
        long eventId = eventIdList.get(position);
        Intent intent = new Intent(SavedEventsActivity.this, EditEventActivity.class);
        intent.putExtra("eventId", eventId);
        startActivity(intent);
    }

    /**
     * Deletes the selected event from the database and updates the ListView.
     *
     * @param position The position of the selected event in the ListView.
     */
    private void deleteEvent(int position) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String selection = EventDatabaseHelper.COLUMN_ID + "=?";
        String[] selectionArgs = {String.valueOf(eventIdList.get(position))};

        db.delete(EventDatabaseHelper.TABLE_NAME, selection, selectionArgs);
        eventList.remove(position);
        eventIdList.remove(position);
        adapter.notifyDataSetChanged();

        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onResume() {
        super.onResume();
        loadSavedEvents();
    }
}
