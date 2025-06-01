package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.Internal
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import kotlin.Throws

object GlobalContext {
    // 缓存 ThreadLocal 实例
    val DRAFT_CONTEXT_LOCAL: ThreadLocal<DraftContext> by lazy {
        // 延迟初始化，确保只反射一次
        runCatching {
            Internal::class.java
                .getDeclaredField("DRAFT_CONTEXT_LOCAL")
                .apply {
                    isAccessible = true // 突破 private 限制
                    // 如果需要修改 final 字段，移除 final 修饰符（可选）
                    val modifiersField = Field::class.java.getDeclaredField("modifiers")
                    modifiersField.isAccessible = true
                    modifiersField.setInt(this, modifiers and Modifier.FINAL.inv())
                }
                .get(null) as ThreadLocal<DraftContext> // 静态字段用 null 获取
        }.getOrElse {
            throw IllegalStateException("反射获取Internal下的DRAFT_CONTEXT_LOCAL 失败", it)
        }
    }

    @JvmStatic
    fun <T> usingDraftContext(ctx: DraftContext?, block: () -> T): T {
        // 1. 保存旧上下文
        val oldCtx: DraftContext? = DRAFT_CONTEXT_LOCAL.get()

        // 3. 设置新上下文到 ThreadLocal
        DRAFT_CONTEXT_LOCAL.set(ctx)

        return try {
            // 4. 执行业务逻辑块
            val result = block.invoke()

            result
        } finally {
            // 6. 恢复旧上下文（无论是否发生异常都会执行）
            if (oldCtx != null) {
                DRAFT_CONTEXT_LOCAL.set(oldCtx)
            } else {
                DRAFT_CONTEXT_LOCAL.remove()
            }
        }
    }

    @JvmStatic
    fun <T> nullDraftContext( block: () -> T): T =
        usingDraftContext(null,block)

    @JvmStatic
    @Throws(Throwable::class)
    fun <T> nullDraftContextWithThrowable( block: OneReturnLambda<T>): T =
        usingDraftContext(null,block::run)
}

fun interface OneReturnLambda<T> {
    @Throws(Throwable::class)
    fun run(): T
}