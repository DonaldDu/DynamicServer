package com.dhy.dynamicserver.demo

import android.content.Context
import com.dhy.dynamicserver.data.RemoteConfig
import com.dhy.dynamicserver.data.getUsingTestServer
import com.dhy.dynamicserver.demo.ApiUtil.Companion.apiUtil

enum class DynamicServer(private val release: String, private val test: String) {
    APP_BASE("http://www.app.com", "http://192.168.141.34:8093"),
    USER_CENTER("http://www.user.com", "http://192.168.141.34:8094"),
    ;

    override fun toString(): String {
        return RemoteConfig.serverMap[name] ?: release
    }

    companion object {
        fun init(context: Context) {
            if (BuildConfig.DEBUG) {
                RemoteConfig.initDynamicServer(DynamicServer::class.java)
                updateServer(context.getUsingTestServer())
            }
        }

        fun updateServer(config: RemoteConfig, context: Context? = null) {
            config.updateServerMap(context)
            config.toServers().forEach {
                if (it.releaseValue != null) apiUtil.updateApi(it.releaseValue!!, it.value)
            }
        }
    }
}

operator fun DynamicServer.plus(other: String): String {
    return toString() + other
}