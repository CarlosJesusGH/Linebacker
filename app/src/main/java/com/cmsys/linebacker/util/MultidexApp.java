package com.cmsys.linebacker.util;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
//import android.support.multidex.MultiDex;

/**
 * Created by CarlosJesusGH on 02/01/16.
 */
public class MultidexApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
