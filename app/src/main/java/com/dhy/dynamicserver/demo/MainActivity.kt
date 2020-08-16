package com.dhy.dynamicserver.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhy.apiholder.ApiHolderUtil
import com.dhy.dynamicserver.TestServerUtil
import com.dhy.dynamicserver.TestUserUtil
import com.dhy.dynamicserver.data.RemoteConfig
import com.dhy.retrofitrxutil.ObserverX
import com.dhy.retrofitrxutil.sample.SampleStyledProgressGenerator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var api: ApiHolder
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ObserverX.setProgressGenerator(SampleStyledProgressGenerator())
        ObserverX.setErrorHandler(MyNetErrorHandler())
        api = ApiUtil().api
        val context = this
        DynamicServer.load(this)
        val user = object : TestUserUtil(context, api, null) {
            override fun onConfigSelected(config: RemoteConfig) {
                Toast.makeText(this@MainActivity, "${config.name}：${config.values.firstOrNull()}", Toast.LENGTH_SHORT).show()
            }
        }
        user.initOnViewLongClick(buttonUser)
        buttonUser.setOnClickListener {
            user.show()
        }
        val server = object : TestServerUtil(context, api) {
            override fun onConfigSelected(config: RemoteConfig) {
                DynamicServer.updateServer(config, this@MainActivity)
            }
        }
        server.initOnViewLongClick(buttonServer)
        buttonServer.setOnClickListener {
            server.show()
        }
        btClearData.setOnClickListener {
            Runtime.getRuntime().exec("pm clear $packageName")
            Toast.makeText(this, "cleared", Toast.LENGTH_SHORT).show()
        }
    }
}
