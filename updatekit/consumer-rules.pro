# UpdateKit 作为库被 App 消费时，随 AAR 一起生效的 R8/ProGuard 规则。
# 背景：Retrofit 通过动态代理 + 反射调用接口方法，R8 full mode 会把"没有直接调用者"的
# 接口方法裁掉（默认 retrofit2.pro 里 allowshrinking 允许删除），导致 release 包
# retrofit.create() 阶段 ClassCastException。这里显式 keep 住服务接口与模型。

# Retrofit 需要反射泛型签名与方法注解
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations, AnnotationDefault

# 服务接口：接口本身与方法都不能被裁
-keep,allowobfuscation interface com.mcallzbl.updatekit.data.UpdateApiService
-keepclassmembers interface com.mcallzbl.updatekit.data.UpdateApiService {
    <methods>;
}

# Retrofit 内部实现会被反射访问（保留但不影响混淆）
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**
-dontwarn kotlin.Unit
-dontwarn kotlin.coroutines.Continuation

# Gson 模型：字段名/序列化注解必须保留
-keep class com.mcallzbl.updatekit.model.** { *; }
-keepclassmembers class com.mcallzbl.updatekit.model.** {
    <fields>;
}

# OkHttp / Okio 可选依赖
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn org.codehaus.mojo.animal_sniffer.**
-dontwarn javax.annotation.**
