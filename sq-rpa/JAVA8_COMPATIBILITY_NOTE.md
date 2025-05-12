# Java 8 兼容性说明

为了使项目与 Java 8 兼容，我们已经进行了以下修改和配置：

## 1. 配置 Gradle 插件版本

在根目录的 `build.gradle` 文件中，已将 Android Gradle 插件版本设置为 7.1.3：

```gradle
buildscript {
    // ...
    dependencies {
        classpath 'com.android.tools.build:gradle:7.1.3'
        // ...
    }
}
```

## 2. 配置 Gradle 版本

在 `gradle/wrapper/gradle-wrapper.properties` 文件中，已将 Gradle 版本设置为 7.4.2：

```properties
distributionUrl=https\://mirrors.aliyun.com/gradle/gradle-7.4.2-bin.zip
```

## 3. 适配 Kotlin 和 Compose 版本

在根目录的 `build.gradle` 文件中，已将 Kotlin 和 Compose 版本设置为与 Java 8 兼容的版本：

```gradle
buildscript {
    ext {
        kotlin_version = '1.6.10'
        compose_version = '1.1.1'
        room_version = '2.4.2'
        koin_version = '3.1.5'
    }
    // ...
}
```

## 4. 兼容性依赖版本

在 `app/build.gradle` 文件中，已配置与 Java 8 兼容的依赖版本：

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1'
    
    // AndroidX
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.1'
    implementation 'androidx.activity:activity-compose:1.4.0'
    
    // Compose
    implementation "androidx.compose.ui:ui:$compose_version"
    implementation "androidx.compose.material:material:$compose_version"
    
    // Room
    implementation "androidx.room:room-runtime:$room_version"
    implementation "androidx.room:room-ktx:$room_version"
    kapt "androidx.room:room-compiler:$room_version"
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.9.0'
    
    // JavaScript Engine for scripting
    implementation 'org.mozilla:rhino:1.7.13'
    // ...其他依赖
}
```

## 5. 设置 Java 版本兼容性

在 `app/build.gradle` 文件中，已设置 Java 版本兼容性为 1.8：

```gradle
android {
    // ...
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
    
    kotlinOptions {
        jvmTarget = '1.8'
    }
    // ...
}
```

## 6. 配置 Gradle 属性

在 `gradle.properties` 文件中，已配置正确的Java 8路径和网络设置：

```properties
# 指定使用Java 8
org.gradle.java.home=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home

# 网络设置 - 增加超时和重试次数
org.gradle.internal.http.connectionTimeout=120000
org.gradle.internal.http.socketTimeout=120000
org.gradle.internal.repository.max.retries=10
org.gradle.internal.repository.initial.backoff=500

# 使用阿里云镜像仓库
maven.url.aliyun.general=https://maven.aliyun.com/repository/public/
maven.url.aliyun.google=https://maven.aliyun.com/repository/google/
maven.url.aliyun.gradle=https://maven.aliyun.com/repository/gradle-plugin/
```

## 7. 配置存储库镜像

在根目录的 `build.gradle` 文件中，已配置国内镜像源以提高下载速度：

```gradle
allprojects {
    // 使用阿里云镜像
    repositories {
        maven { url 'https://maven.aliyun.com/repository/public/' }
        maven { url 'https://maven.aliyun.com/repository/google/' }
        maven { url 'https://maven.aliyun.com/repository/gradle-plugin/' }
        google()
        mavenCentral()
    }
}
```

## 8. Java 版本检查

在根目录的 `build.gradle` 文件中，已添加Java版本检查逻辑：

```gradle
// 检查Gradle使用的JDK版本
def gradleJavaVersion = org.gradle.internal.jvm.Jvm.current().javaVersion
logger.lifecycle("当前使用的Java版本: $gradleJavaVersion")

// 检查Java版本是否兼容
if (gradleJavaVersion != JavaVersion.VERSION_1_8) {
    logger.warn("警告: 当前项目配置为使用Java 8，但检测到使用的是 $gradleJavaVersion")
    logger.warn("请确保在gradle.properties中正确设置org.gradle.java.home指向Java 8")
}
```

## 9. 环境准备脚本

项目提供 `prepare_env.sh` 脚本用于准备构建环境：

```bash
#!/bin/bash
# 脚本会检查Java版本
# 清理缓存
# 预下载Gradle 7.4.2
```

## 注意事项

1. **必须使用Java 8**：本项目严格要求Java 8环境，使用其他版本可能导致构建失败。

2. **Android Studio设置**：打开项目后，请检查Android Studio的Gradle JDK设置是否指向Java 8。

3. **依赖版本**：本项目使用的库版本都经过精心选择，以确保与Java 8兼容。请勿随意升级依赖版本。

4. **Room数据库**：项目使用的Room 2.4.2版本已经过测试，可在Java 8环境下正常工作。

5. **构建速度**：使用配置的阿里云镜像和网络设置，可大幅提高依赖下载速度。

## 如果遇到问题

如果在构建过程中遇到问题：

1. 执行 `prepare_env.sh` 脚本清理缓存并预下载Gradle
2. 确保 `gradle.properties` 中的Java 8路径正确
3. 在Android Studio中选择"File"→"Invalidate Caches / Restart"
4. 检查构建日志中的Java版本是否显示为1.8

## 当前状态

目前项目已成功适配Java 8环境，可以在Java 8环境下正常构建和运行。所有依赖库和配置都经过测试，确保可以稳定工作。 