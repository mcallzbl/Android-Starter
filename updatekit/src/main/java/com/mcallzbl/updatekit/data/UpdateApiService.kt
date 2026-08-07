package com.mcallzbl.updatekit.data

import com.mcallzbl.updatekit.model.ApiResponse
import com.mcallzbl.updatekit.model.UpdateInfo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 更新检查 API 服务接口
 */
interface UpdateApiService {

    @GET("api/v1/apps/{appId}/latest")
    suspend fun getLatestVersion(
        @Path("appId") appId: String,
        @Query("version") currentVersion: String?
    ): ApiResponse<UpdateInfo>
}
