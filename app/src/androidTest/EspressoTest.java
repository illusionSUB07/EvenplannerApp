import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.Eveplanner.MainActivity;
import com.example.Eveplanner.MainActivity2;
import com.example.Eveplanner.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Espresso tests for MainActivity and MainActivity2.
 */
@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRuleMainActivity = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public ActivityScenarioRule<MainActivity2> activityScenarioRuleMainActivity2 = new ActivityScenarioRule<>(MainActivity2.class);

    @Test
    public void testOpenActivity() {
        // Perform click action on the button in MainActivity
        Espresso.onView(ViewMatchers.withId(R.id.button))
                .perform(ViewActions.click());

        // Add assertions to verify the expected behavior
        // For example, you can assert that MainActivity2 is launched

        // Assert that MainActivity2 is displayed
        Espresso.onView(ViewMatchers.withId(R.id.activity_main2_layout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testWelcomeMessage() {
        // Get the expected welcome message
        String expectedMessage = "Welcome Dear User!";

        // Assert that the welcome message is displayed correctly in MainActivity2
        Espresso.onView(ViewMatchers.withId(R.id.textView2))
                .check(ViewAssertions.matches(ViewMatchers.withText(expectedMessage)));
    }

    @Test
    public void testStartButton() {
        // Perform click action on the start button in MainActivity2
        Espresso.onView(ViewMatchers.withId(R.id.button2))
                .perform(ViewActions.click());

        // Add assertions to verify the expected behavior
        // For example, you can assert that a specific activity is launched

        // Assert that the home page activity is displayed
        Espresso.onView(ViewMatchers.withId(R.id.home_page_layout))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
