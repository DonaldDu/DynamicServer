package com.dhy.dynamicserver

import android.content.Context
import android.view.View
import android.widget.TextView
import com.dhy.dynamicserver.data.RemoteConfig
import com.dhy.xintent.formatText

open class TestServerUtil(context: Context, api: TestConfigApi) : TestConfigUtil(context, api, "TestServers") {
    override fun loadData(): List<RemoteConfig> {
        return super.loadData().filter { it.hasAllServers() }
    }

    override fun genDefaultConfigs(): List<RemoteConfig> {
        return RemoteConfig.getConfigs()
    }

    companion object {
        @JvmStatic
        @Deprecated(message = "user TextView.updateServerLabel plz", replaceWith = ReplaceWith("serverLabel.updateServerLabel(usingTestServer)"))
        fun updateServerLabel(serverLabel: TextView, usingTestServer: RemoteConfig) {
            serverLabel.updateServerLabel(usingTestServer)
        }
    }
}

fun TextView.updateServerLabel(usingTestServer: RemoteConfig) {
    this.visibility = View.VISIBLE
    formatText(usingTestServer.toString())
}