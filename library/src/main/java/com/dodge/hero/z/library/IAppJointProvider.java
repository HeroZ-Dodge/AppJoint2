package com.dodge.hero.z.library;

import java.util.Map;
import java.util.Set;

/**
 * Created by linzheng on 2018/10/11.
 */

public interface IAppJointProvider {

    Map<Class, Class> getRouterMap();

    Set<Class> getModuleSet();

}
