package com.netease.register

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * Created by linzheng on 2019/2/26.
 */

open class MyMethodVisitor constructor(api: Int, mv: MethodVisitor, access: Int, name: String, desc: String) : AdviceAdapter(api, mv, access, name, desc) {






    override fun onMethodEnter() {
        mv.visitTypeInsn(Opcodes.NEW, "com/dodge/hero/z/processor/AppJointProvider\$app")
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/dodge/hero/z/processor/AppJointProvider\$app", "<init>", "()V", false)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dodge/hero/z/library/AppJoint", "register", "(Lcom/dodge/hero/z/library/IAppJointProvider;)V", false)

        mv.visitTypeInsn(Opcodes.NEW, "com/dodge/hero/z/processor/AppJointProvider\$app")
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/dodge/hero/z/processor/AppJointProvider\$app", "<init>", "()V", false)
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/dodge/hero/z/library/AppJoint", "register", "(Lcom/dodge/hero/z/library/IAppJointProvider;)V", false)
    }


    override fun onMethodExit(opcode: Int) {
        super.onMethodExit(opcode)
    }


}
