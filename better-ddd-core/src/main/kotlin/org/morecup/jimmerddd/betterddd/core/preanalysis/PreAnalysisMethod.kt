package org.morecup.jimmerddd.betterddd.core.preanalysis

import org.morecup.jimmerddd.betterddd.core.annotation.AggregateRoot
import org.morecup.jimmerddd.betterddd.core.annotation.DomainEntity
import org.morecup.jimmerddd.betterddd.core.annotation.ValueObj
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.reflect.Method

fun analysisMethodsCalledFields(methods: Set<Method>):Set<FieldInfo> {
    val methodInfoSet: Set<MethodInfo> = methods.flatMap { ClassMethodAnalyzer.analyzeMethods(it) }.toSet()
    val propertiesAccessed = analyzePropertiesAccessedByMethods(methodInfoSet)
    println("Properties accessed: $propertiesAccessed")
    return propertiesAccessed
}

class FieldInfo{
    var owner:Class<*>
    var name:String

//    constructor(owner: String,name: String) {
//        this.owner = owner.replace('/', '.')
//        this.name = name
//    }

    constructor(owner: Class<*>,name: String) {
        this.owner = owner
        this.name = name
    }
}

// Discover properties accessed in methods using ASM
private fun analyzePropertiesAccessedByMethods(methods: Set<MethodInfo>): Set<FieldInfo> {
    val accessedProperties = mutableSetOf<FieldInfo>()

    for (method in methods) {
        val classReader = ClassReader(method.ownerClass)
        classReader.accept(object : ClassVisitor(Opcodes.ASM9) {
            override fun visitMethod(
                access: Int,
                name: String?,
                descriptor: String?,
                signature: String?,
                exceptions: Array<out String>?
            ): MethodVisitor? {
                return if (name == method.name && descriptor == method.desc) {
                    object : MethodVisitor(Opcodes.ASM9) {
                        override fun visitFieldInsn(
                            opcode: Int,
                            owner: String?,
                            name: String?,
                            descriptor: String?
                        ) {
                            if (opcode == Opcodes.GETFIELD) {
                                owner?.let {  ownerNotNull ->
                                    name?.let {
                                        val clazz = Class.forName(ownerNotNull.replace('/', '.'))
                                        if (clazz.isAnnotationPresent(AggregateRoot::class.java)||
                                            clazz.isAnnotationPresent(DomainEntity::class.java)||
                                            clazz.isAnnotationPresent(ValueObj::class.java)
                                        ) {
                                            accessedProperties.add(FieldInfo(clazz, it))
                                        }
                                    }
                                }
                                
                            }
                        }
                    }
                } else null
            }
        }, 0)
    }

    return accessedProperties
}