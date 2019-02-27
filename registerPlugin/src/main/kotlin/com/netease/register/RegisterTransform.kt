package com.netease.register

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


/**
 *  Created by linzheng on 2019/2/25.
 */
class RegisterTransform : Transform() {


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
        super.transform(transformInvocation)
        println("dodge_start transform")
        transformInvocation?.inputs?.forEach { transformInput ->
            transformInput.directoryInputs.forEach { input ->
                val dest = transformInvocation.outputProvider.getContentLocation(input.name, input.contentTypes, input.scopes, Format.DIRECTORY)
                //遍历目录下的每个文件
                scanFileDirectory(input.file)
                // 处理完后拷到目标文件
                FileUtils.copyDirectory(input.file, dest)
            }
            transformInput.jarInputs.forEach { jarInput ->

                if (jarInput.file.absolutePath.endsWith(".jar")) {
                    // ...对jar进行插入字节码
                    val tempFile = File(jarInput.file.parent + File.separator + "dodge_temp.jar")

                    if (tempFile.exists()) {
                        tempFile.delete()
                    }
                    val fos = FileOutputStream(tempFile)
                    val jarOutputStream = JarOutputStream(fos)

                    val jarFile = JarFile(jarInput.file)
                    val enumeration = jarFile.entries()
                    while (enumeration.hasMoreElements()) {
                        val jarEntry = enumeration.nextElement()
                        val entryName = jarEntry.name
                        val zipEntry = ZipEntry(entryName)
//                        println "==== jarInput class entryName :" + entryName
                        if (entryName.endsWith(".class")) {
                            jarOutputStream.putNextEntry(zipEntry)
                            val inputStream = jarFile.getInputStream(jarEntry)
                            val classReader = ClassReader(IOUtils.toByteArray(inputStream))
                            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                            val cv = MyClassVisitor(Opcodes.ASM5, classWriter)
                            classReader.accept(cv, ClassReader.EXPAND_FRAMES)

                            val bytes = classWriter.toByteArray()
                            jarOutputStream.write(bytes)
                            inputStream.close()
                        }
                    }

                    //结束
                    jarOutputStream.closeEntry()
                    jarOutputStream.close()
                    fos.close()
                    jarFile.close()
                }
                var jarName = jarInput.name
                val md5Name = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length - 4)
                }
                val dest = transformInvocation.outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        println("dodge_end transform")
    }


    private fun scanJar() {


    }


    private fun scanFileDirectory(file: File) {
        file.listFiles()?.forEach {
            if (it.isFile) {
                val path = it.name
                if (path.endsWith(".class") && !name.startsWith("R\$") && "R.class" != path && "BuildConfig.class" != path) {
                    insertCode(it)
                }
            } else {
                scanFileDirectory(it)
            }
        }
    }


    private fun insertCode(file: File) {
        val inputStream = FileInputStream(file)
        val classReader = ClassReader(FileInputStream(file))
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = MyClassVisitor(Opcodes.ASM6, classWriter)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        val code = classWriter.toByteArray()

        val outputStream = FileOutputStream(file)
        outputStream.write(code)

        inputStream.close()
        outputStream.close()
    }


}