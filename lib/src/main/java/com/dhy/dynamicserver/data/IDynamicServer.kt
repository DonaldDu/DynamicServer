package com.dhy.dynamicserver.data

interface IDynamicServer {
    /**
     * server type name: release, test, preRelease
     * */
    val type: String

    /**
     * server name: 'baiDu'
     * */
    val name: String

    /**
     * server address: 'https://www.baidu.com/'
     * */
    val value: String

    val releaseValue: String?
}