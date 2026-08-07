package com.mcallzbl.updatekit.data

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * APK 下载管理器
 */
@Singleton
class DownloadManager @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress

    private val _downloadState = MutableStateFlow<DownloadState>(DownloadState.Idle)
    val downloadState: StateFlow<DownloadState> = _downloadState

    companion object {
        private const val BUFFER_SIZE = 8192
    }

    /**
     * 下载 APK 文件
     * @param context 上下文
     * @param downloadUrl 下载地址
     * @param version 版本号
     * @return 下载完成的文件
     */
    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        version: String
    ): Result<File> = withContext(Dispatchers.IO) {
        _downloadState.value = DownloadState.Downloading(0)
        _downloadProgress.value = 0

        val apkFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
            "update_$version.apk"
        )

        try {
            val request = Request.Builder()
                .url(downloadUrl)
                .addHeader("Accept", "application/octet-stream")
                .build()

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // 处理重定向
                    if (response.code in 300..399) {
                        val redirectUrl = response.header("Location")
                        if (redirectUrl != null) {
                            return@withContext downloadApk(context, redirectUrl, version)
                        }
                    }
                    _downloadState.value = DownloadState.Failed("HTTP ${response.code}")
                    return@withContext Result.failure(Exception("HTTP ${response.code}"))
                }

                val body = response.body ?: run {
                    _downloadState.value = DownloadState.Failed("Empty response body")
                    return@withContext Result.failure(Exception("Empty response body"))
                }

                val totalBytes = body.contentLength()
                var downloadedBytes = 0L

                body.byteStream().use { inputStream ->
                    FileOutputStream(apkFile).use { outputStream ->
                        val buffer = ByteArray(BUFFER_SIZE)
                        var bytesRead: Int

                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead

                            if (totalBytes > 0) {
                                val progress = ((downloadedBytes * 100L) / totalBytes).toInt()
                                _downloadProgress.value = progress
                                _downloadState.value = DownloadState.Downloading(progress)
                            }
                        }
                    }
                }
            }

            _downloadState.value = DownloadState.Completed(apkFile)
            Result.success(apkFile)
        } catch (e: Exception) {
            apkFile.delete()
            _downloadState.value = DownloadState.Failed(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    /**
     * 重置下载状态
     */
    fun reset() {
        _downloadProgress.value = 0
        _downloadState.value = DownloadState.Idle
    }

    /**
     * 下载状态
     */
    sealed class DownloadState {
        data object Idle : DownloadState()
        data class Downloading(val progress: Int) : DownloadState()
        data class Completed(val file: File) : DownloadState()
        data class Failed(val error: String) : DownloadState()
    }
}
