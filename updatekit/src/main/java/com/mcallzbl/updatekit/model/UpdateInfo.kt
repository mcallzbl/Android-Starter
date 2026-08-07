package com.mcallzbl.updatekit.model

import com.google.gson.annotations.SerializedName

/**
 * 更新信息数据模型
 */
data class UpdateInfo(
    val version: String,

    @SerializedName("build_number")
    val buildNumber: Int,

    @SerializedName("download_url")
    val downloadUrl: String?,

    @SerializedName("file_size")
    val fileSize: Long,

    @SerializedName("file_hash")
    val fileHash: String?,

    val changelog: String?,

    @SerializedName("force_update")
    val forceUpdate: Boolean,

    @SerializedName("has_update")
    val hasUpdate: Boolean
) {
    /**
     * 格式化文件大小
     */
    fun getFormattedFileSize(): String {
        return when {
            fileSize <= 0 -> "未知"
            fileSize < 1024 -> "$fileSize B"
            fileSize < 1024 * 1024 -> String.format("%.2f KB", fileSize / 1024.0)
            fileSize < 1024 * 1024 * 1024 -> String.format("%.2f MB", fileSize / (1024.0 * 1024))
            else -> String.format("%.2f GB", fileSize / (1024.0 * 1024 * 1024))
        }
    }
}
