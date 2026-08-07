package com.mcallzbl.updatekit.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

/**
 * APK 安装工具类
 */
object ApkInstaller {

    /**
     * 安装 APK
     * @param context 上下文
     * @param apkFile APK 文件
     */
    fun install(context: Context, apkFile: File) {
        if (!apkFile.exists()) {
            throw IllegalArgumentException("APK 文件不存在")
        }

        val intent = Intent(Intent.ACTION_VIEW)
        val apkUri: Uri

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            apkUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.updatekit.fileprovider",
                apkFile
            )
        } else {
            apkUri = Uri.fromFile(apkFile)
        }

        intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(intent)
    }
}
