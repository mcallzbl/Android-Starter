package com.mcallzbl.updatekit.model

/**
 * API 统一响应包装
 */
data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T?
)
