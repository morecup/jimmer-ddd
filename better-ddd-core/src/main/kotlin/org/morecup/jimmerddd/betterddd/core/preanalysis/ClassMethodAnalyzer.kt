package org.morecup.jimmerddd.betterddd.core.preanalysis

import org.objectweb.asm.*
import org.objectweb.asm.Opcodes
import java.lang.invoke.SerializedLambda
import java.lang.reflect.Method
import kotlin.collections.getOrPut

class ClassMethodAnalyzer {

    data class AnalysisResult(
        val methodCalls: MutableMap<MethodInfo, MutableSet<MethodInfo>> = mutableMapOf(),
        val lambdaRelations: MutableMap<MethodInfo, MethodInfo> = mutableMapOf()
    ) {
        fun addCall(caller: MethodInfo, callee: MethodInfo) {
            methodCalls.getOrPut(caller) { mutableSetOf() }.add(callee)
        }

        fun linkLambda(lambdaMethod: MethodInfo, originMethod: MethodInfo) {
            lambdaRelations[lambdaMethod] = originMethod
        }

        fun printResults() {
            println("方法调用追踪结果:")
            methodCalls.keys.forEach { caller ->
                println("$caller 调用了:")
                printCallHierarchy(caller, indent = "  ", visited = mutableSetOf())
                println()
            }
        }

        fun getCalledListByMethod(method: MethodInfo): MutableSet<MethodInfo> {
            val mutableSet: MutableSet<MethodInfo> = mutableSetOf()
            callHierarchy(method,mutableSet)
            return mutableSet
        }

        private fun callHierarchy(
            method: MethodInfo,
            calledList: MutableSet<MethodInfo>,
        ){
            if (calledList.contains(method)) return
            calledList.add(method)

            val callees = methodCalls[method]?: mutableSetOf()
            lambdaRelations[method]?.let { info ->
                callees.add(info)
            }

            callees.forEach {
                callHierarchy(it, calledList)
            }
        }


        private fun printCallHierarchy(
            method: MethodInfo,
            indent: String,
            visited: MutableSet<MethodInfo>
        ) {
            if (method in visited) return  // 避免循环调用
            visited.add(method)

            val callees = methodCalls[method]?: mutableSetOf()
            lambdaRelations[method]?.let { info ->
                callees.add(info)
            }

            val lastIndex = callees.size - 1

            callees.forEachIndexed { index, callee ->
                val isLast = index == lastIndex
                val currentIndent = if (isLast) "└─ " else "├─ "
                val nextIndent = if (isLast) "   " else "│  "

                println("$indent$currentIndent${callee}")

//                val called: String? = methodCalls[callee]?.toString()
//                if (called != null) {
//                    println("$indent$currentIndent${callee}$lambdaOrigin")
//                }
//                // 处理lambda关联显示
//                val lambdaOrigin = lambdaRelations[callee]?.toString() ?: ""
//                println("$indent$currentIndent${callee}$lambdaOrigin")

                // 递归打印子调用
                printCallHierarchy(
                    callee,
                    indent + nextIndent,
                    visited = visited.toMutableSet()  // 为每个分支创建新的visited集合
                )
            }
        }
    }

