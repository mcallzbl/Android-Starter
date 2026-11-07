# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- UpdateKit / Retrofit / Gson 规则（release 必需，详见 updatekit/consumer-rules.pro）---
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

-keep,allowobfuscation interface com.mcallzbl.updatekit.data.UpdateApiService
-keepclassmembers interface com.mcallzbl.updatekit.data.UpdateApiService {
    <methods>;
}

-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn kotlin.Unit
-dontwarn kotlin.coroutines.Continuation

-keep class com.mcallzbl.updatekit.model.** { *; }
-keepclassmembers class com.mcallzbl.updatekit.model.** {
    <fields>;
}

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn javax.annotation.**