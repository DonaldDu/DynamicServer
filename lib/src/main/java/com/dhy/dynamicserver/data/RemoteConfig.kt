package com.dhy.dynamicserver.data

import android.content.Context
import androidx.annotation.Keep
import com.dhy.dynamicserver.IConfigFormatter
import com.dhy.dynamicserver.TestConfigUtil
import com.dhy.dynamicserver.data.RemoteConfig.Companion.dynamicServers
import com.dhy.dynamicserver.data.RemoteConfig.Companion.releaseServerTypeName
import com.dhy.xpreference.XPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.Serializable

@Keep
class RemoteConfig : Serializable {
    var name = ""
    var values: MutableList<String> = mutableListOf()
    fun add(name: String, value: String) {
        values.add("$name@$value")
    }

    @Transient
    var configFormatter: IConfigFormatter? = null
    override fun toString(): String {
        val formatter = configFormatter ?: TestConfigUtil.configFormatter
        return formatter.format(this)
    }

    val isEmpty: Boolean
        get() {
            return name.isEmpty()
        }

    fun isRelease(): Boolean {
        return name.lowercase() == releaseServerTypeName
    }

    fun toServers(): List<IDynamicServer> {
        return values.map {
            val kv = it.split("@")
            Server(name, kv.first(), kv.last())
        }
    }

    fun updateServerMap(context: Context? = null) {
        serverMap.clear()
        values.forEach {
            val kv = it.split("@")
            serverMap[kv.first()] = kv.last()
        }
        if (context != null) XPreferences.put(context, this)
    }

    fun hasAllServers(): Boolean {
        val newServers by lazy { dynamicServers.map { it.name } }
        val servers = toServers().map { it.name }
        return servers.containsAll(newServers)
    }

    fun toTestUser(): TestUser {
        val namePwd = values.first().split("@")
        val pwd = if (namePwd.size == 2) namePwd.last() else null
        return TestUser(name, namePwd.first(), pwd)
    }

    companion object {
        @JvmStatic
        internal var dynamicServers: List<IDynamicServer> = emptyList()
        internal var releaseServerTypeName: String = "release"

        @JvmStatic
        val serverMap: MutableMap<String, String> = mutableMapOf()

        @JvmStatic
        fun initDynamicServer(dynamicServer: Class<out Enum<*>>, releaseServerTypeName: String = "release") {
            this.releaseServerTypeName = releaseServerTypeName
            dynamicServers = dynamicServer.getDynamicServers()
        }

        internal fun getConfigs(): List<RemoteConfig> {
            return dynamicServers.groupBy { it.type }
                .map {
                    val config = RemoteConfig()
                    config.name = it.key
                    it.value.forEach { ds ->
                        config.add(ds.name, ds.value)
                    }
                    config
                }
        }

        @JvmStatic
        internal fun getReleaseConfig(): RemoteConfig? {
            return getConfigs().find { it.isRelease() }
        }
    }
}

private fun Class<out Enum<*>>.getDynamicServers(): List<IDynamicServer> {
    val dynamicServers: MutableList<IDynamicServer> = mutableListOf()
    val serverTypes = declaredFields.filter {
        it.type.isAssignableFrom(String::class.java)
    }
    val servers = declaredFields.filter {
        it.type.isAssignableFrom(this)
    }.map { it.get(null) as Enum<*> }

    serverTypes.forEach { type ->
        type.isAccessible = true
        servers.forEach {
            dynamicServers.add(Server(type.name, it.name, type.get(it) as String))
        }
    }

    return dynamicServers
}

fun Enum<*>.release(): String {
    return dynamicServers.find { it.name == name && it.type == releaseServerTypeName }?.value ?: toString()
}

suspend fun Context.getUsingTestServer(): RemoteConfig {
    return withContext(Dispatchers.IO) {
        var testServer: RemoteConfig = getPref()
        if (testServer.isEmpty || !testServer.hasAllServers()) {
            val release = RemoteConfig.getReleaseConfig()
            testServer = release ?: testServer
            if (release == null) {
                val msg = if (dynamicServers.isEmpty()) {
                    "please call RemoteConfig.initDynamicServers first"
                } else {
                    "dynamicServer must contain '$releaseServerTypeName' server type"
                }
                throw Exception(msg)
            }
        }
        testServer
    }
}

suspend fun <T> getPref(context: Context, clazz: Class<T>): T {
    return withContext(Dispatchers.IO) {
        XPreferences.get(context, clazz, false)
    }
}

suspend inline fun <reified T> Context.getPref(): T {
    return getPref(this, T::class.java)
}

fun runInIoScope(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}

fun runInMainScope(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}