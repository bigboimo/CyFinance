package com.example.cyfinance.ui.Earnings;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.DrawerMatchers.isClosed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;
import android.view.Gravity;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;


@RunWith(AndroidJUnit4.class)
public class HusseinSystemTest {

    @Rule
    public ActivityScenarioRule<EarningsActivity> activityScenarioRule =
            new ActivityScenarioRule<>(EarningsActivity.class);

    @Test
    public void testEarningsInput() {
        // Assume that you have already set up MockWebServer or similar to mock network responses
        // For the primary monthly income
        onView(withId(R.id.primary)).perform(replaceText("5000"));
        // For the secondary monthly income
        onView(withId(R.id.secondary)).perform(replaceText("1500"));

        // Now click the submit button
        onView(withId(R.id.submit)).perform(click());

    }

    // Add more tests for different inputs, error responses, etc.
}
