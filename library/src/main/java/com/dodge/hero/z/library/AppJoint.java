package com.dodge.hero.z.library;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppJoint {

    public static final String TAG = "AppJoint";

    private List<Application> moduleApplications = new ArrayList<>();

    private Set<Class> moduleSet = new HashSet<>();

    private Map<Class, Class> routersMap = new HashMap<>();

    private Map<Class, Object> routerInstanceMap = new HashMap<>();

    private AppJoint() {
    }

    public void attachBaseContext(Context context) {
        for (Application app : moduleApplications) {
            try { // invoke each application's attachBaseContext
                Method attachBaseContext = ContextWrapper.class.getDeclaredMethod("attachBaseContext", Context.class);
                attachBaseContext.setAccessible(true);
                attachBaseContext.invoke(app, context);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void onCreate() {
        for (Application app : moduleApplications) {
            app.onCreate();
        }
    }

    public void onConfigurationChanged(Configuration configuration) {
        for (Application app : moduleApplications) {
            app.onConfigurationChanged(configuration);
        }
    }

    public void onLowMemory() {
        for (Application app : moduleApplications) {
            app.onLowMemory();
        }
    }

    public void onTerminate() {
        for (Application app : moduleApplications) {
            app.onTerminate();
        }

    }

    public void onTrimMemory(int level) {
        for (Application app : moduleApplications) {
            app.onTrimMemory(level);
        }
    }

    public List<Application> moduleApplications() {
        return moduleApplications;
    }

    public Map<Class, Class> routersMap() {
        return routersMap;
    }

    public static AppJoint get() {
        return SingletonHolder.INSTANCE;
    }

    public static void initDodgeJoin() {
        Log.d(TAG, "initDodgeJoin: start to register Router and Module");
        // Auto register by plugin
        // look like this
//        register(new AppJointProvider$app());
//        register(new AppJointProvider$module1());
    }


    public static void register(IAppJointProvider scanInterface) {
        get().routersMap.putAll(scanInterface.getRouterMap());
        get().moduleSet.addAll(scanInterface.getModuleSet());
    }

    public static synchronized <T> T router(Class<T> routerType) {
        T requiredRouter = null;
        if (!get().routerInstanceMap.containsKey(routerType)) {
            try {
                requiredRouter = (T) get().routersMap.get(routerType).newInstance();
                get().routerInstanceMap.put(routerType, requiredRouter);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        } else {
            requiredRouter = (T) get().routerInstanceMap.get(routerType);
        }
        return requiredRouter;
    }

    static class SingletonHolder {
        static AppJoint INSTANCE = new AppJoint();
    }


}