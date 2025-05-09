#!/bin/bash

# 停止可能正在运行的Gradle守护进程
echo "正在停止Gradle守护进程..."
./gradlew --stop

# 删除项目的.gradle和build目录
echo "删除项目.gradle和build目录..."
rm -rf .gradle
rm -rf build
rm -rf app/build

# 删除Android Studio的缓存
echo "删除Android Studio的缓存..."
rm -rf ~/.gradle/caches/
rm -rf ~/.android/build-cache/

echo "清除完成！"
echo "请重启Android Studio并使用File > Invalidate Caches / Restart... 选项" 