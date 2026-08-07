package com.mcallzbl.updatekit

import android.content.Context
import com.mcallzbl.updatekit.data.DownloadManager
import com.mcallzbl.updatekit.data.UpdateApiService
import com.mcallzbl.updatekit.data.UpdateRepository
import com.mcallzbl.updatekit.model.UpdateInfo
import com.mcallzbl.updatekit.util.ApkInstaller
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * UpdateKit 入口类
 *
 * 使用方式:
 *
 * 1. 在 Application 中初始化:
 *    class MyApp : Application() {
 *        override fun onCreate() {
 *            super.onCreate()
 *            UpdateKit.initialize(this, "https://updater.example.com")
 *        }
 *    }
 *
 * 2. 检查更新:
 *    lifecycleScope.launch {
 *        UpdateKit.checkUpdate("app_id", BuildConfig.VERSION_NAME)
 *            .onSuccess { updateInfo ->
 *                if (updateInfo.hasUpdate) {
 *                    // 在 Composable 上下文中显示对话框:
 *                    UpdateDialog(
 *                        updateInfo = updateInfo,
 *                        downloadManager = UpdateKit.getDownloadManager()!!,
 *                        onDismiss = { },
 *                        onInstall = { file -> ApkInstaller.install(context, file) }
 *                    )
 *                }
 *            }
 *    }
 *
 * 3. 或者使用 Hilt 注入的 Repository:
 *    @Inject lateinit var updateRepository: UpdateRepository
 */
object UpdateKit {

    private const val DEFAULT_TIMEOUT_MS = 30_000L

    private var isInitialized = false
    private var _currentUpdateInfo: UpdateInfo? = null

    private var baseUrl: String = "https://api.example.com/"
    private var okHttpClient: OkHttpClient? = null
    private var retrofit: Retrofit? = null
    private var updateRepository: UpdateRepository? = null
    private var downloadManager: DownloadManager? = null

    /**
     * 初始化 UpdateKit
     * @param context 上下文
     * @param url 更新服务器基础 URL
     */
    fun initialize(context: Context, url: String) {
        if (isInitialized) return

        baseUrl = if (url.endsWith("/")) url else "$url/"

        okHttpClient = OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .readTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .writeTimeout(DEFAULT_TIMEOUT_MS, TimeUnit.MILLISECONDS)
            .followRedirects(true)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient!!)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit!!.create(UpdateApiService::class.java)
        updateRepository = UpdateRepository(apiService)
        downloadManager = DownloadManager(okHttpClient!!)

        isInitialized = true
    }

    /**
     * 检查更新
     * @param appId 应用 ID
     * @param currentVersion 当前版本
     * @return 更新信息结果
     */
    suspend fun checkUpdate(appId: String, currentVersion: String): Result<UpdateInfo> {
        if (!isInitialized) {
            return Result.failure(IllegalStateException("UpdateKit 未初始化，请先调用 initialize()"))
        }

        return updateRepository!!.checkForUpdate(appId, currentVersion)
    }

    /**
     * 获取下载管理器
     */
    fun getDownloadManager(): DownloadManager? = downloadManager

    /**
     * 获取当前更新信息
     */
    fun getCurrentUpdateInfo(): UpdateInfo? = _currentUpdateInfo

    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = isInitialized
}

/**
 * 使用 Kotlin 协程的更新检查扩展
 */
suspend fun UpdateRepository.checkUpdate(
    appId: String,
    currentVersion: String
): Result<UpdateInfo> = checkForUpdate(appId, currentVersion)
