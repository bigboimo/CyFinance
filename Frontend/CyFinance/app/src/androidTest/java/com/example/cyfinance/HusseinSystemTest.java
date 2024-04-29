package com.example.cyfinance;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class HusseinSystemTest {

    @Rule
    public ActivityScenarioRule<EarningsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(EarningsActivity.class);

    @Test
    public void testPrimaryIncomeInputAcceptance() throws InterruptedException {
        onView(withId(R.id.primary)).perform(replaceText("5000"), closeSoftKeyboard());
        Thread.sleep(500); // Wait for any asynchronous updates (if any)
        onView(withId(R.id.primary)).check(matches(withText("5000")));
    }

    @Test
    public void testSecondaryIncomeInputAcceptance() throws InterruptedException {
        onView(withId(R.id.secondary)).perform(replaceText("2500"), closeSoftKeyboard());
        Thread.sleep(500); // Wait for any asynchronous updates (if any)
        onView(withId(R.id.secondary)).check(matches(withText("2500")));
    }

    @Test
    public void testSubmitButtonAction() throws InterruptedException {
        onView(withId(R.id.primary)).perform(replaceText("5000"), closeSoftKeyboard());
        onView(withId(R.id.secondary)).perform(replaceText("2500"), closeSoftKeyboard());
        onView(withId(R.id.submit)).perform(click());
        Thread.sleep(500); // Simulate waiting for response from the server
        // Add assertions here to check for the next activity or UI updates
    }

    @Test
    public void testFieldValidations() throws InterruptedException {
        onView(withId(R.id.submit)).perform(click());
        Thread.sleep(500); // Give time for the UI to show errors
        // Checks for error indications; ensure errors are displayed in the UI
        // onView(withId(R.id.primary)).check(matches(hasErrorText("Field required")));
        // onView(withId(R.id.secondary)).check(matches(hasErrorText("Field required")));
    }
}
