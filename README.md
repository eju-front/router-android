# Router SDK

Android端动态路由跳转方案，替代默认的Activity/Fragment跳转方式。

通过viewmap.xml快速替换目标页面来避免更新APP。

支持跳转方式如下：

1. H5 <-> H5
2. Native <-> Native
3. H5 <-> Native

请参照[wiki](wiki)获得更多信息。



## 如何安装

前往 [Releaese](releases) 下载最新的 `libEjuRouter.jar` 并放在应用的 `libs` 目录下




## 开始使用

##### ViewMap

Router的核心文件，作为项目中各个页面跳转的依据。

文件格式：

```json
{
  "mapinfos": [
    {
      "id": "代表画面的唯一ID",
      "description": "画面的描述信息，仅为记忆方便，不起实际作用",
      "type": 0, //0 代表原生画面，1 代表本地 html，2 代表远程 html，-1 表示没有限制
      "resource": "画面路径，原生为完整类名，html 为路径名"
    }
  ]
}
```

在项目工程构建过程中应该创建一份默认的ViewMap来作为默认的页面跳转依据。

工程文件示例：

```json
{
  "mapinfos": [
    {
      "id": "main",
      "description": "主画面",
      "type": 0,
      "resource": "com.ejurouter.router_sdk.activity.RouterActivity"
    },
    {
      "id": "target",
      "description": "bilili",
      "type": 0,
      "resource": "com.ejurouter.router_sdk.activity.TargetActivity"
    },
    {
      "id": "remote",
      "description": "远程 Html",
      "type": 2,
      "resource": "http://172.29.32.215:8080/app/index.html"
    },
    {
      "id": "local",
      "description": "本地 Html",
      "type": 1,
      "resource": "file:///android_asset/www/index.html"
    },
    {
      "id": "fragment",
      "description": "Fragment",
      "type": 0,
      "resource": "com.ejurouter.router_sdk.fragment.TargetFragment"
    },
    {
      "id": "supportFragment",
      "description": "Support Fragment",
      "type": 0,
      "resource": "com.ejurouter.router_sdk.fragment.SupportTargetFragment"
    }
  ]
}
```


##### ViewMap 版本说明

APP保留一份默认的 `viewmap` 文件,作为初始化文件，初始版本可在 `Router` 初始化的时候指定。默认初始版本为 `V1.0.0`。**请按照相关规范命名版本号，要么统一v或者V开头，要么直接是1.0.1的样式，初始化的时候需要指定更新的 url(应该包含应用名称标识和平台标识)。**



	 Router router = Router.getInstance();
	        Option option = new Option();
	        option.defaultVersion = "V1.0.0";//设置默认的版本号
	        option.request = EjuRequest.newBuilder().url("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android")
	                .method(EjuRequest.METHOD_GET).build();//配置检查更新的相关参数（url 应用标识 平台标识）
	        router.initialize(this, option);

##### ViewMap 更新说明

`SDK` 会在应用每次**启动的时候**加载本地最新的配置文件和检测服务器最新的配置文件，如果需要更新，将会自动下载最新的配置文件，**在下一次重新启动应用的时候读取下载的配置文件参数，执行新的逻辑**。

##### Router

Router的导航控制，项目中所有的页面跳转应该由`Router`来进行。使用方法如下：

1. 自定义Application，在`onCreate()`方法中初始化`Router`对象。

   ```java
   public class App extends Application {
       @Override
       public void onCreate() {
           super.onCreate();
           Router router = Router.getInstance();
           Option option = new Option();
           option.defaultVersion = "V1.0.0";
           option.request = EjuRequest.newBuilder().url("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android")
                   .method(EjuRequest.METHOD_GET).build();
           router.initialize(this, option);
           router.set404ViewMap(DefaultActivity.class.getName());
       }
   }
   ```

2. 在需要进行页面跳转的地方调用下列代码：

   ```java
   Router router = Router.getInstance();

   // 当前上下文
   Context context = this;

   // 传递参数
   Map<String, Object> param = new HashMap<>();
   param.put("name", "Peter");
   param.put("age", 18);
   param.put("male", true);

   // 跳转
   router.route(context, "target", ViewMapInfo.TYPE_NATIVE, param);
   ```




## License

See the [LICENSE](LICENSE) file for license rights and limitations (Apache 2.0).

