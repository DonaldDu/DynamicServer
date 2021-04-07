package com.dhy.dynamicserver.data

import java.io.Serializable

class ConfigResponse : Serializable {
    var code: Int = 0
    var error: String? = null
    private val results: List<ConfigRecord>? = null

    val configs: List<RemoteConfig>?
        get() = if (results?.size == 1) results.first().data else null

    private class ConfigRecord : Serializable {
        var data: List<RemoteConfig>? = null
    }

    fun isClassNotExists(): Boolean {
        return code == 101
    }
}