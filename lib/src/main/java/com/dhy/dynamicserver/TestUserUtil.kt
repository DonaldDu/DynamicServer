package com.dhy.dynamicserver

import android.content.Context
import android.widget.EditText
import android.widget.TextView
import com.dhy.dynamicserver.data.RemoteConfig

open class TestUserUtil(context: Context, api: TestConfigApi, private val userEt: EditText?) : TestConfigUtil(context, api, "TestUsers") {
    /**
     * 建议创建默认样例后，在远程复制样例添加真实数据
     * */
    override fun genDefaultConfigs(): List<RemoteConfig> {
        val cf = RemoteConfig()
        cf.name = "UserName"
        cf.values.add("pwd123")
        return listOf(cf)
    }

    private val testUserFormatter = object : IConfigFormatter {
        override fun format(config: RemoteConfig): String {
            return if (config.values.isNotEmpty()) "${config.name}@${config.values.first()}"
            else config.name
        }
    }

    override fun getConfigFormatter(): IConfigFormatter {
        return testUserFormatter
    }

    override fun setUpConfigItemView(tv: TextView) {}

    override fun onConfigSelected(config: RemoteConfig) {
        userEt?.setText(config.values.first())
    }
}
