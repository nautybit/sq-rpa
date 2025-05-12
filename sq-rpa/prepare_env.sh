#!/bin/bash

echo "===== 准备微信自动回复机器人构建环境 ====="

# 检查Java版本
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "检测到Java版本: $java_version"

if [[ $java_version != 1.8* ]]; then
  echo "警告: 当前Java版本不是Java 8，这可能导致构建问题"
  echo "请确保您已在gradle.properties中正确设置org.gradle.java.home指向Java 8路径"
fi

# 清理缓存
echo "清理缓存..."
rm -rf $HOME/.gradle/caches/modules-2/files-2.1/com.android.tools.build
rm -rf .gradle
rm -rf app/build

# 预下载Gradle
echo "预下载Gradle 7.4.2..."
mkdir -p $HOME/.gradle/wrapper/dists/gradle-7.4.2-bin
gradle_zip="$HOME/.gradle/wrapper/dists/gradle-7.4.2-bin/gradle-7.4.2-bin.zip"

if [ ! -f "$gradle_zip" ]; then
  echo "从阿里云镜像下载Gradle 7.4.2..."
  curl -L -o "$gradle_zip" "https://mirrors.aliyun.com/gradle/gradle-7.4.2-bin.zip"
else
  echo "Gradle zip已存在: $gradle_zip"
fi

echo "===== 环境准备完成 ====="
echo "现在您可以打开项目并构建应用了。"
echo "如果遇到问题，请尝试在Android Studio中选择File > Invalidate Caches / Restart..." 