    private class AnalyzerClassVisitor(
        private val result: AnalysisResult,
        private val currentClass: String
    ) : ClassVisitor(Opcodes.ASM9) {

        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor {
            val methodKey = MethodInfo(currentClass, name ?: "", descriptor ?: "")
            val mv = super.visitMethod(access, name, descriptor, signature, exceptions)

            return object : MethodVisitor(Opcodes.ASM9, mv) {
                // 处理普通方法调用
                override fun visitMethodInsn(
                    opcode: Int,
                    owner: String?,
                    name: String?,
                    descriptor: String?,
                    isInterface: Boolean
                ) {
                    if (true) {
                        val callee = MethodInfo(
                            owner ?: "",
                            name ?: "",
                            descriptor ?: ""
                        )
                        result.addCall(methodKey, callee)
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }

                // 处理 Lambda 表达式
                override fun visitInvokeDynamicInsn(
                    name: String?,
                    descriptor: String?,
                    bootstrapMethod: Handle?,
                    vararg bootstrapArgs: Any?
                ) {
                    if (isLambdaBootstrap(bootstrapMethod)) {
                        bootstrapArgs.filterIsInstance<Handle>().forEach { handle ->
                            when (handle.tag) {
                                Opcodes.H_INVOKESTATIC,
                                Opcodes.H_INVOKEVIRTUAL,
                                Opcodes.H_INVOKESPECIAL,
                                Opcodes.H_INVOKEINTERFACE -> {
                                    val lambdaMethod = MethodInfo(
                                        handle.owner,
                                        handle.name,
                                        handle.desc
                                    )
                                    result.linkLambda(methodKey,lambdaMethod)
                                }
                            }
                        }
                    }
                    super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethod, *bootstrapArgs)
                }

                private fun isLambdaBootstrap(handle: Handle?): Boolean {
                    return handle?.run {
                        owner == "java/lang/invoke/LambdaMetafactory" &&
                                (this.name == "metafactory" || this.name == "altMetafactory")
                    } == true
                }
            }
        }
    }

    companion object {

        fun analyze(targetClassName: String): AnalysisResult {
            val result = AnalysisResult()
            val classReader = ClassReader(targetClassName)
//            val classNode = ClassNode().also { classReader.accept(it, ClassReader.EXPAND_FRAMES) }

            // 第一遍分析：建立方法调用和 Lambda 关联
            classReader.accept(
                AnalyzerClassVisitor(result, targetClassName),
                ClassReader.EXPAND_FRAMES
            )

//            // 第二遍分析：处理 Lambda 方法体
//            classNode.methods.forEach { methodNode ->
//                val methodKey = AsmMethodInfo(classNode.name, methodNode.name, methodNode.desc)
//                if (result.lambdaRelations.containsKey(methodKey)) {
//                    result.addCall(methodKey,result.lambdaRelations[methodKey]!!)
//                }
//            }

            return result
        }

        fun analyzeMethods(methodInfo: MethodInfo): MutableSet<MethodInfo> {
            val result = analyze(methodInfo.ownerClass)
            val calledListByMethod: MutableSet<MethodInfo> = result.getCalledListByMethod(methodInfo)
            return calledListByMethod
        }

        fun analyzeMethods(method: Method): MutableSet<MethodInfo> {
            val result = analyze(method.declaringClass.name)
            val calledListByMethod: MutableSet<MethodInfo> = result.getCalledListByMethod(MethodInfo(method))
            return calledListByMethod
        }

        fun analyzeMethods(serializableLambda: SerializedLambda): MutableSet<MethodInfo> {
            val methodInfo = MethodInfo(serializableLambda)
            val result = analyze(methodInfo.ownerClass)
            val calledListByMethod: MutableSet<MethodInfo> = result.getCalledListByMethod(methodInfo)
            return calledListByMethod
        }
    }

}

class MethodInfo {
    var ownerClass: String  // 已处理后的类名
    var name: String
    var desc: String

    constructor(rawOwnerClass: String,name: String, desc: String) {
        ownerClass = rawOwnerClass.replace('/', '.')
        this.name = name
        this.desc = desc
    }

    constructor(method: Method) {
        ownerClass = method.declaringClass.name
        this.name = method.name
        this.desc = Type.getMethodDescriptor(method)
    }

    constructor(serializableLambda: SerializedLambda) {
        ownerClass = serializableLambda.implClass.replace('/', '.')
        this.name = serializableLambda.implMethodName
        this.desc = serializableLambda.implMethodSignature
    }

    override fun toString() = "$ownerClass.$name$desc"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as MethodInfo
        if (ownerClass != other.ownerClass) return false
        if (name != other.name) return false
        if (desc != other.desc) return false
        return true
    }

    override fun hashCode(): Int {
        var result = ownerClass.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + desc.hashCode()
        return result
    }
}