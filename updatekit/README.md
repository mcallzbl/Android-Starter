# UpdateKit

一个轻量、易集成的 Android 应用更新库，支持 Kotlin 协程、Retrofit、Material 3 Compose UI。

## 功能特性

- Kotlin 协程异步检查更新
- Retrofit 网络请求
- Material 3 Compose UI（支持动态配色）
- APK 下载进度显示
- 支持强制更新/可选更新
- Android Q+ Scoped Storage 支持
- FileProvider 自动安装
- Hilt 依赖注入支持
- ProGuard 代码混淆

## 安装

### 方式一：引用本地 AAR

将 `updatekit-release.aar` 发布到您的 Maven 仓库或直接引用：

```kotlin
// settings.gradle.kts
include(":updatekit")
project(":updatekit").projectDir = file("../path/to/updatekit")
```

```kotlin
// app/build.gradle.kts
dependencies {
    implementation(project(":updatekit"))
}
```

### 方式二：发布到 Maven 仓库

```bash
./gradlew :updatekit:publishReleasePublicationToMavenRepository
```

## 快速开始

### 1. 添加权限

在 `AndroidManifest.xml` 中添加必要权限：

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
```

### 2. 初始化

在 `Application` 类中初始化 UpdateKit：

```kotlin
class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        UpdateKit.initialize(this, "https://your-updater-server.com")
    }
}
```

别忘了在 `AndroidManifest.xml` 中注册 Application：

```xml
<application
    android:name=".MyApp"
    ... >
```

### 3. 检查并显示更新

#### 方式一：Compose 项目

由于 `UpdateDialog` 是 `@Composable` 函数，需要在 Composable 上下文中调用：

```kotlin
@Composable
fun MainScreen() {
    var pendingUpdateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    val context = LocalContext.current
    var hasChecked by remember { mutableStateOf(false) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // 检查更新
    LaunchedEffect(hasChecked) {
        if (!hasChecked) {
            hasChecked = true
            lifecycleOwner.lifecycleScope.launch {
                val result = UpdateKit.checkUpdate(
                    appId = "your_app_id",
                    currentVersion = BuildConfig.VERSION_NAME
                )
                result.onSuccess { updateInfo ->
                    if (updateInfo.hasUpdate) {
                        pendingUpdateInfo = updateInfo
                    }
                }.onFailure { error ->
                    Log.e("UpdateKit", "检查更新失败: ${error.message}")
                }
            }
        }
    }

    // 显示更新对话框
    if (pendingUpdateInfo != null) {
        UpdateDialog(
            context = context,
            updateInfo = pendingUpdateInfo!!,
            downloadManager = UpdateKit.getDownloadManager()!!,
            onDismiss = {
                pendingUpdateInfo = null
            },
            onInstall = { file ->
                ApkInstaller.install(context, file)
                pendingUpdateInfo = null
            }
        )
    }

    // 你的 UI
    Scaffold { ... }
}
```

#### 方式二：传统 View 项目（非 Compose）

使用 `UpdateHelper`：

```kotlin
// 在 Activity 或 Fragment 中
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 检查并显示更新对话框
        UpdateHelper.checkAndShowDialog(
            activity = this,
            appId = "your_app_id",
            currentVersion = BuildConfig.VERSION_NAME,
            downloadManager = UpdateKit.getDownloadManager()!!
        )
    }
}
```

或者只显示对话框（不自动检查）：

```kotlin
UpdateHelper.showUpdateDialog(
    activity = this,
    updateInfo = updateInfo,
    downloadManager = UpdateKit.getDownloadManager()!!,
    onDismiss = {
        // 用户点击了"稍后"
    },
    onInstall = { file ->
        // 下载完成，可以在这里安装
        ApkInstaller.install(this, file)
    }
)
```

## 使用 Hilt 依赖注入

如果您使用 Hilt，可以直接注入 `UpdateRepository` 和 `DownloadManager`：

```kotlin
@AndroidEntryPoint
class MainViewModel @Inject constructor(
    private val updateRepository: UpdateRepository
) : ViewModel() {

    fun checkUpdate() {
        viewModelScope.launch {
            val result = updateRepository.checkForUpdate(
                appId = "your_app_id",
                currentVersion = "1.0.0"
            )
            // 处理结果
        }
    }
}
```

## API 参考

### UpdateKit

| 方法 | 说明 |
|------|------|
| `initialize(context, baseUrl)` | 初始化 UpdateKit |
| `checkUpdate(appId, currentVersion)` | 检查更新，返回 `Result<UpdateInfo>` |
| `getDownloadManager()` | 获取下载管理器实例 |
| `getCurrentUpdateInfo()` | 获取当前更新信息 |

### UpdateInfo

| 字段 | 类型 | 说明 |
|------|------|------|
| `version` | String | 最新版本号 |
| `buildNumber` | Int | 构建号 |
| `downloadUrl` | String? | APK 下载地址 |
| `fileSize` | Long | 文件大小（字节） |
| `fileHash` | String? | 文件 MD5/SHA1 校验码 |
| `changelog` | String? | 更新日志 |
| `forceUpdate` | Boolean | 是否强制更新 |
| `hasUpdate` | Boolean | 是否有可用更新 |

### UpdateDialog

```kotlin
@Composable
fun UpdateDialog(
    context: Context,
    updateInfo: UpdateInfo,
    downloadManager: DownloadManager,
    onDismiss: () -> Unit,
    onInstall: (File) -> Unit,
    modifier: Modifier = Modifier
)
```

### UpdateInfoCard

用于非对话框形式展示更新信息的卡片组件：

```kotlin
@Composable
fun UpdateInfoCard(
    updateInfo: UpdateInfo,
    modifier: Modifier = Modifier
)
```

### UpdateHelper（传统 View 项目）

适用于非 Compose 项目：

```kotlin
object UpdateHelper {
    // 检查并显示更新对话框
    fun checkAndShowDialog(
        activity: FragmentActivity,
        appId: String,
        currentVersion: String,
        downloadManager: DownloadManager,
        onDismiss: () -> Unit = {},
        onInstall: (File) -> Unit
    )

    // 显示更新对话框
    fun showUpdateDialog(
        activity: FragmentActivity,
        updateInfo: UpdateInfo,
        downloadManager: DownloadManager,
        onDismiss: () -> Unit = {},
        onInstall: (File) -> Unit
    )
}
```

## 服务器端 API

UpdateKit 期望服务器返回以下格式的 JSON：

```json
{
    "code": 0,
    "message": "success",
    "data": {
        "version": "2.0.0",
        "build_number": 200,
        "download_url": "https://example.com/app-2.0.0.apk",
        "file_size": 15728640,
        "file_hash": "d41d8cd98f00b204e9800998ecf8427e",
        "changelog": "1. 新增功能\n2. 修复 Bug\n3. 性能优化",
        "force_update": false,
        "has_update": true
    }
}
```

### API 端点

```
GET /api/v1/apps/{appId}/latest?version={currentVersion}
```

| 参数 | 说明 |
|------|------|
| `appId` | 应用公开标识 |
| `currentVersion` | 当前版本号（可选） |

## 配置 ProGuard

如果启用 ProGuard，确保保留必要的类：

```proguard
# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keep class com.mcallzbl.updatekit.model.** { *; }

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
```

## License

MIT License
