# Android 快速启动模板

[English](README_EN.md) | 中文

这是一个集成了常用配置和依赖的 Android 项目模板，帮助您快速开始新项目的开发。

## ✨ 特性

### 🛠 开发工具
- ✓ KSP (Kotlin Symbol Processing) 注解处理器
- ✓ Hilt 依赖注入框架
- ✓ BuildConfig 已启用
- ✓ compileSdk 35 支持最新特性

### 📱 UI 相关
- ✓ Jetpack Compose Material3 UI
- ✓ Material Icons Extended 图标库
- ✓ 高刷新率支持

### 🔧 基础设施
- ✓ BaseActivity 封装
- ✓ Timber 日志工具（已在 App 类中初始化）
- ✓ 网络权限预配置

### 💾 数据处理
- ✓ Retrofit 网络请求
- ✓ Room 数据库
- ✓ Gson JSON解析
- ✓ DataStore 轻量级存储

### 🔍 调试友好
- ✓ Debug 版本使用独立包名，可与正式版共存
- ✓ Timber 日志工具助力调试
- ✓ 完整的 Kotlin 协程支持

## 🚀 开始使用

1. Star此项目
2. 点击右上角Use this template
3. 按照指引构建你的仓库
4. 等待几秒钟，包名和项目会自动切换好
5. 克隆到本地
6. 开始开发您的功能

## 📦 已集成依赖

- **UI**: Jetpack Compose, Material3
- **网络**: Retrofit
- **存储**: Room, DataStore
- **工具**: Timber, Gson
- **架构**: Hilt, KSP
- **异步**: Kotlin Coroutines & Flow

## 🔨 项目结构

```
app/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com.your.package/
│  │  │     ├─ base/           # 基础类
│  │  │     ├─ data/           # 数据层
│  │  │     ├─ di/             # 依赖注入
│  │  │     ├─ ui/             # UI 组件
│  │  └─ res/                  # 资源文件
```

## 📝 注意事项

- Debug 版本包名后缀为 `.debug`
- 已配置网络权限

## 🤝 贡献

欢迎提交 Issue 和 Pull Request 来完善这个模板！