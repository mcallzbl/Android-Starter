import java.text.SimpleDateFormat
import java.util.Date

plugins {
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.mcallzbl.android_starter"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.mcallzbl.android_starter"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }

        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        // 配置开启buildConfig构建特性
        buildConfig = true
    }

    // 自定义打包名称
    applicationVariants.all {
        outputs.all {
            val currentDate = SimpleDateFormat("yyyy.MM.dd").format(Date())
            val outputFileName = "${applicationId}_${buildType.name}_v${versionName}_${currentDate}.apk"
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName = outputFileName
        }
    }
}
dependencies {
    //网络请求
    implementation (libs.retrofit)
    implementation (libs.converter.gson)
    // 依赖注入 (Hilt)
    implementation(libs.hilt.android)                // Hilt核心库
    ksp(libs.hilt.android.compiler)                  // Hilt注解处理器(KSP)
    implementation(libs.androidx.hilt.navigation.compose) // Hilt与Compose导航集成

    // 日志工具
    implementation(libs.timber)                      // 高级日志工具

    // 协程
    implementation(libs.kotlinx.coroutines.android)  // Kotlin协程Android支持

    // 数据库 (Room)
    implementation(libs.androidx.room.runtime)      // Room数据库核心库
    implementation(libs.androidx.room.ktx)          // Room的Kotlin扩展和协程支持
    ksp(libs.androidx.room.compiler)                // Room注解处理器(KSP)

    // UI组件
    implementation(libs.androidx.material.icons.extended) // Compose扩展图标库
    implementation(libs.androidx.datastore.preferences) // 轻量级数据存储(替代SharedPreferences)

    // Android核心扩展
    implementation(libs.androidx.core.ktx)           // Android核心KTX扩展
    implementation(libs.androidx.lifecycle.runtime.ktx) // 生命周期KTX扩展

    // Jetpack Compose相关
    implementation(libs.androidx.activity.compose)   // Activity与Compose集成
    implementation(platform(libs.androidx.compose.bom)) // Compose BOM(统一版本管理)
    implementation(libs.androidx.ui)                 // Compose基础UI组件
    implementation(libs.androidx.ui.graphics)        // Compose图形支持
    implementation(libs.androidx.ui.tooling.preview) // Compose预览工具
    implementation(libs.androidx.material3)          // Material Design 3组件

    // 测试依赖
    testImplementation(libs.junit)                  // JUnit单元测试框架
    androidTestImplementation(libs.androidx.junit)  // AndroidX JUnit扩展
    androidTestImplementation(libs.androidx.espresso.core) // Espresso UI测试框架
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose测试BOM
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI测试

    // Debug工具
    debugImplementation(libs.androidx.ui.tooling)    // Compose调试工具
    debugImplementation(libs.androidx.ui.test.manifest) // 测试清单文件
}