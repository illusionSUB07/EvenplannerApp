package com.example.Eveplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * HomePageActivity is the main entry point of the application after the user enters his/her name.
 * This activity provides a menu with options to navigate to different parts of the application.
 */
public class HomePageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        setTitle("EVEPLAN");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.Add) {
            Intent addEventIntent = new Intent(this, AddEventActivity.class);
            startActivity(addEventIntent);
            return true;
        } else if (id == R.id.Reminds) {
            Intent addReminderIntent = new Intent(this, AddReminderActivity.class);
            startActivity(addReminderIntent);
            return true;
        }
        else if (id == R.id.events) {
            Intent eventsInUSIntent = new Intent(this, EventsInUSActivity.class);
            startActivity(eventsInUSIntent);
            return true;
        } else if (id == R.id.maps) {
            Intent openMapsIntent = new Intent(this, MapsActivity.class);
            startActivity(openMapsIntent);
            return true;
        } else if (id == R.id.list) {
            Intent savedEventsIntent = new Intent(this, SavedEventsActivity.class);
            startActivity(savedEventsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}