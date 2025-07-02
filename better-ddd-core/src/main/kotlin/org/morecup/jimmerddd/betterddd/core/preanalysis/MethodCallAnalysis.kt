import org.morecup.jimmerddd.betterddd.core.preanalysis.MethodInfo
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.reflect.Method

fun analysisStackTraceElementCalledMethod(stackTraceElement: StackTraceElement, aggregateRootClass: Class<*>):MutableSet<Method> {
    val className = stackTraceElement.className
    val methodName = stackTraceElement.methodName
    // 获取源类
    val sourceClass = Class.forName(className)

    // 获取具体的源方法，需要同时匹配行号
    val sourceMethod = try {
        sourceClass.declaredMethods.find { method ->
            // 通过反射获取方法的行号信息
            val methodLineNumber = try {
                method.declaringClass
                    .getDeclaredMethod(method.name, *method.parameterTypes)
                    .let { sourceClass.getDeclaredMethod(it.name, *it.parameterTypes) }
                    .run {
                        val lineNumberTable = this.javaClass.getDeclaredField("slot")
                            .apply { isAccessible = true }
                            .get(this) as? Int
                        lineNumberTable
                    }
            } catch (e: Exception) {
                null
            }

            method.name == methodName && methodLineNumber == stackTraceElement.lineNumber
        }
    } catch (e: Exception) {
        null
    } ?: throw RuntimeException("找不到方法调用")

    val sourceDescriptor = getMethodDescriptor(sourceMethod)


    val calledMethods = mutableSetOf<Method>()

    val visitor = object : ClassVisitor(Opcodes.ASM9) {
        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor? {
            // 只访问目标方法
            if (name != methodName || descriptor != sourceDescriptor) {
                return null
            }

            return object : MethodVisitor(Opcodes.ASM9) {
                override fun visitMethodInsn(
                    opcode: Int,
                    owner: String,
                    name: String,
                    descriptor: String,
                    isInterface: Boolean
                ) {
                    // 检查是否调用了目标类的方法
                    if (owner == aggregateRootClass.name.replace('.', '/')) {
                        // 查找对应的方法
                        aggregateRootClass.methods.find { method ->
                            method.name == name &&
                                    getMethodDescriptor(method) == descriptor
                        }?.let { method ->
                            calledMethods.add(method)
                        }
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
                }
            }
        }
    }

    // 开始分析
    val classReader = ClassReader(className)
    classReader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

    // 输出结果
    println("方法 $methodName${sourceDescriptor} 调用了 ${aggregateRootClass.simpleName} 的以下方法：")
    calledMethods.forEach { methodInfo ->
        println("- ${methodInfo.name}(${
            methodInfo
        })")
    }
    return calledMethods
}

private fun getMethodDescriptor(method: Method): String {
    return org.objectweb.asm.Type.getMethodDescriptor(method)
}