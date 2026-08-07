package com.mcallzbl.updatekit.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.mcallzbl.updatekit.R
import com.mcallzbl.updatekit.data.DownloadManager
import com.mcallzbl.updatekit.model.UpdateInfo
import com.mcallzbl.updatekit.util.ApkInstaller
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.io.File

/**
 * 传统 View 系统的更新对话框（支持非 Compose 项目）
 */
class UpdateDialogFragment : DialogFragment() {

    private var updateInfo: UpdateInfo? = null
    private var downloadManager: DownloadManager? = null
    private var onDismissCallback: (() -> Unit)? = null
    private var onInstallCallback: ((File) -> Unit)? = null

    private var progressDialog: ProgressDialog? = null
    private var dialog: AlertDialog? = null
    private var downloadJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val ARG_VERSION = "version"
        private const val ARG_BUILD_NUMBER = "build_number"
        private const val ARG_DOWNLOAD_URL = "download_url"
        private const val ARG_FILE_SIZE = "file_size"
        private const val ARG_FILE_HASH = "file_hash"
        private const val ARG_CHANGELOG = "changelog"
        private const val ARG_FORCE_UPDATE = "force_update"
        private const val ARG_HAS_UPDATE = "has_update"

        fun newInstance(
            updateInfo: UpdateInfo,
            downloadManager: DownloadManager,
            onDismiss: () -> Unit = {},
            onInstall: (File) -> Unit = {}
        ): UpdateDialogFragment {
            return UpdateDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_VERSION, updateInfo.version)
                    putInt(ARG_BUILD_NUMBER, updateInfo.buildNumber)
                    putString(ARG_DOWNLOAD_URL, updateInfo.downloadUrl)
                    putLong(ARG_FILE_SIZE, updateInfo.fileSize)
                    putString(ARG_FILE_HASH, updateInfo.fileHash)
                    putString(ARG_CHANGELOG, updateInfo.changelog)
                    putBoolean(ARG_FORCE_UPDATE, updateInfo.forceUpdate)
                    putBoolean(ARG_HAS_UPDATE, updateInfo.hasUpdate)
                }
                this.updateInfo = updateInfo
                this.downloadManager = downloadManager
                this.onDismissCallback = onDismiss
                this.onInstallCallback = onInstall
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val info = arguments ?: throw IllegalStateException("Arguments not found")
        val version = info.getString(ARG_VERSION) ?: ""
        val fileSize = info.getLong(ARG_FILE_SIZE, 0)
        val changelog = info.getString(ARG_CHANGELOG) ?: ""
        val forceUpdate = info.getBoolean(ARG_FORCE_UPDATE, false)

        val formattedSize = formatFileSize(fileSize)

        val builder = AlertDialog.Builder(requireContext())
            .setTitle("发现新版本 v$version")
            .setMessage("版本: $version\n大小: $formattedSize\n\n更新内容:\n${changelog.replace("\\n", "\n")}")
            .setCancelable(!forceUpdate)

        if (!forceUpdate) {
            builder.setNegativeButton("稍后") { _, _ ->
                onDismissCallback?.invoke()
            }
        }

        builder.setPositiveButton("更新") { _, _ ->
            startDownload()
        }

        dialog = builder.create()
        return dialog!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        downloadJob?.cancel()
        scope.cancel()
    }

    private fun startDownload() {
        val url = updateInfo?.downloadUrl ?: return

        progressDialog = ProgressDialog.show(
            requireContext(),
            "正在下载",
            "正在下载更新...",
            true,
            false
        )

        downloadJob = scope.launch {
            downloadManager?.downloadApk(
                requireContext(),
                url,
                updateInfo?.version ?: ""
            )?.onSuccess { file ->
                progressDialog?.dismiss()
                onInstallCallback?.invoke(file)
            }?.onFailure { error ->
                progressDialog?.dismiss()
                Toast.makeText(requireContext(), "下载失败: ${error.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun formatFileSize(size: Long): String {
        return when {
            size <= 0 -> "未知"
            size < 1024 -> "$size B"
            size < 1024 * 1024 -> String.format("%.2f KB", size / 1024.0)
            size < 1024 * 1024 * 1024 -> String.format("%.2f MB", size / (1024.0 * 1024))
            else -> String.format("%.2f GB", size / (1024.0 * 1024 * 1024))
        }
    }
}

/**
 * 传统 Activity 的更新帮助类
 */
object UpdateHelper {

    /**
     * 检查更新并显示对话框（适用于传统 View 系统）
     */
    fun checkAndShowDialog(
        activity: androidx.fragment.app.FragmentActivity,
        appId: String,
        currentVersion: String,
        downloadManager: DownloadManager,
        onDismiss: () -> Unit = {},
        onInstall: (File) -> Unit = { file ->
            ApkInstaller.install(activity, file)
        }
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = com.mcallzbl.updatekit.UpdateKit.checkUpdate(appId, currentVersion)
            result.onSuccess { updateInfo ->
                if (updateInfo.hasUpdate) {
                    showUpdateDialog(activity, updateInfo, downloadManager, onDismiss, onInstall)
                }
            }.onFailure {
                // 处理错误
            }
        }
    }

    /**
     * 显示更新对话框（适用于传统 View 系统）
     */
    fun showUpdateDialog(
        activity: androidx.fragment.app.FragmentActivity,
        updateInfo: UpdateInfo,
        downloadManager: DownloadManager,
        onDismiss: () -> Unit = {},
        onInstall: (File) -> Unit = { file ->
            ApkInstaller.install(activity, file)
        }
    ) {
        UpdateDialogFragment.newInstance(
            updateInfo = updateInfo,
            downloadManager = downloadManager,
            onDismiss = onDismiss,
            onInstall = onInstall
        ).show(activity.supportFragmentManager, "update_dialog")
    }
}
