package com.dhy.dynamicserver

import com.dhy.dynamicserver.data.RemoteConfig

interface IConfigFormatter {
    fun format(config: RemoteConfig): String {
        val server = config.values.joinToString("\n")
        return "${config.name}\n$server"
    }
}