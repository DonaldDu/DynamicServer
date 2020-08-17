package com.dhy.dynamicserver.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.dynamicserver.TestServerUtil
import com.dhy.dynamicserver.TestUserUtil
import com.dhy.dynamicserver.data.RemoteConfig
import com.dhy.dynamicserver.data.getUsingTestServer
import com.dhy.dynamicserver.demo.ApiUtil.Companion.api
import com.dhy.dynamicserver.updateServerLabel
import com.dhy.retrofitrxutil.ObserverX
import com.dhy.retrofitrxutil.sample.SampleStyledProgressGenerator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ObserverX.setProgressGenerator(SampleStyledProgressGenerator())
        ObserverX.setErrorHandler(MyNetErrorHandler())
        val context = this
        DynamicServer.init(this)
        val user = object : TestUserUtil(context, api) {
            override fun onConfigSelected(config: RemoteConfig) {
                Toast.makeText(context, "${config.name}：${config.values.first()}", Toast.LENGTH_SHORT).show()
            }
        }
        user.initShowOnClick(buttonUser, false)

        val server = object : TestServerUtil(context, api) {
            override fun onConfigSelected(config: RemoteConfig) {
                DynamicServer.updateServer(config, context)
                tvServers.updateServerLabel(config)
            }
        }
        server.initShowOnClick(buttonServer, false)
        server.initShowOnClick(tvServers, true)
        tvServers.updateServerLabel(getUsingTestServer())

        btClearData.setOnClickListener {
            Runtime.getRuntime().exec("pm clear $packageName")
            Toast.makeText(this, "cleared", Toast.LENGTH_SHORT).show()
        }
    }

    fun showUserPage() {
        val host = DynamicServer.USER_CENTER
        println("$host/userPage")
    }
}
