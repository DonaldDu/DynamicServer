package com.dhy.dynamicserver.demo

import com.dhy.apiholder.BaseUrl
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