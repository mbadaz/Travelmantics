package com.mambure.travelmantics;

import androidx.test.espresso.idling.CountingIdlingResource;

public class IdlingResourceUtil {

    CountingIdlingResource mIdlingResource;
    private static IdlingResourceUtil mIdlingResourceUtil;

    private IdlingResourceUtil(){

        mIdlingResource = new CountingIdlingResource("recylerViewLoading");

    }

    public static CountingIdlingResource get() {
        if (mIdlingResourceUtil == null){
            mIdlingResourceUtil = new IdlingResourceUtil();
        }
        return mIdlingResourceUtil.getmIdlingResource();
    }

    private CountingIdlingResource getmIdlingResource() {
        return mIdlingResource;
    }
}
