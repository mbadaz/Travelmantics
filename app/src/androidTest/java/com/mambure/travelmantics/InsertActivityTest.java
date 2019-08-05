package com.mambure.travelmantics;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeTextIntoFocusedView;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class InsertActivityTest {

    @Rule
    public ActivityTestRule<DealActivity> mActivityTestRule = new ActivityTestRule<>(DealActivity.class);

    @Test
    public void saveDealUIFlowTest() {
        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.txtTitle)).
                perform(click()).perform(typeTextIntoFocusedView("title"));
        onView(withId(R.id.txtDescription)).
                perform(click()).perform(typeTextIntoFocusedView("description"));
        onView(withId(R.id.txtPrice)).
                perform(click()).perform(typeTextIntoFocusedView("price"));

        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.menuItemSave), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());
        onView(withText(R.string.saved_toast)).
                inRoot(RootMatchers.withDecorView(not(is(mActivityTestRule.getActivity().getWindow().getDecorView()))));

        ViewAssertion isEditTextEmptyAssertion = new ViewAssertion() {
            @Override
            public void check(View view, NoMatchingViewException noViewFoundException) {
                if (noViewFoundException == null) {
                    Assert.assertEquals("", ((EditText)view).getText().toString());
                }
            }
        };

        onView(withId(R.id.txtTitle)).check(isEditTextEmptyAssertion);
        onView(withId(R.id.txtDescription)).check(isEditTextEmptyAssertion);
        onView(withId(R.id.txtPrice)).check(isEditTextEmptyAssertion);
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
