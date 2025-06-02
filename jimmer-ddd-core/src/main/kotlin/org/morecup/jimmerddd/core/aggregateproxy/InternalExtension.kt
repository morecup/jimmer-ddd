@file:JvmName("GlobalContext")
package org.morecup.jimmerddd.core.aggregateproxy

import org.babyfish.jimmer.runtime.DraftContext
import org.babyfish.jimmer.runtime.Internal
import kotlin.Throws

// 缓存 ThreadLocal 实例
private val DRAFT_CONTEXT_LOCAL: ThreadLocal<DraftContext> by lazy {
    // 延迟初始化，确保只反射一次
    runCatching {
        Internal::class.java
            .getDeclaredField("DRAFT_CONTEXT_LOCAL")
            .apply {
                isAccessible = true // 突破 private 限制
            }
            .get(null) as ThreadLocal<DraftContext> // 静态字段用 null 获取
    }.getOrElse {
        throw IllegalStateException("反射获取Internal下的DRAFT_CONTEXT_LOCAL 失败", it)
    }
}

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

fun <T> nullDraftContext( block: () -> T): T =
    usingDraftContext(null,block)

@Throws(Throwable::class)
fun <T> nullDraftContextWithThrowable( block: OneReturnLambda<T>): T =
    usingDraftContext(null,block::run)


fun interface OneReturnLambda<T> {
    @Throws(Throwable::class)
    fun run(): T
}