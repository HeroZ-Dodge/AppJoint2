package com.netease.register

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *  Created by linzheng on 2019/2/26.
 */

class MyClassVisitor(api: Int, cv: ClassVisitor?) : ClassVisitor(api, cv) {


    override fun visit(version: Int, access: Int, name: String?, signature: String?, superName: String?, interfaces: Array<out String>?) {
        super.visit(version, access, name, signature, superName, interfaces)
//        println("dodge_ | name = $name, sign = $signature")
    }

    override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val vm = super.visitMethod(access, name, desc, signature, exceptions)
        if (name.equals("initDodgeJoin")) {
            println("Dodge get initDodgeJoin()")
            return MyMethodVisitor(Opcodes.ASM6, vm, access, name!!, desc!!)
        }
        return vm
    }


}