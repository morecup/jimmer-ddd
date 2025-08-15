import org.morecup.jimmerddd.betterddd.core.preanalysis.MethodInfo
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.lang.reflect.Method

fun analysisStackTraceElementCalledMethod(stackTraceElement: StackTraceElement, aggregateRootClass: Class<*>):MutableSet<Method> {
    val className = stackTraceElement.className
    val methodName = stackTraceElement.methodName
    val lineNumber = stackTraceElement.lineNumber
    
    // 使用ASM获取更准确的方法信息
    var targetMethodDescriptor: String? = null
    var targetMethod: Method? = null
    
    val methodFinder = object : ClassVisitor(Opcodes.ASM9) {
        override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String,
            signature: String?,
            exceptions: Array<out String>?
        ): MethodVisitor? {
            // 匹配方法名
            if (name == methodName) {
                return object : MethodVisitor(Opcodes.ASM9) {
                    var firstLine = Int.MAX_VALUE
                    var lastLine = -1
                    
                    override fun visitLineNumber(line: Int, start: org.objectweb.asm.Label?) {
                        if (line < firstLine) firstLine = line
                        if (line > lastLine) lastLine = line
                        
                        // 如果行号匹配，这就是我们要找的方法
                        if (line == lineNumber) {
                            targetMethodDescriptor = descriptor
                        }
                    }
                    
                    override fun visitEnd() {
                        // 如果没有精确匹配行号，但行号在方法范围内也算匹配
                        if (targetMethodDescriptor == null && 
                            lineNumber >= firstLine && lineNumber <= lastLine && firstLine != Int.MAX_VALUE) {
                            targetMethodDescriptor = descriptor
                        }
                    }
                }
            }
            return null
        }
    }
    
    // 分析类文件获取方法描述符
    try {
        val classReader = ClassReader(className)
        classReader.accept(methodFinder, 0)
    } catch (e: Exception) {
        throw RuntimeException("无法分析类文件: $className", e)
    }
    
    if (targetMethodDescriptor == null) {
        // 回退到仅通过方法名匹配（在有重载的情况下可能不准确）
        val sourceClass = Class.forName(className)
        val candidateMethods = sourceClass.declaredMethods.filter { it.name == methodName }

        if (candidateMethods.size == 1) {
            // 如果只有一个匹配的方法，直接使用它
            targetMethodDescriptor = getMethodDescriptor(candidateMethods[0])
        } else {
            // 如果有多个重载方法，使用第一个作为默认选择（可能不准确）
            targetMethodDescriptor = getMethodDescriptor(candidateMethods.firstOrNull()
                ?: throw RuntimeException("找不到方法: $methodName"))
        }
//        throw RuntimeException("找不到匹配的方法: $methodName 在行号: $lineNumber")
    }
    
    // 使用反射获取实际的Method对象
    val sourceClass = Class.forName(className)
    targetMethod = findMethodByDescriptor(sourceClass, methodName, targetMethodDescriptor!!)
    
    if (targetMethod == null) {
        throw RuntimeException("无法通过反射获取方法: $methodName with descriptor $targetMethodDescriptor")
    }

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
            if (name != methodName || descriptor != targetMethodDescriptor) {
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
                        aggregateRootClass.declaredMethods.find { method ->
                            method.name == name &&
                                    getMethodDescriptor(method) == descriptor
                        }?.let { method ->
                            calledMethods.add(method)
                        }
                    }
                }
            }
        }
    }

    // 开始分析
    val classReader = ClassReader(className)
    classReader.accept(visitor, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)

    // 输出结果
    println("方法 $className$methodName$targetMethodDescriptor 调用了 ${aggregateRootClass.simpleName} 的以下方法：")
    calledMethods.forEach { method ->
        println("- ${method.name}(${method.parameterTypes.joinToString(", ") { it.simpleName }})")
    }
    return calledMethods
}

/**
 * 根据方法描述符查找Method对象
 */
private fun findMethodByDescriptor(clazz: Class<*>, methodName: String, descriptor: String): Method? {
    return clazz.declaredMethods.find { method ->
        method.name == methodName && getMethodDescriptor(method) == descriptor
    } ?: clazz.methods.find { method ->
        method.name == methodName && getMethodDescriptor(method) == descriptor
    }
}

private fun getMethodDescriptor(method: Method): String {
    return org.objectweb.asm.Type.getMethodDescriptor(method)
}