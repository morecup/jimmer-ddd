package org.morecup.jimmerddd.core

open class JimmerDDDException @JvmOverloads constructor(
    message: String = "",
    cause: Throwable? = null
) : RuntimeException(message, cause)