package com.dhy.dynamicserver.data

import java.io.Serializable

class CreateConfigRequest(appId: String, configName: String) : Serializable {
    private var applicationId: String = appId
    private var name: String = configName
    var data: List<RemoteConfig>? = null
}
