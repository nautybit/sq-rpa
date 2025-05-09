# Java 8 兼容性说明

为了使项目与 Java 8 兼容，需要进行以下修改：

## 1. 降级 Gradle 插件版本

在根目录的 `build.gradle` 文件中，将 Android Gradle 插件版本降级到 4.2.2：

```gradle
buildscript {
    // ...
    dependencies {
        classpath 'com.android.tools.build:gradle:4.2.2'
        // ...
    }
}
```

## 2. 降级 Gradle 版本

在 `gradle/wrapper/gradle-wrapper.properties` 文件中，将 Gradle 版本降级到 6.7.1：

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-6.7.1-bin.zip
```

## 3. 调整 Kotlin 版本

在根目录的 `build.gradle` 文件中，将 Kotlin 版本设置为 1.6.10，以与 Compose 编译器兼容：

```gradle
buildscript {
    ext {
        kotlin_version = '1.6.10'
        compose_version = '1.1.1'
        // ...
    }
    // ...
}
```

## 4. 降级依赖版本

在 `app/build.gradle` 文件中，降级各种依赖的版本：

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.0"
    
    // AndroidX
    implementation 'androidx.core:core-ktx:1.7.0'
    implementation 'androidx.appcompat:appcompat:1.4.1'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0'
    implementation 'androidx.activity:activity-compose:1.4.0'
    
    // Room 数据库
    def room_version_downgraded = "2.4.0"
    implementation "androidx.room:room-runtime:$room_version_downgraded"
    implementation "androidx.room:room-ktx:$room_version_downgraded"
    kapt "androidx.room:room-compiler:$room_version_downgraded"
    
    // 其他依赖也需要相应降级
    // ...
}
```

## 5. 调整 SDK 版本

在 `app/build.gradle` 文件中，将 compileSdkVersion 和 targetSdkVersion 降级到 31（Android 12）：

```gradle
android {
    compileSdkVersion 31
    defaultConfig {
        // ...
        targetSdkVersion 31
        // ...
    }
    // ...
}
```

## 6. 修改 Gradle 属性

在 `gradle.properties` 文件中，添加以下配置：

```properties
# 关闭 kapt 增量编译以避免兼容性问题
kapt.incremental.apt=false

# 使用旧版编译，以确保兼容性
android.enableD8.desugaring=false

# 使用更低级别的 JVM 目标以确保兼容性
kotlin.jvm.target.validation.mode=warning
```

## 注意事项

即使进行了上述修改，由于项目使用了较新版本的 Compose UI 库，可能仍然会遇到兼容性问题。在这种情况下，可能需要考虑以下选项：

1. 使用 Java 11 或更高版本
2. 降级 Compose 版本到与 Java 8 兼容的版本
3. 移除 Compose UI 组件，改用传统的 XML 布局

## 推荐方案

考虑到项目的复杂性和依赖关系，建议使用 Java 11 或更高版本进行开发，这将避免大多数兼容性问题，并允许使用最新版本的 Android 开发工具和库。 