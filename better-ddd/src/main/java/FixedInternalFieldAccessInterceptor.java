import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.jar.asm.*;
import net.bytebuddy.pool.TypePool;

import java.lang.instrument.Instrumentation;

import static net.bytebuddy.jar.asm.Opcodes.*;

public class FixedInternalFieldAccessInterceptor {

    public static class Person {
        public String name = "John";
        public int age = 30;

        public void update(String newName, int newAge) {
            name = newName;   // 内部写入 name
            age = newAge;     // 内部写入 age
            String fullName = name + " Doe";  // 内部读取 name
        }

        public void birthday() {
            age++;  // 内部读取和写入 age
            System.out.println(name + " is now " + age);
        }
    }

    public static class FieldInterceptor {
        // 字段读拦截器
        public static void onFieldRead(Object target, String owner, String name, String desc) {
            System.out.printf("[READ] %s.%s (%s)%n",
                    owner.replace('/', '.'), name, desc);
        }

        // 字段写拦截器
        public static void onFieldWrite(Object target, Object value, String owner, String name, String desc) {
            System.out.printf("[WRITE] %s.%s (%s) << %s%n",
                    owner.replace('/', '.'), name, desc, value);
        }
    }

    public static void main(String[] args) throws Exception {
        // 安装 Byte Buddy Agent
        Instrumentation inst = ByteBuddyAgent.install();

        // 创建字节码增强类
        DynamicType.Unloaded<Person> dynamicType = new ByteBuddy()
                .redefine(Person.class)
                .visit(new FieldAccessVisitorWrapper())
                .make();

        // 保存字节码用于调试（可选）
        // FileOutputStream fos = new FileOutputStream("Person.class");
        // fos.write(dynamicType.getBytes());
        // fos.close();

        // 加载增强后的类
        Class<?> enhancedClass = dynamicType.load(
                FixedInternalFieldAccessInterceptor.class.getClassLoader(),
                ClassReloadingStrategy.fromInstalledAgent()
        ).getLoaded();

        // 创建实例并测试
        Person person = (Person) enhancedClass.getDeclaredConstructor().newInstance();

        // 测试直接字段访问
        System.out.println("Direct read: " + person.name);
        person.name = "Alice";

        // 测试内部方法中的字段访问
        person.update("Bob", 40);
        person.birthday();
    }

    // 字节码访问包装器 - 使用 COMPUTE_FRAMES 处理栈帧
    static class FieldAccessVisitorWrapper implements AsmVisitorWrapper {
        @Override
        public int mergeWriter(int flags) {
            return flags | ClassWriter.COMPUTE_FRAMES;
        }

        @Override
        public int mergeReader(int flags) {
            return flags;
        }

        @Override
        public ClassVisitor wrap(TypeDescription instrumentedType, ClassVisitor classVisitor, Implementation.Context implementationContext, TypePool typePool, FieldList<FieldDescription.InDefinedShape> fields, MethodList<?> methods, int writerFlags, int readerFlags) {
            return new FieldAccessClassVisitor(ASM9, classVisitor);
        }
    }

    // 类访问器
    static class FieldAccessClassVisitor extends ClassVisitor {
        public FieldAccessClassVisitor(int api, ClassVisitor cv) {
            super(api, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor,
                                         String signature, String[] exceptions) {
            MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new FieldAccessMethodVisitor(api, mv, access, name, descriptor);
        }
    }

    // 方法访问器 - 核心拦截逻辑（保持栈平衡）
    static class FieldAccessMethodVisitor extends MethodVisitor {
        private final int methodAccess;
        private final String methodName;
        private final String methodDesc;

        public FieldAccessMethodVisitor(int api, MethodVisitor mv, int access, String name, String desc) {
            super(api, mv);
            this.methodAccess = access;
            this.methodName = name;
            this.methodDesc = desc;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            // 先调用原始指令
            super.visitFieldInsn(opcode, owner, name, descriptor);

            // 在原始操作后添加拦截调用
            if (opcode == GETFIELD || opcode == GETSTATIC) {
                addFieldReadInterception(opcode, owner, name, descriptor);
            } else if (opcode == PUTFIELD || opcode == PUTSTATIC) {
                addFieldWriteInterception(opcode, owner, name, descriptor);
            }
        }

        private void addFieldReadInterception(int opcode, String owner, String name, String descriptor) {
            boolean isStatic = (opcode == GETSTATIC);

            // 复制字段值（用于后续使用）
            addDup(descriptor);

            // 压入目标对象（静态字段使用null）
            if (isStatic) {
                visitInsn(ACONST_NULL);
            } else {
                // 对于实例字段，对象引用在字段值下方
                visitDupX(descriptor, 1);
            }

            // 压入字段信息
            visitLdcInsn(owner);
            visitLdcInsn(name);
            visitLdcInsn(descriptor);

            // 调用读拦截器
            visitMethodInsn(INVOKESTATIC,
                    Type.getInternalName(FieldInterceptor.class),
                    "onFieldRead",
                    "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    false);
        }

        private void addFieldWriteInterception(int opcode, String owner, String name, String descriptor) {
            boolean isStatic = (opcode == PUTSTATIC);

            // 复制新值（用于后续使用）
            addDup(descriptor);

            // 压入目标对象（静态字段使用null）
            if (isStatic) {
                visitInsn(ACONST_NULL);
            } else {
                // 对于实例字段，复制对象引用
                if (descriptor.equals("D") || descriptor.equals("J")) {
                    // 长整型/双精度型需要特殊处理
                    visitInsn(DUP_X2);
                    visitInsn(POP);
                } else {
                    visitInsn(DUP_X1);
                    visitInsn(SWAP);
                }
            }

            // 压入字段信息
            visitLdcInsn(owner);
            visitLdcInsn(name);
            visitLdcInsn(descriptor);

            // 调用写拦截器
            visitMethodInsn(INVOKESTATIC,
                    Type.getInternalName(FieldInterceptor.class),
                    "onFieldWrite",
                    "(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
                    false);
        }

        private void addDup(String descriptor) {
            if (descriptor.equals("D") || descriptor.equals("J")) {
                visitInsn(DUP2);
            } else {
                visitInsn(DUP);
            }
        }

        private void visitDupX(String descriptor, int depth) {
            if (descriptor.equals("D") || descriptor.equals("J")) {
                switch (depth) {
                    case 1: visitInsn(DUP2_X1); break;
                    case 2: visitInsn(DUP2_X2); break;
                    default: visitInsn(DUP2);
                }
            } else {
                switch (depth) {
                    case 1: visitInsn(DUP_X1); break;
                    case 2: visitInsn(DUP_X2); break;
                    default: visitInsn(DUP);
                }
            }
        }
    }
}