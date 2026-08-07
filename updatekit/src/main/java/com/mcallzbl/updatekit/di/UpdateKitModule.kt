package com.mcallzbl.updatekit.di

import com.mcallzbl.updatekit.data.DownloadManager
import com.mcallzbl.updatekit.data.UpdateApiService
import com.mcallzbl.updatekit.data.UpdateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * UpdateKit Hilt 模块
 */
@Module
@InstallIn(SingletonComponent::class)
object UpdateKitModule {

    private var baseUrl: String = "https://api.example.com/"

    /**
     * 初始化基础 URL
     */
    fun setBaseUrl(url: String) {
        baseUrl = if (url.endsWith("/")) url else "$url/"
    }

    /**
     * 获取当前基础 URL
     */
    fun getBaseUrl(): String = baseUrl

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUpdateApiService(retrofit: Retrofit): UpdateApiService {
        return retrofit.create(UpdateApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUpdateRepository(apiService: UpdateApiService): UpdateRepository {
        return UpdateRepository(apiService)
    }

    @Provides
    @Singleton
    fun provideDownloadManager(okHttpClient: OkHttpClient): DownloadManager {
        return DownloadManager(okHttpClient)
    }
}
