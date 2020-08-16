package com.dhy.dynamicserver.demo

import com.dhy.apiholder.ApiHolderUtil
import com.dhy.apiholder.BaseUrl
import com.dhy.apiholder.BaseUrlData
import com.dhy.dynamicserver.TestConfigApi
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET

interface ApiHolder : AppApi, UserCenterApi, PushCenterApi, TestConfigApi

@DynamicBaseUrl(DynamicServer.APP_BASE)
interface AppApi {
    @GET("login")
    fun login(): Observable<String>
}

@DynamicBaseUrl(DynamicServer.USER_CENTER)
interface UserCenterApi {
    @GET("userInfo")
    fun fetchUserInfo(): Observable<String>
}

@BaseUrl("http://www.push.com")
interface PushCenterApi {
    @GET("updatePushId")
    fun updatePushId(): Observable<String>
}

class ApiUtil : ApiHolderUtil<ApiHolder>(ApiHolder::class) {
    companion object {
        val apiUtil = ApiUtil()
        val api = apiUtil.api
    }

    override fun getUserBaseUrl(cls: Class<*>): BaseUrlData {
        val baseUrl = cls.getAnnotation(DynamicBaseUrl::class.java)
        return if (baseUrl != null) BaseUrlData(baseUrl.value.toString(), baseUrl.append)
        else {
            throw IllegalArgumentException(String.format("%s: MUST ANNOTATE WITH 'BaseUrl'", cls.name))
        }
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynamicBaseUrl(
    val value: DynamicServer,
    /**
     * append to value, auto check separator of '/'
     */
    val append: String = ""
)