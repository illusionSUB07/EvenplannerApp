package com.example.Eveplanner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {





     //@Rule public ActivityScenarioRule<MainActivity> activityRule =new ActivityScenarioRule<>(MainActivity.class);
    //@Rule public ActivityScenarioRule<MainActivity2> activityRule =new ActivityScenarioRule<>(MainActivity2.class);
    @Rule
    public ActivityScenarioRule<MapsActivity> activityRule =new ActivityScenarioRule<>(MapsActivity.class);






    private final CountingIdlingResource idlingResource = new CountingIdlingResource("MainActivityResource");

    @Before
    public void registerIdlingResource() {
        IdlingRegistry.getInstance().register(MainActivity.idlingResource);
    }

    @Test
    public void testA_MainActivityViewsDisplayed() {
        onView(withId(R.id.editText)).check(matches(isDisplayed()));
        onView(withId(R.id.button)).check(matches(isDisplayed()));
        onView(withId(R.id.textView)).check(matches(isDisplayed()));
    }

    @Test
    public void testB_MainActivityButton_Click() {
        idlingResource.increment();
        onView(withId(R.id.button)).perform(click());
        idlingResource.decrement();
    }

    @Test
    public void testC_MainActivity2ViewsDisplayed() {
        onView(withId(R.id.textView2)).check(matches(isDisplayed()));
        onView(withId(R.id.button2)).perform(click());
    }

    @Test
    public void testD_MainActivity2Button_Click() {
        onView(withId(R.id.button2)).perform(click());
    }
    @Test
    public void testVisibility_searchButton() {
        Espresso.onView(ViewMatchers.withId(R.id.search_button))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testHint_startEditText() {
        Espresso.onView(ViewMatchers.withId(R.id.start_edit_text))
                .check(ViewAssertions.matches(ViewMatchers.withHint("Start Address")));
    }

    @Test
    public void testInput_startEditText() {
        Espresso.onView(ViewMatchers.withId(R.id.start_edit_text))
                .perform(ViewActions.typeText("Some address"))
                .check(ViewAssertions.matches(ViewMatchers.withText("Some address")));
    }
    @Test
    public void mapsActivity_ShouldDisplayCorrectUI_AfterButtonClick() {
        // Simulate a button click
        onView(withId(R.id.search_button)).perform(click());

        // Check that the correct UI is displayed
        onView(withId(R.id.map_view))
                .check(matches(isDisplayed()));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(idlingResource);
    }
}
