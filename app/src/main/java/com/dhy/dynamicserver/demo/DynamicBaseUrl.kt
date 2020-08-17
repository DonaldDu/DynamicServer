package com.dhy.dynamicserver.demo

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynamicBaseUrl(
    val value: DynamicServer,
    /**
     * append to value, auto check separator of '/'
     */
    val append: String = ""
)