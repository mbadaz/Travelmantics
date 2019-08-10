package com.mambure.travelmantics;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.contrib.RecyclerViewActions;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@RunWith(AndroidJUnit4ClassRunner.class)
@LargeTest
public class SaveDealExecutionFlowTest {




    private static final String TITLE_INPUT_TEXT = "Deal title";
    private static final String PRICE_INPUT_TEXT = "Deal price";
    private static final String DESCRIPTION_INPUT_TEXT = "Deal description";
    private String title;
    private String description;
    private String price;
    private String addMenuItemLabel;
    private String saveDealMenuItemLabel;
    private ViewInteraction addNewDealViewInteraction;
    private ViewInteraction saveDealViewInteraction;
    private DealAdapter mDealAdapter;
    private Context context;
    static ListActivity mListActivity;


    @Before
    public void initialize() {

        ActivityScenario<ListActivity> mListActivityActivityScenario = ActivityScenario.launch(ListActivity.class);

        mListActivityActivityScenario.onActivity(new ActivityScenario.ActivityAction<ListActivity>() {
            @Override
            public void perform(ListActivity activity) {
                mListActivity = activity;
                context = activity.getApplicationContext();
                mDealAdapter = activity.getDealAdapter();
            }
        });
        addMenuItemLabel = context.getResources().getString(R.id.menu_addNewDeal);

        saveDealMenuItemLabel = context.getResources().getString(R.id.menuItemSave);

        IdlingRegistry.getInstance().register(IdlingResourceUtil.get());

        Random random = new Random();
            title = Long.toString(random.nextInt(100000000));
        price = Long.toString(random.nextInt(5000));
        description = Long.toString(random.nextInt(1000000000));
    }

    @Test
    public void saveDealTest() {

        addNewDealViewInteraction = onView(
                allOf(withId(R.id.menu_addNewDeal), withText("Add new deal"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));

        addNewDealViewInteraction.perform(click());

        saveDealViewInteraction = onView(
                allOf(withId(R.id.menuItemSave), withText("Save"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.action_bar),
                                        1),
                                0),
                        isDisplayed()));

        onView(withId(R.id.txtTitle)).perform(typeText(title));
        onView(withId(R.id.txtPrice)).perform(typeText(price));
        onView(withId(R.id.txtDescription)).perform(typeText(description));

        saveDealViewInteraction.perform(click());

        onView(withId(R.id.rvDeals)).
                perform(RecyclerViewActions.scrollToPosition(mDealAdapter.getItemCount()-1));

        onView(withText(title)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(price)).check(ViewAssertions.matches(isDisplayed()));
        onView(withText(description)).check(ViewAssertions.matches(isDisplayed()));


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

    @After
    public void cleanUp() {
        IdlingRegistry.getInstance().unregister(IdlingResourceUtil.get());
    }

}
