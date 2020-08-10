package com.dhy.dynamicserver.demo

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.dhy.retrofitrxutil.BaseErrorHandler
import com.dhy.retrofitrxutil.IError
import org.json.JSONObject
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MyNetErrorHandler : BaseErrorHandler() {
    private val SERVER_ERROR = "服务器请求错误"
    override fun isDebug() = BuildConfig.DEBUG
    private val INVALID_TOKEN = arrayOf(401)//, 9001
    override fun showDialog(context: Context, msg: String): Dialog {
        return AlertDialog.Builder(context).setMessage(msg).show().apply { setCancelable(true) }
    }

    override fun isAuthorizeFailed(activity: Activity, error: IError): Boolean {
        return INVALID_TOKEN.contains(error.code)
    }

    override fun onLogout(context: Context) {

    }

    override fun parseError(e: Throwable): IError {
        e.printStackTrace()
        return when (e) {
            is HttpException -> parseHttpError(e)
            is SocketTimeoutException -> Error(-1, "网络请求超时，请稍后再试")
            is UnknownHostException -> {
                val msg = if (isNetworkConnected()) {
                    "网络请求失败，请稍后再试"
                } else {
                    "暂无网络，请稍后再试"
                }
                Error(-1, msg)
            }
            else -> super.parseError(e)
        }
    }

    private fun isNetworkConnected(): Boolean {
        return true
    }

    private fun parseHttpError(e: HttpException): IError {
        val json = e.response()?.errorBody()?.string()
        val code = e.code()
        var message = if (json?.startsWith("{") == true) {
            JSONObject(json).optString("msg")
        } else SERVER_ERROR

        if (TextUtils.isEmpty(message)) {
            message = SERVER_ERROR
            Log.d("ErrorHandler", json!!)
        }
        return Error(code, message, e.code())
    }

    class Error(private val errorCode: Int, private val msg: String, private val httpCode: Int = 0) : IError {
        override fun httpCode() = httpCode

        override fun getMessage(): String {
            return msg
        }

        override fun getCode(): Int {
            return errorCode
        }
    }
}
