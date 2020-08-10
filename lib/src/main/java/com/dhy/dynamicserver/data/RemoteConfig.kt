package com.dhy.dynamicserver.data

import android.content.Context
import androidx.annotation.Keep
import com.dhy.dynamicserver.IConfigFormatter
import com.dhy.dynamicserver.TestConfigUtil
import com.dhy.xpreference.XPreferences
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
        val formater = configFormatter ?: TestConfigUtil.configFormatter
        return formater.format(this)
    }

    val isEmpty: Boolean
        get() {
            return name.isEmpty()
        }

    fun isRelease(): Boolean {
        return name.toLowerCase() == "release"
    }

    fun toConfigs(): List<Config> {
        return values.map {
            val kv = it.split("@")
            Config(kv.first(), kv.last())
        }
    }

    fun updateServersMap(context: Context? = null) {
        serversMap.clear()
        values.forEach {
            val kv = it.split("@")
            serversMap[kv.first()] = kv.last()
        }
        if (context != null) XPreferences.put(context, this)
    }

    fun isValid(): Boolean {
        val servers = toConfigs().map { it.name }
        return servers.containsAll(newServers)
    }

    companion object {
        @JvmStatic
        lateinit var dynamicServers: List<IDynamicServer>
        private val newServers by lazy { dynamicServers.map { it.name } }

        @JvmStatic
        val serversMap: MutableMap<String, String> = mutableMapOf()

        @JvmStatic
        fun getConfigs(): List<RemoteConfig> {
            val test = RemoteConfig().apply { name = "Test" }
            val release = RemoteConfig().apply { name = "Release" }
            dynamicServers.forEach {
                test.add(it.name, it.test)
                release.add(it.name, it.release)
            }
            return listOf(test, release)
        }

        @JvmStatic
        fun getReleaseConfig(): RemoteConfig {
            return getConfigs().find { it.isRelease() }!!
        }
    }
}

fun Context.getUsingTestServer(): RemoteConfig {
    var testServer: RemoteConfig = XPreferences.get(this)
    if (testServer.isEmpty || !testServer.isValid()) {
        testServer = RemoteConfig.getReleaseConfig()
    }
    return testServer
}
