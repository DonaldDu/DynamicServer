package com.dhy.dynamicserver.data

import com.google.gson.Gson
import java.io.Serializable

class FetchConfigRequest(appId: String, configName: String) : Serializable {
    private var applicationId: String = appId
    private var name: String = configName
    private val data = mapOf("\$exists" to true)
    override fun toString(): String {
        return Gson().toJson(this)
    }
}