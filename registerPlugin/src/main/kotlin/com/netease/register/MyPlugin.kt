package com.netease.register

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  Created by linzheng on 2019/2/25.
 */

class MyPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        if (project.plugins.hasPlugin(AppPlugin::class.java)) {
            val android = project.extensions.getByType(AppExtension::class.java)
            val transform = RegisterTransform(project)
            android.registerTransform(transform)
        }


    }
}