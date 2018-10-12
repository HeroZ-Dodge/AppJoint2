package com.dodge.hero.z.appjoint;

import android.app.Application;

import com.dodge.hero.z.library.AppJoint;

/**
 * Created by linzheng on 2018/10/11.
 */

public class MyApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        AppJoint.init();
        AppJoint.get().onCreate();
    }


}
