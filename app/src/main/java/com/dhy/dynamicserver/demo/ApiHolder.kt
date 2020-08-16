package com.dhy.dynamicserver.demo

import com.dhy.apiholder.ApiHolderUtil
import com.dhy.apiholder.BaseUrlData
import com.dhy.dynamicserver.TestConfigApi

interface ApiHolder : SysApi, YWApi, TestConfigApi

@DynamicBaseUrl(DynamicServer.BASE_URL)
interface SysApi {

}

@DynamicBaseUrl(DynamicServer.YW_URL)
interface YWApi {

}

class ApiUtil : ApiHolderUtil<ApiHolder>(ApiHolder::class) {
    override fun getUserBaseUrl(cls: Class<*>): BaseUrlData {
        val baseUrl = cls.getAnnotation(DynamicBaseUrl::class.java)
        return if (baseUrl != null) BaseUrlData(baseUrl.value.toString(), baseUrl.append, baseUrl.rootApi)
        else {
            throw IllegalArgumentException(String.format("%s: MUST ANNOTATE WITH 'BaseUrl'", cls.name))
        }
    }
}

@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class DynamicBaseUrl(
    val value: DynamicServer,
    /**
     * append to value, auto check separator of '/'
     */
    val append: String = "",
    /**
     * marke as root api for  [ApiHolderUtil.isRelease]
     */
    val rootApi: Boolean = false
)