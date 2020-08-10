package com.dhy.dynamicserver.data

import java.io.Serializable

class ConfigResponse : Serializable {
    private val results: List<ConfigRecord>? = null

    val configs: List<RemoteConfig>?
        get() = if (results?.size == 1) results.first().data else null

    private class ConfigRecord : Serializable {
        internal var data: List<RemoteConfig>? = null
    }
}