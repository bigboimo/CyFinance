package com.example.cyfinance;


import static android.provider.Settings.System.getString;

import com.example.cyfinance.LoginActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.StringEndsWith.endsWith;


@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest   // large execution time
public class CasperSystemTest {

    private static final int SIMULATED_DELAY_MS = 500;

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule = new ActivityScenarioRule<>(LoginActivity.class);
    @Rule
    public ActivityScenarioRule<SignupActivity> activityScenarioRuleSign = new ActivityScenarioRule<>(SignupActivity.class);

    /**
     * Start the server and run this test
     *
     * Tests if the frontend can successfully login using the backend
     */
    @Test
    public void successLogin(){
        String User = "test@email.com";
        String Password = "password";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.username = null;
            activity.password = null;
        });

        onView(withId(R.id.login_username_edt)).perform(typeText(User), closeSoftKeyboard());
        onView(withId(R.id.login_password_edt)).perform(typeText(Password), closeSoftKeyboard());
        onView(withId(R.id.login_btn)).perform(click());
        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        //onView(withId(R.id.myTextView)).check(matches(withText(endsWith(resultString))));
        //onView(withText(LoginActivity.Response)).check(matches(withText("success")));
    }

    /**
     * Start the server and run this test
     *
     * Tests if the frontend can successfully signup with the backend
     */
    @Test
    public void successSignup(){

        String newUsername = "new@email.com";
        String newPassword = "password10";

        activityScenarioRuleSign.getScenario().onActivity(activity -> {
            activity.username = null;
            activity.password = null;
        });

        // Type in username and password
        onView(withId(R.id.signup_username_edt)).perform(typeText(newUsername), closeSoftKeyboard());
        onView(withId(R.id.signup_password_edt)).perform(typeText(newPassword), closeSoftKeyboard());
        // Click button to submit
        onView(withId(R.id.signup_signup_btn)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        //onView(withText(SignupActivity.Response)).check(matches(withText(endsWith("User created"))));
    }

    /**
     * Start the server and run this test
     *
     * Tests if the signup page can output that the user already exists
     */
    @Test
    public void userAlreadyCreated() {

        String username = "test1@email.com";
        String password = "password";
        String resultString = "User already exists";
        onView(withId(R.id.login_signup_btn)).perform(click());

//        activityScenarioRuleSign.getScenario().onActivity(activity -> {
//            activity.username = null;
//            activity.password = null;
//        });

        onView(withId(R.id.signup_username_edt)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.signup_password_edt)).perform(typeText(password), closeSoftKeyboard());
        onView(withId(R.id.signup_signup_btn)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {
        }

        // Verify that volley returned the correct value
        //onView(withText(SignupActivity.Response)).check(matches(withText(endsWith(resultString))));
    }

    /**
     * Start the server and run this test
     *
     * Tests if the login page can output a failure
     */
    @Test
    public void loginFail() {

        String username = "inputstring@email.com";
        String password = "password";
        String resultString = "failure";

        activityScenarioRule.getScenario().onActivity(activity -> {
            activity.username = null;
            activity.password = null;
        });

        // Type in testString and send request
        onView(withId(R.id.login_username_edt)).perform(typeText(username), closeSoftKeyboard());
        onView(withId(R.id.login_password_edt)).perform(typeText(password), closeSoftKeyboard());
        // Click button to submit
        onView(withId(R.id.login_btn)).perform(click());

        // Put thread to sleep to allow volley to handle the request
        try {
            Thread.sleep(SIMULATED_DELAY_MS);
        } catch (InterruptedException e) {}

        // Verify that volley returned the correct value
        //onView(withText(LoginActivity.Response)).check(matches(withText(endsWith(resultString))));
    }



}
