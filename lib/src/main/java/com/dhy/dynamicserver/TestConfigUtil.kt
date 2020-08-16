package com.dhy.dynamicserver

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.dhy.dynamicserver.data.*
import com.dhy.retrofitrxutil.ObserverX
import com.dhy.xpreference.XPreferences
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

abstract class TestConfigUtil(
    private val context: Context,
    private val api: TestConfigApi,
    private val configName: String
) : AdapterView.OnItemClickListener {

    private lateinit var configs: List<RemoteConfig>
    private var testConfigSetting: TestConfigSetting = XPreferences.get(context)
    private lateinit var dialog: Dialog
    private lateinit var listView: ListView
    private val itemLayoutId = android.R.layout.simple_list_item_1
    private val isTestUser = configName == "TestUsers"

    companion object {
        var configFormatter: IConfigFormatter = object : IConfigFormatter {}
    }

    fun initShowOnClick(view: View, longClick: Boolean = true) {
        if (longClick) {
            view.setOnLongClickListener {
                show()
                true
            }
        } else {
            view.setOnClickListener {
                show()
            }
        }
    }

    internal open fun loadData(): List<RemoteConfig> {
        return testConfigSetting.datas[configName] ?: emptyList()
    }

    fun show() {
        configs = loadData()
        dialog = Dialog(context)
        listView = ListView(context)
        dialog.setContentView(listView)
        val footer = LayoutInflater.from(context).inflate(itemLayoutId, null) as TextView
        val type = if (isTestUser) "测试用户" else "测试服务器地址"
        footer.text = String.format("更新【%s】数据", type)
        listView.addFooterView(footer, null, true)
        listView.onItemClickListener = this
        updateListView()
        dialog.show()
        dialog.window?.apply {
            val lp = attributes
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            attributes = lp
        }
    }

    protected open fun getConfigFormatter(): IConfigFormatter {
        return configFormatter
    }

    private fun updateListView() {
        if (configs.isEmpty()) onGetDatas(genDefaultConfigs())
        configs.forEach {
            it.configFormatter = getConfigFormatter()
        }
        listView.adapter = object : ArrayAdapter<RemoteConfig>(context, itemLayoutId, configs) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent) as TextView
                setUpConfigItemView(view)
                return view
            }
        }
    }

    protected open fun setUpConfigItemView(tv: TextView) {
        tv.textSize = 12f
    }

    private fun onGetDatas(configs: List<RemoteConfig>) {
        this.configs = configs
        if (configs.isNotEmpty()) updateListView()
    }

    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        if (position == parent.adapter.count - 1) {//last item: refresh datas
            refreshDatas()
        } else {//use current user
            onConfigSelected(configs[position])
            dismissDialog()
        }
    }

    private fun refreshDatas() {
        refreshDatas(context, api, getLcId(), getLcKey(), refreshDatasCallback)
    }

    private val refreshDatasCallback: (List<RemoteConfig>?) -> Unit = { result ->
        if (result != null) {
            testConfigSetting.datas[configName] = result
            XPreferences.put(context, testConfigSetting)
            onGetDatas(result)
        } else {
            val msg = if (isTestUser) "测试用户" else "测试服务器地址"
            AlertDialog.Builder(context)
                .setMessage("暂无${msg}数据")
                .setNegativeButton("关闭", null)
                .setPositiveButton("创建默认数据") { _, _ ->
                    createDefaultConfigs()
                }.show()
        }
    }

    private fun createDefaultConfigs() {
        if (genDefaultConfigs().isNotEmpty()) {
            createDefaultConfigs {
                if (it.isSuccess) refreshDatas()
                val tip = if (it.isSuccess) "创建数据成功" else it.error
                Toast.makeText(context, tip, Toast.LENGTH_LONG).show()
            }
        } else Toast.makeText(context, "请设置 RemoteConfig.dynamicServers", Toast.LENGTH_LONG).show()
    }

    protected open fun onConfigSelected(config: RemoteConfig) {}

    private fun refreshDatas(context: Context, api: TestConfigApi, lcId: String, lcKey: String, callback: (List<RemoteConfig>?) -> Unit) {
        val request = FetchConfigRequest(context.packageName, configName)
        api.fetchTestConfigs(lcId, lcKey, request)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ObserverX<ConfigResponse>(context) {
                override fun onResponse(response: ConfigResponse) {
                    callback(response.configs)
                }
            })
    }

    protected abstract fun genDefaultConfigs(): List<RemoteConfig>

    private fun createDefaultConfigs(callback: (LCResponse) -> Unit) {
        val lcId = getLcId()
        val lcKey = getLcKey()
        val request = CreateConfigRequest(context.packageName, configName)
        request.data = genDefaultConfigs()
        api.createTestConfigs(lcId, lcKey, request).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ObserverX<LCResponse>(context) {
                override fun onResponse(response: LCResponse) {
                    callback(response)
                }
            })
    }

    private fun dismissDialog() {
        if (dialog.isShowing) dialog.dismiss()
    }

    private fun getLcId(): String {
        return context.getString(R.string.X_LC_ID)
    }

    private fun getLcKey(): String {
        return context.getString(R.string.X_LC_KEY)
    }
}
