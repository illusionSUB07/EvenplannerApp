package com.example.Eveplanner;

import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;

import org.junit.Test;

public class MapsActivityTest {

    /**
     * This test checks if the map is displayed when the activity is launched.
     */
    @Test
    public void testIsMapDisplayed() {
        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);
        Espresso.onView(withId(R.id.map_view)).check(matches(isDisplayed()));
    }

    @Test
    public void mapsActivity_ShouldDisplayAddressesCorrectly() {
        // Set the start and end addresses
        String startAddress = "123 Start St";
        String endAddress = "456 End Ave";

        ActivityScenario<MapsActivity> scenario = ActivityScenario.launch(MapsActivity.class);

        // Enter the start and end addresses
        Espresso.onView(withId(R.id.start_edit_text)).perform(clearText(), typeText(startAddress));
        Espresso.onView(withId(R.id.end_edit_text)).perform(clearText(), typeText(endAddress));

        // Check if the text in these views match the addresses
        Espresso.onView(withId(R.id.start_edit_text)).check(matches(withText(startAddress)));
        Espresso.onView(withId(R.id.end_edit_text)).check(matches(withText(endAddress)));
    }
}
