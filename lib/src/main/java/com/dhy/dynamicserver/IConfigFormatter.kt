package com.dhy.dynamicserver

import com.dhy.dynamicserver.data.RemoteConfig

interface IConfigFormatter {
    fun format(config: RemoteConfig): String {
        val server = config.values.joinToString("\n")
        return "${config.name.captureName()}\n$server"
    }
}

private val azRange = 97..122
fun String.captureName(): String {
    val cs = toCharArray()
    return if (azRange.contains(cs[0].toInt())) {
        cs[0] = cs[0] - 32
        String(cs)
    } else this
}