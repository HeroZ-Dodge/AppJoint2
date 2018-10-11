package com.dodge.hero.z.appjoint.router;

import com.dodge.hero.z.annotation.RouterSpec;

/**
 * Created by linzheng on 2018/10/11.
 */

@RouterSpec
public class AppRouterImpl implements IAppRouter {


    @Override
    public void print() {
        System.out.println("AppRouterImpl print()");

    }
}
