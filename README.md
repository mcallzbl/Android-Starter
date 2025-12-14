# Android Quick Start Template

[English](README.md) | [中文](README_zh.md)

This is an Android project template with commonly used configurations and dependencies integrated, helping you quickly start new project development.

## ✨ Features

### 🛠 Development Tools
- ✓ KSP (Kotlin Symbol Processing)
- ✓ Hilt Dependency Injection
- ✓ BuildConfig Enabled
- ✓ compileSdk 36 Support

### 📱 UI Related
- ✓ Jetpack Compose Material3 UI
- ✓ Material Icons Extended
- ✓ High Refresh Rate Support

### 🔧 Infrastructure
- ✓ BaseActivity Encapsulation
- ✓ Timber Logging (Initialized in App class)
- ✓ Network Permission Preconfigured

### 💾 Data Processing
- ✓ Retrofit for Networking
- ✓ Room Database
- ✓ Gson JSON Parsing
- ✓ DataStore for Lightweight Storage

### 🔍 Debug Friendly
- ✓ Debug version with separate package name
- ✓ Timber logging support
- ✓ Full Kotlin Coroutines support

## 🚀 Getting Started

1. Star this project
2. Click "Use this template" in the top right
3. Follow the guide to create your repository
4. Wait a few seconds for automatic package name and project setup
5. Clone to local
6. Start developing your features

## 📦 Integrated Dependencies

- **UI**: Jetpack Compose, Material3
- **Network**: Retrofit
- **Storage**: Room, DataStore
- **Tools**: Timber, Gson
- **Architecture**: Hilt, KSP
- **Async**: Kotlin Coroutines & Flow

## 🔨 Project Structure

```
app/
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com.your.package/
│  │  │     ├─ base/           # Base classes
│  │  │     ├─ data/           # Data layer
│  │  │     ├─ di/             # Dependency injection
│  │  │     ├─ ui/             # UI components
│  │  └─ res/                  # Resources
```

## 📝 Notes

- Debug version has `.debug` package name suffix
- Network permissions preconfigured
- The project structure is simple and mainly for personal use

## 🤝 Contributing

Issues and Pull Requests are welcome to improve this template! 
