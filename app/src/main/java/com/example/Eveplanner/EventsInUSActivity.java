package com.example.Eveplanner;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This activity fetches events data from a RESTful API and displays it.
 * The user initiates this process by clicking a button.
 * After the button is clicked, it becomes invisible.
 */
public class EventsInUSActivity extends AppCompatActivity {

    String api = "https://app.ticketmaster.com/discovery/v2/events.json?size=12&apikey=yMp5rBce6HKAtWiQGORdWjJTVimeOGxU&locale=*";

    /**
     * Sets up the layout and click listeners when the activity is created.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_in_germany);

        final Button button = findViewById(R.id.button_get_events);
        final TextView eventsText = findViewById(R.id.text_events);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getEvents(eventsText);
                button.setVisibility(View.GONE);  // hides the button
            }
        });
    }

    /**
     * Fetches event data from the API and updates a TextView with the results.
     * If an error occurs, the TextView is updated with an error message.
     *
     * @param eventsText the TextView to update with the results
     */
    private void getEvents(final TextView eventsText) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, api, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray eventsArray = response.getJSONObject("_embedded").getJSONArray("events");
                            StringBuilder eventsInfo = new StringBuilder();
                            String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                            for(int i = 0; i < eventsArray.length(); i++) {
                                JSONObject event = eventsArray.getJSONObject(i);

                                String name = event.getString("name");
                                String date = event.getJSONObject("dates").getJSONObject("start").getString("localDate");
                                String venue = event.getJSONObject("_embedded").getJSONArray("venues").getJSONObject(0).getString("name");

                                if (currentDate.compareTo(date) <= 0) {  // only append the event to the string builder if its date is later than or equal to the current date
                                    eventsInfo.append("Name: ").append(name);
                                    eventsInfo.append("\nDate: ").append(date);
                                    eventsInfo.append("\nVenue: ").append(venue);
                                    eventsInfo.append("\n");
                                    eventsInfo.append("---------------------------------------------------------------------------------------------\n");  // line separator
                                }
                            }

                            eventsText.setText(eventsInfo.toString());
                        } catch (JSONException e) {
                            eventsText.setText("Error parsing JSON.");
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                error.printStackTrace();  // Print the stack trace for debugging purposes
                eventsText.setText("Error fetching events: " + error.getMessage());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
