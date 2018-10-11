package com.dodge.hero.z.appjoint.router;

import android.util.Log;

import com.dodge.hero.z.annotation.RouterSpec;

/**
 * Created by linzheng on 2018/10/11.
 */
@RouterSpec
public class ModuleRouterImpl implements IModuleRouter {

    public static final String TAG = ModuleRouterImpl.class.getSimpleName();

    @Override
    public void print() {

        Log.d(TAG, "print: ");
    }
}
