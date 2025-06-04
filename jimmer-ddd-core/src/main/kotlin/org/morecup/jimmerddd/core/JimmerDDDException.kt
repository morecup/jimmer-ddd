package org.morecup.jimmerddd.core

open class JimmerDDDException  : RuntimeException{
    constructor(cause: Throwable?) : super(cause)

    constructor(message: String?, cause: Throwable?) : super(message, cause)

    constructor(message: String?) : super(message)
}