package com.dhy.dynamicserver.demo

import com.dhy.apiholder.ApiHolderUtil
import com.dhy.apiholder.IBaseUrl

class ApiUtil : ApiHolderUtil<ApiHolder>(ApiHolder::class) {
    companion object {
        val apiUtil = ApiUtil()
        val api = apiUtil.api
    }

    override fun getUserBaseUrl(cls: Class<*>): IBaseUrl {
        val baseUrl = cls.getAnnotation(DynamicBaseUrl::class.java)
        return if (baseUrl != null) MyBaseUrl(baseUrl)
        else {
            throw IllegalArgumentException(String.format("%s: MUST ANNOTATE WITH 'BaseUrl'", cls.name))
        }
    }

    private class MyBaseUrl(private val dynamic: DynamicBaseUrl) : IBaseUrl {
        override val append: String = dynamic.append

        override val value: String
            get() = dynamic.value.toString()
    }
}