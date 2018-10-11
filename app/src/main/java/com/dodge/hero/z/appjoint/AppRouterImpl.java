package com.dodge.hero.z.appjoint;

import android.util.Log;

import com.dodge.hero.z.annotation.RouterSpec;
import com.dodge.hero.z.module1.router.IAppRouter;

/**
 * Created by linzheng on 2018/10/11.
 */

@RouterSpec
public class AppRouterImpl implements IAppRouter {

    public static final String TAG = AppRouterImpl.class.getSimpleName();

    @Override
    public void print() {
        Log.d(TAG, "print: here is " + TAG);
    }

}
