package com.mcallzbl.android_starter

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mcallzbl.android_starter.base.BaseActivity
import com.mcallzbl.android_starter.ui.theme.AndroidstarterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.time.LocalTime

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidstarterTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    WelcomeScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(modifier: Modifier = Modifier) {
    var isVisible by remember { mutableStateOf(false) }
    val greeting = getGreetingByTime()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(Unit) {
        delay(300)
        isVisible = true
    }

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Image(
                    painter = painterResource(id = android.R.drawable.ic_menu_compass),
                    contentDescription = null,
                    modifier = Modifier.size(120.dp)
                )

                Text(
                    text = greeting,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Android 快速启动模板",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                    textAlign = TextAlign.Center
                )

                FeatureSection(
                    title = "🛠 开发工具",
                    features = listOf(
                        "KSP 注解处理器",
                        "Hilt 依赖注入",
                        "BuildConfig 支持",
                    )
                )

                FeatureSection(
                    title = "📱 UI 相关",
                    features = listOf(
                        "Material 3 设计",
                        "Compose UI",
                        "高刷新率支持"
                    )
                )

                FeatureSection(
                    title = "💾 数据处理",
                    features = listOf(
                        "Retrofit 网络",
                        "Room 数据库",
                        "DataStore 存储",
                        "Gson 解析"
                    )
                )

                Text(
                    text = "开始您的项目开发之旅吧！",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.tertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }

}

@Composable
private fun FeatureSection(
    title: String,
    features: List<String>
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            features.forEach { feature ->
                Text(
                    text = "✓ $feature",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getGreetingByTime(): String {
    return when (LocalTime.now().hour) {
        in 0..5 -> "🌙 程序员の夜：代码和星星一样闪亮，但你的黑眼圈更耀眼~"
        in 6..11 -> "☀️ 早安！被子精已被打败，现在请战胜Bug大魔王！[6](@ref)"
        in 12..13 -> "🍱 午间播报：CPU温度≈你的便当温度，该歇会儿啦！"
        in 14..17 -> "😼 下午茶时间：如果代码能喝，现在应该是第8杯咖啡味~[1](@ref)"
        else -> "🌃 晚安全球：'git commit '前记得保存灵魂进度哦[7](@ref)"
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    AndroidstarterTheme {
        WelcomeScreen(
            modifier = Modifier.fillMaxSize()
        )
    }
}