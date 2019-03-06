package com.dodge.hero.z.appjoint;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.dodge.hero.z.annotation.RouterSpec;
import com.dodge.hero.z.module1.router.IAppRouter;

/**
 * Created by linzheng on 2018/10/11.
 */

@RouterSpec("app")
public class AppRouterImpl implements IAppRouter {

    public static final String TAG = AppRouterImpl.class.getSimpleName();

    @Override
    public void print() {
        Log.d(TAG, "print: here is " + TAG);
    }

    @Override
    public void method1(Context context) {
        Toast.makeText(context, "App method", Toast.LENGTH_SHORT).show();
    }

}
