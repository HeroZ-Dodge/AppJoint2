package com.netease.register

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by linzheng on 2019/2/26.
 */

open class MyMethodVisitor(api: Int, mv: MethodVisitor, access: Int, name: String, desc: String, private val classList: List<String>?) : AdviceAdapter(api, mv, access, name, desc) {


    override fun onMethodEnter() {
        classList?.forEach {
            register(it)
        }
    }


    private fun register(className: String) {
        mv.visitTypeInsn(Opcodes.NEW, className)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className, "<init>", "()V", false)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dodge/hero/z/library/AppJoint", "register", "(Lcom/dodge/hero/z/library/IAppJointProvider;)V", false)
    }


}
