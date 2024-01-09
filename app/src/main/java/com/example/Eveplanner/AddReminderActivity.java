package com.example.Eveplanner;

        import android.app.AlarmManager;
        import android.app.DatePickerDialog;
        import android.app.PendingIntent;
        import android.app.TimePickerDialog;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;
        import android.widget.TimePicker;
        import android.widget.Toast;

        import androidx.appcompat.app.AppCompatActivity;

        import java.text.SimpleDateFormat;
        import java.util.Calendar;
        import java.util.Locale;

/**
 * This activity enables users to add a reminder with a selected date and time.
 */
public class AddReminderActivity extends AppCompatActivity {

    private EditText eventDateTimeText;
    private Button addButton;
    private EditText reminderText;
    private Calendar selectedDateTime;
    private static final String CHANNEL_ID = "REMINDER_CHANNEL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        eventDateTimeText = findViewById(R.id.edt_reminder_date_time);
        addButton = findViewById(R.id.btn_submit_reminder);
        reminderText = findViewById(R.id.reminder_text);
        selectedDateTime = Calendar.getInstance();

        eventDateTimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReminder();
            }
        });
    }

    /**
     * Displays the DatePickerDialog to select a date.
     */
    private void showDateTimePicker() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int dayOfMonth = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddReminderActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDateTime.set(Calendar.YEAR, year);
                        selectedDateTime.set(Calendar.MONTH, month);
                        selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimePicker();
                    }
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    /**
     * Displays the TimePickerDialog to select a time.
     */
    private void showTimePicker() {
        int hourOfDay = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddReminderActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedDateTime.set(Calendar.MINUTE, minute);
                        updateDateTimeText();
                    }
                }, hourOfDay, minute, false);
        timePickerDialog.show();
    }

    /**
     * Updates the eventDateTimeText EditText with the selected date and time.
     */
    private void updateDateTimeText() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
        String dateTime = format.format(selectedDateTime.getTime());
        eventDateTimeText.setText(dateTime);
    }

    /**
     * Adds a reminder based on the input values provided by the user.
     */
    private void addReminder() {
        String title = eventDateTimeText.getText().toString().trim();
        String text = reminderText.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter the event title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (text.isEmpty()) {
            Toast.makeText(this, "Please enter the reminder text", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert selectedDateTime to milliseconds
        long alarmTimeMillis = selectedDateTime.getTimeInMillis();

        // Create an explicit intent for the broadcast receiver
        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("reminderText", text);

        // Create a pending intent to be triggered when the alarm fires
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Get the AlarmManager service
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        // Set the alarm
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis,
                        pendingIntent
                );
            } else {
                alarmManager.setExact(
                        AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis,
                        pendingIntent
                );
            }
        }

        // Show a toast message indicating that the reminder has been added
        Toast.makeText(this, "Reminder added: " + title + " - " + text, Toast.LENGTH_SHORT).show();

        // Log the reminder details
        Log.d("AddReminderActivity", "Reminder added: " + title + " - " + text);

        // Finish the activity and return to the previous screen
        finish();
    }
}