package com.dhy.dynamicserver

import android.content.Context
import android.view.View
import android.widget.TextView
import com.dhy.dynamicserver.data.RemoteConfig
import com.dhy.dynamicserver.data.getUsingTestServer
import com.dhy.dynamicserver.data.runInMainScope
import com.dhy.xintent.formatText

open class TestServerUtil(context: Context, api: TestConfigApi?) : TestConfigUtil(context, api, "TestServers") {
    override suspend fun loadData(): List<RemoteConfig> {
        return super.loadData().filter { it.hasAllServers() }
    }

    override fun genDefaultConfigs(): List<RemoteConfig> {
        return RemoteConfig.getConfigs()
    }

    override fun setUpConfigItemView(tv: TextView) {
        tv.textSize = 12f
    }
}

fun TextView.updateServerLabel(usingTestServer: RemoteConfig? = null) {
    this.visibility = View.VISIBLE
    if (usingTestServer != null) {
        formatText(usingTestServer)
    } else {
        runInMainScope {
            formatText(context.getUsingTestServer())
        }
    }
}