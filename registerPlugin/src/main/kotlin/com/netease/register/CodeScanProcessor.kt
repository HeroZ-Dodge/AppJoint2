package com.netease.register

import com.android.build.api.transform.JarInput
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.InputStream
import java.util.jar.JarFile

/**
 * 接口扫描器
 *  Created by linzheng on 2019/2/27.
 */

class CodeScanProcessor(val classList: MutableList<String>) {

    var injectClassFile: File? = null    // 保存需要编织代码的目标文件

    /**
     * 扫描jar
     */
    fun scanFromJar(jarInput: JarInput, dest: File) {
        val jarFile = JarFile(jarInput.file)
        val enumeration = jarFile.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry = enumeration.nextElement()
            if (jarEntry.name.endsWith(".class")) {
                val inputStream = jarFile.getInputStream(jarEntry)
                scanInterface(inputStream)
                scanInjectClass(jarEntry.name, dest)
            }
        }
    }

    private fun scanInjectClass(name: String, dest: File) {
        if (name.endsWith("AppJoint.class")) {
            injectClassFile = dest
            println("Dodge_ find Class =  $name")
            println("Dodge_ find AppJoint file = ${dest.absolutePath}")
        }
    }

    /**
     * 扫描目录
     */
    fun scanFromDirectory(directory: File, dest: File) {
        var root = directory.absolutePath
        if (!root.endsWith(File.separator)) {
            root += File.separator
        }
        forEachFile(directory) { file ->
            scanInterface(file.inputStream())
            var path = file.absolutePath.replace(root, "")
            if (File.separator != "/") {
                path = path.replace("\\\\", "/")
            }
            scanInjectClass(path, File(dest.absolutePath + File.separator + path))
        }
    }

    /**
     * 递归遍历文件夹下的所有文件
     */
    private fun forEachFile(file: File, callback: (file: File) -> Unit) {
        file.listFiles().forEach {
            if (it.isDirectory) {
                forEachFile(it, callback)
            } else {
                callback.invoke(it)
            }
        }
    }

    /**
     * 扫描接口
     */
    private fun scanInterface(inputStream: InputStream) {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = ScanClassVisitor(Opcodes.ASM6, classWriter)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
    }


    inner class ScanClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {

        override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
            if (interfaces?.contains("com/dodge/hero/z/library/IAppJointProvider") == true) {
                classList.add(name!!)
            }
            super.visit(version, access, name, signature, superName, interfaces)
        }

    }


}