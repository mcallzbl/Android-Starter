package com.mcallzbl.updatekit.data

import com.mcallzbl.updatekit.model.UpdateInfo
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 更新数据仓库
 */
@Singleton
class UpdateRepository @Inject constructor(
    private val apiService: UpdateApiService
) {
    companion object {
        private const val SUCCESS_CODE = 0
    }

    /**
     * 检查更新
     * @param appId 应用 ID
     * @param currentVersion 当前版本
     * @return 更新信息结果
     */
    suspend fun checkForUpdate(
        appId: String,
        currentVersion: String?
    ): Result<UpdateInfo> {
        return try {
            val response = apiService.getLatestVersion(appId, currentVersion)

            if (response.code != SUCCESS_CODE) {
                return Result.failure(Exception(response.message.ifEmpty { "请求失败" }))
            }

            val updateInfo = response.data
                ?: return Result.failure(Exception("应用不存在或暂无可用版本"))

            Result.success(updateInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
