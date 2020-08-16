# DynamicServer [![](https://jitpack.io/v/DonaldDu/DynamicServer.svg)](https://jitpack.io/#DonaldDu/DynamicServer) [JitPack](https://jitpack.io/#DonaldDu/DynamicServer)
测试员：服务地址临时修改为‘xx’，帮打个包吧！


遇到这种需求，大家一般怎么解决呢？

如果只是一两次，直接修改代码就成。

经常需要调整服务地址的话，最好还是弄个工具。因为多个App中都经常需要切换正式和测试服务，所以写了个工具，方便复用。

有时测试需要不同角色账号来验证功能，所以也实现了内置多个账号的功能，方便快速登录。

但是建议测试账号不要写在代码中，而是通过创建的样例模板在浏览器中创建真实数据。

## 枚举服务地址
通过枚举的方式把已知的服务地址集合到一起。

在构造函数中声明任意多个服务类型（正式，测试，公测，预发布等），并重写toString方法。
> 不能在枚举中声明String类型的其它变量，因为工具中默认所有String类型的变量都为服务类型。

```
enum class DynamicServer(private val release: String, private val test: String) {
    APP_BASE("http://www.app.com", "http://192.168.141.34:8093"),
    USER_CENTER("http://www.user.com", "http://192.168.141.34:8094"),
    ;

    override fun toString(): String {
        return RemoteConfig.serverMap[name] ?: release
    }
}
```
## 读取注解
新建一个注解类，并注解需要动态地址的服务，其它的使用BaseUrl注解。

```
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynamicBaseUrl(
    val value: DynamicServer,
    /**
     * append to value, auto check separator of '/'
     */
    val append: String = ""
)

@DynamicBaseUrl(DynamicServer.APP_BASE)
interface AppApi {
    @GET("login")
    fun login(): Observable<String>
}
```

新建一个工具类，并重写getUserBaseUrl方法
```
class ApiUtil : ApiHolderUtil<ApiHolder>(ApiHolder::class) {
    companion object {
        val apiUtil = ApiUtil()
        val api = apiUtil.api
    }

    override fun getUserBaseUrl(cls: Class<*>): BaseUrlData {
        val baseUrl = cls.getAnnotation(DynamicBaseUrl::class.java)
        return if (baseUrl != null) BaseUrlData(baseUrl.value.toString(), baseUrl.append)
        else {
            throw IllegalArgumentException(String.format("%s: MUST ANNOTATE WITH 'BaseUrl'", cls.name))
        }
    }
}
```
## 初始化
应用启动时，调用 init() 初始化工具。需要更新时，调用updateServer()
```
enum class DynamicServer(private val release: String, private val test: String) {
    companion object {
        fun init(context: Context) {
            if (BuildConfig.DEBUG) {
                RemoteConfig.initDynamicServer(DynamicServer::class.java)
                updateServer(context.getUsingTestServer())
            }
        }

        fun updateServer(config: RemoteConfig, context: Context? = null) {
            config.updateServerMap(context)
            config.toServers().forEach {
                if (it.releaseValue != null) apiUtil.updateApi(it.releaseValue!!, it.value)
            }
        }
    }
}
```

## 手动切换服务
在登录页面中创建TestServerUtil实例并重写onConfigSelected方法

```
    val server = object : TestServerUtil(context, api) {
        override fun onConfigSelected(config: RemoteConfig) {
            DynamicServer.updateServer(config, context)
            tvServers.text = config.toString()
        }
    }
    server.initShowOnClick(tvServers, true)
    tvServers.text = getUsingTestServer().toString()
```
在设置页面显示当前使用的服务地址

```
tvServers.text = getUsingTestServer().toString()
```

## 动态网页地址
如果一些网页地址是动态的，也可以定义到枚举中。

```
    fun showUserPage() {
        val host = DynamicServer.USER_CENTER
        println("$host/userPage")
    }
```
## 远程配置
> 推荐：一直免费用Leancloud的开发版服务，怪不好意思的，推荐大家试试看，真心不错，除了有点小贵外！
以前做了个自家测试版App下载功能，文件流量超了花掉十多块，其它基本没花过钱！

如果需要远程配置功能，需要在 [Leancloud](https://www.leancloud.cn/) 上创建一个（免费）应用，不需要则忽略此项。

找到应用下路径： 设置/应用Keys/Credentials，把AppID和MasterKey配置到应用中。
```
android {
    buildTypes {
        debug {
            resValue "string", "X_LC_ID", "AppID"
            resValue "string", "X_LC_KEY", "MasterKey"
        }
        release {
            resValue "string", "X_LC_ID", ""
            resValue "string", "X_LC_KEY", ""
        }
    }
}
```


如果多个应用都需要，可以把配置信息写到Gradle全局脚本中。

C:\Users\\{用户名}\\.gradle\init.gradle
```
allprojects {
    ext {
        X_LC_ID = "AppID"
        X_LC_KEY = "MasterKey"
        
        INIT_X_LC_ID_KEY = {
            buildTypes {
                debug {
                    resValue "string", "X_LC_ID", findProperty('X_LC_ID') ?: ""
                    resValue "string", "X_LC_KEY", findProperty('X_LC_KEY') ?: ""
                }
                release {
                    resValue "string", "X_LC_ID", ""
                    resValue "string", "X_LC_KEY", ""
                }
            }
        }
    }
}
```
在app项目中如下调用
```
android {
    if (project.hasProperty('INIT_X_LC_ID_KEY')) android.with(INIT_X_LC_ID_KEY)
}
```

## 添加依赖 [![](https://jitpack.io/v/DonaldDu/DynamicServer.svg)](https://jitpack.io/#DonaldDu/DynamicServer) [JitPack](https://jitpack.io/#DonaldDu/DynamicServer)
```
dependencies {
    implementation 'com.github.DonaldDu:DynamicServer:x.x.x'//JitPack version
}
```

## 
到此就基本实现动态服务地址功能了，详细代码请参考demo

---
# 我的其它开源项目
- [ApiHolder多服务端接口适配(超简单)](https://juejin.im/post/6858891439540011015/)
- [RxNet做网络请求，简洁得没法了](https://juejin.im/post/6859008918660579342)
# 最后
开源不易，写文章更不易，劳烦大家给本文点个赞，可以的话，再给个[star](https://github.com/DonaldDu/DynamicServer)，感激不尽
