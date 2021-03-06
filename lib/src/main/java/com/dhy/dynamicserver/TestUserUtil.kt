package com.dhy.dynamicserver

import android.content.Context
import com.dhy.dynamicserver.data.RemoteConfig

open class TestUserUtil(context: Context, api: TestConfigApi?) : TestConfigUtil(context, api, "TestUsers") {
    /**
     * 建议创建默认样例后，在远程复制样例添加真实数据
     * */
    override fun genDefaultConfigs(): List<RemoteConfig> {
        val cf = RemoteConfig()
        cf.name = "UserName"
        cf.add("AccountName", "password")

        val config = RemoteConfig()
        config.name = "张三"
        config.add("10086", "123")
        return listOf(cf, config)
    }
}
