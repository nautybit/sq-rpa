#!/bin/bash

echo "===== 开始修复Gradle下载问题 ====="

# 设置变量
GRADLE_VERSION="8.5"
GRADLE_DOWNLOAD_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip"
GRADLE_DIR="$HOME/.gradle/wrapper/dists/gradle-${GRADLE_VERSION}-bin"
GRADLE_ZIP="$GRADLE_DIR/gradle-${GRADLE_VERSION}-bin.zip"
HASH_DIR=$(find "$GRADLE_DIR" -type d -name "*" -not -name "gradle-${GRADLE_VERSION}-bin" 2>/dev/null | head -1)

echo "1. 检查Gradle目录..."
mkdir -p "$GRADLE_DIR"

if [ ! -z "$HASH_DIR" ]; then
    echo "找到Gradle哈希目录: $HASH_DIR"
else
    echo "未找到Gradle哈希目录，将创建一个..."
    # 创建一个随机哈希目录
    HASH=$(date | md5sum | cut -d' ' -f1)
    HASH_DIR="$GRADLE_DIR/$HASH"
    mkdir -p "$HASH_DIR"
    echo "创建的哈希目录: $HASH_DIR"
fi

echo "2. 检查是否需要下载Gradle..."
if [ -f "$GRADLE_ZIP" ]; then
    echo "Gradle zip已存在: $GRADLE_ZIP"
else
    echo "正在下载Gradle ${GRADLE_VERSION}..."
    echo "从 $GRADLE_DOWNLOAD_URL 下载到 $GRADLE_ZIP"
    
    # 尝试使用curl下载
    curl -L -o "$GRADLE_ZIP" "$GRADLE_DOWNLOAD_URL"
    
    if [ $? -ne 0 ]; then
        echo "curl下载失败，尝试使用wget..."
        wget -O "$GRADLE_ZIP" "$GRADLE_DOWNLOAD_URL"
        
        if [ $? -ne 0 ]; then
            echo "下载失败！请手动下载Gradle并放置在: $GRADLE_ZIP"
            echo "下载地址: $GRADLE_DOWNLOAD_URL"
            exit 1
        fi
    fi
    
    echo "下载完成!"
fi

echo "3. 检查是否需要解压Gradle..."
if [ -d "$HASH_DIR/gradle-${GRADLE_VERSION}" ]; then
    echo "Gradle已解压: $HASH_DIR/gradle-${GRADLE_VERSION}"
else
    echo "正在解压Gradle到 $HASH_DIR..."
    unzip -q "$GRADLE_ZIP" -d "$HASH_DIR"
    if [ $? -ne 0 ]; then
        echo "解压失败！请手动解压 $GRADLE_ZIP 到 $HASH_DIR"
        exit 1
    fi
    echo "解压完成!"
fi

echo "4. 创建ok文件..."
touch "$HASH_DIR/gradle-${GRADLE_VERSION}-bin.zip.ok"

echo "===== Gradle下载和配置完成 ====="
echo "现在您可以重新打开Android Studio，它应该能够使用已下载的Gradle ${GRADLE_VERSION}"
echo ""
echo "如果仍然遇到问题，请尝试以下步骤:"
echo "1. 删除Android Studio缓存: rm -rf ~/Library/Caches/Google/AndroidStudio*"
echo "2. 在Android Studio中选择File > Invalidate Caches / Restart"
echo "3. 确保您的Java版本兼容Gradle ${GRADLE_VERSION}（需要Java 17或更高版本）" 