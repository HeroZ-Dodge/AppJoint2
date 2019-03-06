package com.netease.register

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.gradle.api.Project
import java.io.File


/**
 *  Created by linzheng on 2019/2/25.
 */
class RegisterTransform(val project: Project) : Transform() {


    override fun getName(): String {
        return "RegisterTransform"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_JARS
    }

    override fun isIncremental(): Boolean {
        return true
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {

        project.logger.warn("Dodge_start transform")
        val codeScanProcessor = CodeScanProcessor(arrayListOf())
        transformInvocation?.inputs?.forEach { transformInput ->

            // 遍历class
            transformInput.directoryInputs.forEach { input ->
                val dest = transformInvocation.outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
                if (input.file.isDirectory) {
                    codeScanProcessor.scanFromDirectory(input.file, dest)
                }
                FileUtils.copyDirectory(input.file, dest)
            }

            // 遍历jar
            transformInput.jarInputs.forEach { input ->
                val dest = getDestFile(input, transformInvocation.outputProvider)
                if (input.file.absolutePath.endsWith(".jar")) {
                    codeScanProcessor.scanFromJar(input, dest)
                    project.logger.warn("Dodge- jar name = ${input.name}")
                }
                FileUtils.copyFile(input.file, dest)
            }
        }

        project.logger.warn("interface size = ${codeScanProcessor.classList.size}")
        project.logger.warn(codeScanProcessor.classList.toString())
        codeScanProcessor.injectClassFile?.let {
            CodeInjectProcessor(codeScanProcessor.classList).apply {
                injectCode(it)
            }
        }
        project.logger.warn("dodge_end transform")
    }


    fun getDestFile(jarInput: JarInput, outputProvider: TransformOutputProvider): File {
        var jarName = jarInput.name
        if (jarName.endsWith(".jar")) {
            jarName = jarName.substring(0, jarName.length - 4)
        }
        jarName += DigestUtils.md5Hex(jarInput.file.absolutePath)
        return outputProvider.getContentLocation(jarName, jarInput.contentTypes, jarInput.scopes, Format.JAR)


    }


}