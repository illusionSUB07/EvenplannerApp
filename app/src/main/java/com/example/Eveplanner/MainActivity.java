package com.example.Eveplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.espresso.idling.CountingIdlingResource;

/**
 * The main activity that displays the welcome screen.
 */
public class MainActivity extends AppCompatActivity {

    EditText name;
    public static final String EXTRA_NAME = "com.example.Eveplanner.extra.NAME";

    // Create a new idling resource
    public static CountingIdlingResource idlingResource = new CountingIdlingResource("MainActivityResource");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Opens the second activity and passes the name entered by the user.
     *
     * @param v The view that was clicked (in this case, the button).
     */
    public void openActivity(View v) {
        Toast.makeText(this, "Information Saved!", Toast.LENGTH_SHORT).show();

        // Create an intent to launch the second activity
        Intent intent = new Intent(this, MainActivity2.class);

        // Get the name entered by the user from the EditText
        name = findViewById(R.id.editText);
        String nameText = name.getText().toString();

        // Pass the name as an extra to the second activity
        intent.putExtra(EXTRA_NAME, nameText);

        // Increment the idling resource before starting the new activity
        idlingResource.increment();

        // Start the second activity
        startActivity(intent);
    }

    // Decrement the idling resource counter in onPostResume
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (!idlingResource.isIdleNow()) {
            idlingResource.decrement();
        }
    }
}
