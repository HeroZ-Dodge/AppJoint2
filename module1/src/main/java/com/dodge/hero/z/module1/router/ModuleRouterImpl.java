package com.dodge.hero.z.module1.router;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.dodge.hero.module1.ui.MainActivity;
import com.dodge.hero.z.annotation.RouterSpec;

/**
 * Created by linzheng on 2018/10/11.
 */
@RouterSpec("module1")
public class ModuleRouterImpl implements IModuleRouter {

    public static final String TAG = ModuleRouterImpl.class.getSimpleName();

    @Override
    public void print() {
        Log.d(TAG, "print: here is " + TAG);
    }

    @Override
    public void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void method1(Context context) {
        Toast.makeText(context, "模块1的方法调用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void method2(Context context) {
        Toast.makeText(context, "模块2的方法调用", Toast.LENGTH_SHORT).show();

    }

}
