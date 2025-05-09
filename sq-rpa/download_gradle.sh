#!/bin/bash

# 设置Gradle版本
GRADLE_VERSION="7.4.2"
GRADLE_ZIP="gradle-${GRADLE_VERSION}-bin.zip"
DOWNLOAD_URL="https://mirrors.cloud.tencent.com/gradle/gradle-${GRADLE_VERSION}-bin.zip"

# 创建临时目录
TEMP_DIR="/tmp/gradle_download"
mkdir -p $TEMP_DIR
cd $TEMP_DIR

echo "===== 开始手动下载Gradle ${GRADLE_VERSION} ====="

# 下载Gradle
echo "1. 正在从腾讯云镜像下载Gradle ${GRADLE_VERSION}..."
curl -L -o $GRADLE_ZIP $DOWNLOAD_URL

# 检查下载是否成功
if [ ! -f $GRADLE_ZIP ]; then
    echo "下载失败，请检查网络连接或手动下载: $DOWNLOAD_URL"
    exit 1
fi

# 计算用户的Gradle目录
GRADLE_HOME="$HOME/.gradle"
GRADLE_DIST_DIR="$GRADLE_HOME/wrapper/dists/gradle-${GRADLE_VERSION}-bin"
mkdir -p "$GRADLE_DIST_DIR"

# 生成一个唯一的哈希目录名（与Gradle Wrapper相同的逻辑）
HASH_VALUE=$(echo -n "https\://mirrors.cloud.tencent.com/gradle/gradle-${GRADLE_VERSION}-bin.zip" | shasum | cut -d ' ' -f 1)
HASH_DIR="$GRADLE_DIST_DIR/$HASH_VALUE"
mkdir -p "$HASH_DIR"

echo "2. 将Gradle复制到: $HASH_DIR"
cp $GRADLE_ZIP "$HASH_DIR/"

# 解压Gradle
echo "3. 正在解压Gradle..."
cd "$HASH_DIR"
unzip -q $GRADLE_ZIP

# 创建OK文件标记解压完成
touch "$HASH_DIR/gradle-${GRADLE_VERSION}-bin.zip.ok"

echo "4. 清理临时文件..."
rm -rf $TEMP_DIR

echo "===== Gradle ${GRADLE_VERSION} 已成功安装 ====="
echo "现在可以重新启动Android Studio并尝试构建项目" 