#!/bin/bash

echo "===== 开始直接构建项目 ====="

# 设置Gradle版本
GRADLE_VERSION="7.4.2"
GRADLE_ZIP="gradle-${GRADLE_VERSION}-bin.zip"
DOWNLOAD_URL="https://mirrors.cloud.tencent.com/gradle/gradle-${GRADLE_VERSION}-bin.zip"
LOCAL_GRADLE_DIR="$PWD/local_gradle"

# 检查本地Gradle是否已存在
if [ ! -d "$LOCAL_GRADLE_DIR/gradle-${GRADLE_VERSION}" ]; then
    echo "本地Gradle不存在，正在下载..."
    
    # 创建本地Gradle目录
    mkdir -p $LOCAL_GRADLE_DIR
    cd $LOCAL_GRADLE_DIR
    
    # 下载Gradle
    echo "1. 正在从腾讯云镜像下载Gradle ${GRADLE_VERSION}..."
    curl -L -o $GRADLE_ZIP $DOWNLOAD_URL
    
    # 检查下载是否成功
    if [ ! -f $GRADLE_ZIP ]; then
        echo "下载失败，请检查网络连接"
        exit 1
    fi
    
    # 解压Gradle
    echo "2. 正在解压Gradle..."
    unzip -q $GRADLE_ZIP
    
    # 删除zip文件
    rm $GRADLE_ZIP
    
    # 返回项目根目录
    cd ..
else
    echo "使用已下载的本地Gradle: $LOCAL_GRADLE_DIR/gradle-${GRADLE_VERSION}"
fi

# 设置环境变量
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_251.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH

echo "3. 使用Java版本:"
java -version

echo "4. 直接使用本地Gradle构建项目..."
$LOCAL_GRADLE_DIR/gradle-$GRADLE_VERSION/bin/gradle build --info

echo "===== 构建完成 =====" 