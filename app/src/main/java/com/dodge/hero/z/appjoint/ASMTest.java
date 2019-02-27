package com.dodge.hero.z.appjoint;

import com.dodge.hero.z.library.AppJoint;
import com.dodge.hero.z.processor.AppJointProvider$app;

/**
 * Created by linzheng on 2019/2/26.
 */

public class ASMTest {

    public static void init() {


        AppJoint.register(new AppJointProvider$app());
        AppJoint.register(new AppJointProvider$app());

    }



}
