package com.dhy.dynamicserver.data

class Server(override val type: String, override val name: String, override val value: String) : IDynamicServer {
    override val releaseValue: String? by lazy {
        RemoteConfig.dynamicServers.find { it.name == name && it.type == type }?.value
    }
}