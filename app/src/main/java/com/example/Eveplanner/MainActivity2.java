package com.example.Eveplanner;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * The second activity that displays a welcome message.
 */
public class MainActivity2 extends AppCompatActivity {

    TextView textView;
    Button startButton;  // Add this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Get the TextView from the layout
        textView = findViewById(R.id.textView2);

        // Get the Button from the layout
        startButton = findViewById(R.id.button2);  // Add this

        // Retrieve the name passed from the previous activity
        Intent intent = getIntent();
        String name = intent.getStringExtra(MainActivity.EXTRA_NAME);

        // Set the welcome message with the retrieved name
        textView.setText("Welcome Dear " + name + "!");

        // Add a click listener to the start button
        startButton.setOnClickListener(new View.OnClickListener() {  // Add this
            @Override
            public void onClick(View v) {
                Intent homePageIntent = new Intent(MainActivity2.this, HomePageActivity.class);
                startActivity(homePageIntent);
            }
        });  // Add this
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
