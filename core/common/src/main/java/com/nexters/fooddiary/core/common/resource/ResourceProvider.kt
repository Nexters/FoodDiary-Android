package com.nexters.fooddiary.core.common.resource

interface ResourceProvider {
    fun getString(resId: Int): String
    fun getString(resId: Int, vararg formatArgs: Any): String
    fun getDimension(resId: Int): Float
}

