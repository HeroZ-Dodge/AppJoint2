package com.netease.register

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *  Created by linzheng on 2019/2/25.
 */

class TestPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        println("test-plugin-app")

        project.task("my-test-task").doLast {

            println("my-test-task do last")

        }
    }
}