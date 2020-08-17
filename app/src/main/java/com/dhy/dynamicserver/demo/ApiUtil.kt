package com.dhy.dynamicserver.demo

import com.dhy.apiholder.ApiHolderUtil
import com.dhy.apiholder.BaseUrlData
import com.dhy.dynamicserver.data.release

class ApiUtil : ApiHolderUtil<ApiHolder>(ApiHolder::class) {
    companion object {
        val apiUtil = ApiUtil()
        val api = apiUtil.api
    }

    override fun getUserBaseUrl(cls: Class<*>): BaseUrlData {
        val baseUrl = cls.getAnnotation(DynamicBaseUrl::class.java)
        return if (baseUrl != null) BaseUrlData(baseUrl.value.release(), baseUrl.append)
        else {
            throw IllegalArgumentException(String.format("%s: MUST ANNOTATE WITH 'BaseUrl'", cls.name))
        }
    }
}