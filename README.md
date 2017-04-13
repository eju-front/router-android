# Router SDK

Android 端动态路由跳转方案，替代默认的 Activity/Fragment 跳转方式。

通过 `viewmap.xml` 快速替换目标页面来避免更新 APP。

支持跳转方式如下：

1. H5 <-> H5
2. Native <-> Native
3. H5 <-> Native

请参照 [wiki](https://github.com/eju-front/router-android/wiki) 获得更多信息。

## 如何安装

前往 [Releaese](https://github.com/eju-front/router-android/releases) 下载最新的 `libEjuRouter.jar` 并放在应用的 `libs` 目录下


## 开始使用

### ViewMap

`viewmap.josn` 为 Router 的核心文件，作为项目中各个页面跳转的依据。使用时需放在应用的 `assets` 目录下。

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


#### ViewMap 版本说明

APP保留一份默认的 `viewmap` 文件,作为初始化文件，初始版本可在 `Router` 初始化的时候指定。默认初始版本为 `V1.0.0`。**请按照相关规范命名版本号，要么统一v或者V开头，要么直接是1.0.1 的样式，初始化的时候需要指定更新的 url(应该包含应用名称标识和平台标识)。**

```java
Router router = Router.getInstance();
Option option = new Option();
option.defaultVersion = "V1.0.0";//设置默认的版本号
option.request = EjuRequest.newBuilder().url("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android")
                .method(EjuRequest.METHOD_GET).build();//配置检查更新的相关参数（url 应用标识 平台标识）
router.initialize(this, option);
```

#### ViewMap 更新说明

`SDK` 会在应用每次**启动的时候**加载本地最新的配置文件和检测服务器最新的配置文件，如果需要更新，将会自动下载最新的配置文件，**在下一次重新启动应用的时候读取下载的配置文件参数，执行新的逻辑**。

### Router

Router 的导航控制，项目中所有的页面跳转应该由`Router`来进行。使用方法如下：

#### 初始化 Router

自定义 Application，在`onCreate()`方法中初始化`Router`对象。

```java
public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();
    Router router = Router.getInstance();
    Option option = new Option();
    option.defaultVersion = "V1.0.0";
    option.request = EjuRequest.newBuilder()
    .url("http://172.29.32.215:10086/app/checkViewMap?appName=demo&os=android")
    .method(EjuRequest.METHOD_GET).build();
    router.initialize(this, option);
    router.set404ViewMap(DefaultActivity.class.getName());
  }
}
```

#### 跳转到其它 Activity

在需要进行页面跳转的地方调用下列代码：

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

以上方法参数依次为

- Android Context
- 目标画面 ID
- 目标画面类型
- 传递到目标画面的参数，类型为 `Map<String,Object>`
- 传递到目标画面的 RequestCode，无需回传参数时可以省略

#### 获得 Fragment 对象

```java
Fragment fragment = router.findFragmentById(context, " id for fragment", param);
```

以上方法参数一次为

- Android Context
- 需要获得实例的 Fragment 的 ID
- 传递到该实例中的参数，类型为 `Map<String,Object>`

获得到 Fragment 对象后就可以像平时一样使用 FragmentManager 进行跳转。

> 如果想获得 Support 包的 Fragment 对象，可以使用 `findSupportFragmentById()` 方法替代上述的 `findFragmentById()` 方法

#### 从 Framgent 中跳转到其它 Activity

从 Fragment 中跳转到其它 Activity 时也可以按照 `跳转到其它 Activity` 章节所示代码进行编写，但是如果希望回传参数到 Fragment，即响应 Fragment 的 `onActivityResult()` 的话则需要使用以下方法

```java
router.route(fragment, "id for target fragment", ViewMapInfo.TYPE_NATIVE, param, REQUEST_PARAM);
```

以上方法参数依次为

- 当前 Fragment 对象
- 目标画面 ID
- 目标画面类型
- 传递到目标画面的参数，类型为 `Map<String,Object>`
- 传递到目标画面的 RequestCode，无需回传参数时可以省略

#### 跳转到本地/远程 Html

```java
router.route(context, "id for html", ViewMapInfo.TYPE_REMOTE_HTML, param);
```

以上方法参数依次为

- Android Context
- 目标画面 ID
- 目标画面类型
- 传递到目标画面的参数，类型为 `Map<String,Object>`

> 跳转到本地 html 时只需将上述的 `TYPE_REMOTE_HTML` 替换成 `TYPE_LOCAL_HTML` 即可

#### 控制 Html 路由

 在 Html 中跳转到本地时需要使用特殊的 Schema，SDK 默认提供 `eju` 作为跳转的 Schema，也可以使用以下方法在 Router 初始化时进行指定

```java
option.nativeRouteSchema = Arrays.asList("eju", "foo", "bar");
router.initialize(this, option);
```

跳转示例 

```html
<a href="eju://main?x=1&foo=bar&name=樱桃小丸子">跳转到其它画面</a>
```

### 异常处理

#### 配置 404 画面

当应用无法找到所需跳转的资源时，可以通过指定默认的 404 画面，在发生该错误时直接跳转到该画面，通常其值应该为 Activity 的完整类名

```java
router.set404ViewMap("resource for 404";
```

#### 处理其它错误

当 SDK 在处理过程中发生异常或者资源找不到且没有配置 404 画面时，SDK 会尝试调用开发者指定的异常处理函数

以下方法用于创建一个异常处理函数

```java
ExceptionHandler exceptionHandler = new ExceptionHandler() {
  @Override
  public void handle(EjuException e) {
    // 处理异常
  }
};
```

然后可以调用以下方法注册一个异常处理函数

```java
router.register(exceptionHandler);
```

在画面销毁时卸载异常处理函数

```java
router.unregister(exceptionHandler);
```

或者直接卸载所有异常处理函数

```java
router.unregisterAll();
```

## License

See the [LICENSE](LICENSE) file for license rights and limitations.

