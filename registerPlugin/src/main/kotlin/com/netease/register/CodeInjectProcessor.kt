package com.netease.register

import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * 代码注入
 *  Created by linzheng on 2019/2/27.
 */

class CodeInjectProcessor(private val classList: List<String>) {


    fun injectCode(file: File) {
        println("Dodge_inject code file path = ${file.absolutePath}")
        if (file.absolutePath.endsWith(".jar")) {
            insertCodeToJar(file)
        } else {
            insertCodeToClass(file)
        }
    }

    private fun insertCodeToJar(file: File) {
        println("Dodge insertCodeToJar")
        val tempFile = File(file.parent, file.name + ".temp")
        if (tempFile.exists()) {
            tempFile.delete()
        }
        val jarOutputStream = JarOutputStream(FileOutputStream(tempFile))

        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val inputStream = jarFile.getInputStream(jarEntry)
            val zipEntry = ZipEntry(jarEntry.name)
            jarOutputStream.putNextEntry(zipEntry)
            if (jarEntry.name.endsWith("AppJoint.class")) {
                println("Dodge start generate code in to AppJoint.class")
                val code = doGenerateCode(inputStream)
                jarOutputStream.write(code)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }

        jarOutputStream.close()
        jarFile.close()

        if (file.exists()) {
            file.delete()
        }
        tempFile.renameTo(file)
    }

    private fun insertCodeToClass(file: File) {
        println("Dodge insertCodeToClass")
        val tempFile = File(file.parent, file.name + ".temp")
        val outputStream = FileOutputStream(tempFile)
        val inputStream = file.inputStream()
        val code = doGenerateCode(inputStream)
        outputStream.write(code)
        inputStream.close()
        outputStream.close()
        if (file.exists()) {
            file.delete()
        }
        tempFile.renameTo(file)
    }


    private fun doGenerateCode(inputStream: InputStream): ByteArray {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
        val classVisitor = MyClassVisitor(Opcodes.ASM6, classWriter, classList)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }


}