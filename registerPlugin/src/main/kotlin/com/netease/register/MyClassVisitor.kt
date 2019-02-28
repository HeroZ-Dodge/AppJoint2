package com.netease.register

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 *  Created by linzheng on 2019/2/26.
 */

class MyClassVisitor(api: Int, cv: ClassVisitor?, private val classList: List<String>) : ClassVisitor(api, cv) {


    override fun visitMethod(access: Int, name: String?, desc: String?, signature: String?, exceptions: Array<out String>?): MethodVisitor {
        val vm = super.visitMethod(access, name, desc, signature, exceptions)
        if (name.equals("initDodgeJoin")) {
            println("Dodge get initDodgeJoin()")
            return MyMethodVisitor(Opcodes.ASM6, vm, access, name!!, desc!!, classList)
        }
        return vm
    }


}