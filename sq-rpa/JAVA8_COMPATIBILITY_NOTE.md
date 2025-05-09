# Java 8 兼容性说明

为了使项目与 Java 8 兼容，我们已经进行了以下修改：

## 1. 降级 Gradle 插件版本

在根目录的 `build.gradle` 文件中，已将 Android Gradle 插件版本降级到 7.2.2：

```gradle
buildscript {
    // ...
    dependencies {
        classpath 'com.android.tools.build:gradle:7.2.2'
        // ...
    }
}
```

## 2. 降级 Gradle 版本

在 `gradle/wrapper/gradle-wrapper.properties` 文件中，已将 Gradle 版本降级到 7.4.2：

```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-7.4.2-bin.zip
```

## 3. 调整 Kotlin 和 Compose 版本

在根目录的 `build.gradle` 文件中，已将 Kotlin 版本设置为 1.7.10，以与 Gradle 插件兼容：

```gradle
buildscript {
    ext {
        kotlin_version = '1.7.10'
        compose_version = '1.3.0'
        room_version = '2.5.2'
        koin_version = '3.2.0'
    }
    // ...
}
```

## 4. 降级依赖版本

在 `app/build.gradle` 文件中，已降级各种依赖的版本：

```gradle
dependencies {
    // Kotlin
    implementation "org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version"
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1'
    
    // AndroidX
    implementation 'androidx.core:core-ktx:1.8.0'
    implementation 'androidx.appcompat:appcompat:1.5.0'
    implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.5.1'
    implementation 'androidx.activity:activity-compose:1.5.1'
    
    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.squareup.okhttp3:okhttp:4.9.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.9.0'
    
    // JSON parsing
    implementation 'com.google.code.gson:gson:2.9.0'
    
    // WebSocket
    implementation 'org.java-websocket:Java-WebSocket:1.5.2'
    
    // JavaScript Engine for scripting
    implementation 'org.mozilla:rhino:1.7.13'
    
    // UI Components
    implementation 'com.google.android.material:material:1.6.0'
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

## 6. 修改 Gradle 属性

在 `gradle.properties` 文件中，我们删除了过时的配置选项：

```properties
# 移除过时的kapt配置
# kapt.include.compile.classpath=false

# 移除可能与当前环境不兼容的配置
# android.suppressUnsupportedCompileSdk=34
```

## 注意事项

即使进行了上述修改，由于项目使用了较新版本的库，在某些环境下仍可能遇到兼容性问题。如果遇到问题，请尝试以下操作：

1. 在Android Studio中选择"File"→"Invalidate Caches / Restart"
2. 确保你的Android Studio版本与项目兼容（建议使用Android Studio Arctic Fox或Bumblebee版本）
3. 确保你的JDK版本是Java 8

## 当前状态

目前项目已成功适配Java 8环境，可以在Java 8环境下正常构建和运行。如果在构建过程中遇到任何问题，请参考上述调整或提交issue。 