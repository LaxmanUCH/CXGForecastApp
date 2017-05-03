package com.cxg.internal;

import android.app.Application;
import android.content.Context;

/**
 * Created by CXG Groups Pvt Ltd., Singapore on 4/29/17.
 */

public final class CXGContext extends Application {

    private static Context cxgContext;

    @Override
    public void onCreate() {
        super.onCreate();
        cxgContext= getApplicationContext();
    }


    public static Context getCXGContext() {
        return cxgContext;
    }
}
